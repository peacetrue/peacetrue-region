package com.github.peacetrue.region;

import com.github.peacetrue.spring.data.relational.core.query.CriteriaUtils;
import com.github.peacetrue.spring.data.relational.core.query.QueryUtils;
import com.github.peacetrue.spring.data.relational.core.query.UpdateUtils;
import com.github.peacetrue.spring.util.BeanUtils;
import com.github.peacetrue.util.StreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.data.domain.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * @author : xiayx
 * @since : 2020-05-20 07:16
 **/
@Slf4j
@Service
public class RegionServiceImpl implements RegionService {

    @Autowired
    private R2dbcEntityTemplate entityTemplate;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public static Criteria buildCriteria(RegionQuery params) {
        Mono.empty()
                .switchIfEmpty(Mono.just("1"))
                .map(value -> value + "1")
                .doOnNext(System.out::println);
        /*
        Criteria criteria = Criteria.empty();
        if (params.getId() != null) {
            criteria = criteria.and(Criteria.where("id").in(params.getId()));
        }
        if (params.getCode() != null) {
            criteria = criteria.and(Criteria.where("code").like("%" + params.getCode() + "%"));
        }
        */
        /*
        List<Criteria> criteriaList = new LinkedList<>();
        if (params.getId() != null) {
            criteriaList.add(Criteria.where("id").in(params.getId()));
        }
        if (params.getCode() != null) {
            criteriaList.add(Criteria.where("code").like("%" + params.getCode() + "%"));
        }
        Criteria criteria = Criteria.from(criteriaList);
        */
        return CriteriaUtils.and(
                CriteriaUtils.nullableCriteria(CriteriaUtils.smartIn("id"), params::getId),
                CriteriaUtils.nullableCriteria(Criteria.where("code")::like, value -> "%" + value + "%", params::getCode),
                CriteriaUtils.nullableCriteria(Criteria.where("name")::like, value -> "%" + value + "%", params::getName),
                CriteriaUtils.nullableCriteria(Criteria.where("parentId")::is, params::getParentId),
                CriteriaUtils.nullableCriteria(Criteria.where("createdTime")::greaterThanOrEquals, params.getCreatedTime()::getLowerBound),
                CriteriaUtils.nullableCriteria(Criteria.where("createdTime")::lessThanOrEquals, value -> DateUtils.ceiling(value, Calendar.SECOND), params.getCreatedTime()::getUpperBound)
        );
    }

    @Override
    @Transactional
    public Mono<RegionVO> add(RegionAdd params) {
        log.info("新增信息[{}]", params);
        Region region = BeanUtils.map(params, Region.class);
        return entityTemplate.insert(region)
                .map(item -> BeanUtils.map(item, RegionVO.class))
                .doOnNext(item -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(item, params)));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Page<RegionVO>> query(@Nullable RegionQuery params, @Nullable Pageable pageable, String... projection) {
        log.info("分页查询信息[{}]", params);
        if (params == null) params = RegionQuery.DEFAULT;
        Pageable finalPageable = pageable == null ? PageRequest.of(0, 10) : pageable;
        Criteria where = buildCriteria(params);

        return entityTemplate.count(Query.query(where), Region.class)
                .flatMap(total -> total == 0L ? Mono.empty() : Mono.just(total))
                .<Page<RegionVO>>flatMap(total -> {
                    Query query = Query.query(where).with(finalPageable).sort(finalPageable.getSortOr(Sort.by("code")));
                    return entityTemplate.select(query, Region.class)
                            .map(item -> BeanUtils.map(item, RegionVO.class))
                            .reduce(new ArrayList<>(), StreamUtils.reduceToCollection())
                            .map(item -> new PageImpl<>(item, finalPageable, total));
                })
                .switchIfEmpty(Mono.just(new PageImpl<>(Collections.emptyList())));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RegionVO> query(@Nullable RegionQuery params, @Nullable Sort sort, String... projection) {
        log.info("全量查询信息[{}]", params);
        if (params == null) params = RegionQuery.DEFAULT;
        if (sort == null) sort = Sort.by("code").descending();
        Criteria where = buildCriteria(params);
        Query query = Query.query(where).sort(sort).limit(100);
        return entityTemplate.select(query, Region.class)
                .map(item -> BeanUtils.map(item, RegionVO.class));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RegionVO> get(RegionGet params, String... projection) {
        log.info("获取信息[{}]", params);
        Criteria where = CriteriaUtils.and(
                CriteriaUtils.nullableId(params::getId),
                CriteriaUtils.nullableCriteria(Criteria.where("code")::is, params::getCode)
        );
        return entityTemplate.selectOne(Query.query(where), Region.class)
                .map(item -> BeanUtils.map(item, RegionVO.class));
    }

    @Override
    @Transactional
    public Mono<Integer> modify(RegionModify params) {
        log.info("修改信息[{}]", params);
        Query idQuery = QueryUtils.id(params::getId);
        return entityTemplate.selectOne(idQuery, Region.class)
                .map(item -> BeanUtils.map(item, RegionVO.class))
                .zipWhen(region -> entityTemplate.update(idQuery, UpdateUtils.selectiveUpdateFromExample(BeanUtils.map(params, Region.class)), Region.class))
                .doOnNext(tuple2 -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(tuple2.getT1(), params)))
                .map(Tuple2::getT2)
                .switchIfEmpty(Mono.just(0));
    }

    @Override
    @Transactional
    public Mono<Integer> delete(RegionDelete params) {
        log.info("删除信息[{}]", params);
        Query idQuery = QueryUtils.id(params::getId);
        return entityTemplate.selectOne(idQuery, Region.class)
                .map(item -> BeanUtils.map(item, RegionVO.class))
                .zipWhen(region -> entityTemplate.delete(idQuery, Region.class))
                .doOnNext(tuple2 -> eventPublisher.publishEvent(new PayloadApplicationEvent<>(tuple2.getT1(), params)))
                .map(Tuple2::getT2)
                .switchIfEmpty(Mono.just(0));
    }

}
