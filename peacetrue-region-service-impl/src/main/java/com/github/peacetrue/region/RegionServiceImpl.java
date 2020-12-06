package com.github.peacetrue.region;

import com.github.peacetrue.core.Operators;
import com.github.peacetrue.core.Range;
import com.github.peacetrue.result.ResultType;
import com.github.peacetrue.result.exception.DataResultException;
import com.github.peacetrue.spring.data.relational.core.query.CriteriaUtils;
import com.github.peacetrue.spring.data.relational.core.query.UpdateUtils;
import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.util.DateUtils;
import com.github.peacetrue.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.data.domain.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

/**
 * 地区服务实现
 *
 * @author xiayx
 */
@Slf4j
@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private R2dbcEntityTemplate entityTemplate;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public static Criteria buildCriteria(RegionQuery params) {
        return CriteriaUtils.and(
                CriteriaUtils.nullableCriteria(CriteriaUtils.smartIn("id"), params::getId),
                CriteriaUtils.nullableCriteria(Criteria.where("code")::like, value -> value + "%", params::getCode),
                CriteriaUtils.nullableCriteria(Criteria.where("name")::like, value -> "%" + value + "%", params::getName),
                CriteriaUtils.nullableCriteria(Criteria.where("parentId")::is, params::getParentId),
                CriteriaUtils.nullableCriteria(Criteria.where("level")::is, params::getLevel),
                //根节点是第1级，其他节点都大于1，所有查询排除根节点
                ArrayUtils.isEmpty(params.getId()) ? Criteria.where("level").greaterThan(1) : null,
                CriteriaUtils.nullableCriteria(Criteria.where("creatorId")::is, params::getCreatorId),
                CriteriaUtils.nullableCriteria(Criteria.where("createdTime")::greaterThanOrEquals, params.getCreatedTime()::getLowerBound),
                CriteriaUtils.nullableCriteria(Criteria.where("createdTime")::lessThan, DateUtils.DATE_CELL_EXCLUDE, params.getCreatedTime()::getUpperBound),
                CriteriaUtils.nullableCriteria(Criteria.where("modifierId")::is, params::getModifierId),
                CriteriaUtils.nullableCriteria(Criteria.where("modifiedTime")::greaterThanOrEquals, params.getModifiedTime()::getLowerBound),
                CriteriaUtils.nullableCriteria(Criteria.where("modifiedTime")::lessThan, DateUtils.DATE_CELL_EXCLUDE, params.getModifiedTime()::getUpperBound)
        );
    }

    @Override
    @Transactional
    public Mono<RegionVO> add(RegionAdd params) {
        log.info("新增地区信息[{}]", params);
        if (params.getParentId() == null) params.setParentId(ROOT_ID);
        Region entity = BeanUtils.map(params, Region.class);
        entity.setLeaf(true);
        entity.setCreatorId(params.getOperatorId());
        entity.setCreatedTime(LocalDateTime.now());
        entity.setModifierId(entity.getCreatorId());
        entity.setModifiedTime(entity.getCreatedTime());
        return this.get(Operators.setOperator(params, new RegionGet(params.getParentId())))
                .switchIfEmpty(Mono.error(new DataResultException(
                        ResultType.failure.name(), String.format("父节点'%s'不存在", params.getParentId()), params.getParentId()
                )))
                .zipWhen(parent -> {
                    Query parentIdQuery = Query.query(Criteria.where("parentId").is(parent.getId()));
                    return this.entityTemplate.count(parentIdQuery, Region.class);
                })
                .flatMap(tuple2 -> {
                    //TODO update parent leaf
                    entity.setLevel(tuple2.getT1().getLevel() + 1);
                    entity.setSerialNumber(tuple2.getT2().intValue() + 1);
                    return entityTemplate.insert(entity);
                })
                .map(item -> BeanUtils.map(item, RegionVO.class))
                .doOnNext(item -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(item, params)));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<RegionVO>> query(@Nullable RegionQuery params, @Nullable Pageable pageable, String... projection) {
        log.info("分页查询地区信息[{}]", params);
        if (params == null) params = RegionQuery.DEFAULT;
        if (params.getCreatedTime() == null) params.setCreatedTime(Range.LocalDateTime.DEFAULT);
        if (params.getModifiedTime() == null) params.setModifiedTime(Range.LocalDateTime.DEFAULT);
        Pageable finalPageable = pageable == null ? PageRequest.of(0, 10) : pageable;
        Criteria where = buildCriteria(params);
        return entityTemplate.count(Query.query(where), Region.class)
                .flatMap(total -> total == 0L ? Mono.empty() : Mono.just(total))
                .<Page<RegionVO>>flatMap(total -> {
                    Query query = Query.query(where).with(finalPageable).sort(finalPageable.getSortOr(Sort.by("createdTime").descending()));
                    return entityTemplate.select(query, Region.class)
                            .map(item -> BeanUtils.map(item, RegionVO.class))
                            .flatMap(fetchChildren(projection))
                            .reduce(new ArrayList<>(), StreamUtils.reduceToCollection())
                            .map(item -> new PageImpl<>(item, finalPageable, total));
                })
                .switchIfEmpty(Mono.just(new PageImpl<>(Collections.emptyList(), finalPageable, 0L)));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RegionVO> query(@Nullable RegionQuery params, @Nullable Sort sort, String... projection) {
        log.info("全量查询地区信息[{}]", params);
        if (params == null) params = RegionQuery.DEFAULT;
        if (params.getCreatedTime() == null) params.setCreatedTime(Range.LocalDateTime.DEFAULT);
        if (params.getModifiedTime() == null) params.setModifiedTime(Range.LocalDateTime.DEFAULT);
        if (sort == null) sort = Sort.by("serialNumber");
        Criteria where = buildCriteria(params);
        Query query = Query.query(where).sort(sort).limit(100);
        return entityTemplate.select(query, Region.class)
                .map(item -> BeanUtils.map(item, RegionVO.class))
                .flatMap(fetchChildren(projection));
    }

    private static boolean hasChildren(String... projection) {
        return ArrayUtils.contains(projection, "children");
    }

    private Function<RegionVO, Mono<RegionVO>> fetchChildren(String[] projection) {
        return item -> {
            if (hasChildren(projection)
                    && Boolean.FALSE.equals(item.getLeaf())
                    && !item.getId().equals(item.getParentId())) {
                RegionQuery regionQuery = new RegionQuery();
                regionQuery.setParentId(item.getId());
                return this.query(regionQuery, Sort.by("serialNumber"), projection)
                        .reduce(new ArrayList<>(), StreamUtils.reduceToCollection())
                        .map(children -> {
                            item.setChildren(children);
                            return item;
                        });
            }
            return Mono.just(item);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RegionVO> get(RegionGet params, String... projection) {
        log.info("获取地区信息[{}]", params);
        Criteria where = CriteriaUtils.and(
                Criteria.where("id").is(params.getId()),
                CriteriaUtils.nullableCriteria(Criteria.where("leaf")::is, params::getLeaf)
        );
        return entityTemplate.selectOne(Query.query(where), Region.class)
                .map(item -> BeanUtils.map(item, RegionVO.class))
                .flatMap(fetchChildren(projection));
    }

    @Override
    @Transactional
    public Mono<Integer> modify(RegionModify params) {
        log.info("修改地区信息[{}]", params);
        Criteria where = Criteria.where("id").is(params.getId());
        Query idQuery = Query.query(where);
        return entityTemplate.selectOne(idQuery, Region.class)
                .map(item -> BeanUtils.map(item, RegionVO.class))
                .zipWhen(entity -> {
                    Region modify = BeanUtils.map(params, Region.class);
                    modify.setModifierId(params.getOperatorId());
                    modify.setModifiedTime(LocalDateTime.now());
                    Update update = UpdateUtils.selectiveUpdateFromExample(modify);
                    return entityTemplate.update(idQuery, update, Region.class);
                })
                .doOnNext(tuple2 -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(tuple2.getT1(), params)))
                .map(Tuple2::getT2)
                .switchIfEmpty(Mono.just(0));
    }

    @Override
    @Transactional
    public Mono<Integer> delete(RegionDelete params) {
        log.info("删除地区信息[{}]", params);
        Criteria where = Criteria.where("id").is(params.getId());
        Query idQuery = Query.query(where);
        return entityTemplate.selectOne(idQuery, Region.class)
                .map(item -> BeanUtils.map(item, RegionVO.class))
                .zipWhen(region -> entityTemplate.delete(idQuery, Region.class))
                .doOnNext(tuple2 -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(tuple2.getT1(), params)))
                .map(Tuple2::getT2)
                .switchIfEmpty(Mono.just(0));
    }

}
