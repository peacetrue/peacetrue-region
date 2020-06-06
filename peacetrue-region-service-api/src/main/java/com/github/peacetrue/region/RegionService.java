package com.github.peacetrue.region;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;

/**
 * 地区服务接口
 *
 * @author xiayx
 */
public interface RegionService {

    /** 新增 */
    Mono<RegionVO> add(RegionAdd params);

    /** 分页查询 */
    Mono<Page<RegionVO>> query(@Nullable RegionQuery params, @Nullable Pageable pageable, String... projection);

    /** 全量查询 */
    Flux<RegionVO> query(@Nullable RegionQuery params, @Nullable Sort sort, String... projection);

    /** 全量查询 */
    default Flux<RegionVO> query(@Nullable RegionQuery params, String... projection) {
        return this.query(params, (Sort) null, projection);
    }

    /** 获取 */
    Mono<RegionVO> get(RegionGet params, String... projection);

    /** 修改 */
    Mono<Integer> modify(RegionModify params);

    /** 删除 */
    Mono<Integer> delete(RegionDelete params);
}
