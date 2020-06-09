package com.github.peacetrue.region;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.annotation.Nullable;
import java.util.List;

@FeignClient(name = "peacetrue-region")
public interface RegionServiceClient {

    @PostMapping(value = "/regions")
    RegionVO add(RegionAdd params);

    @GetMapping(value = "/regions", params = "page")
    Page<RegionVO> query(@Nullable @SpringQueryMap RegionQuery params, Pageable pageable, @SpringQueryMap String... projection);

    @GetMapping(value = "/regions", params = "sort")
    List<RegionVO> query(@SpringQueryMap RegionQuery params, Sort sort, @SpringQueryMap String... projection);

    @GetMapping(value = "/regions")
    List<RegionVO> query(@SpringQueryMap RegionQuery params, @SpringQueryMap String... projection);

    @GetMapping(value = "/regions/get")
    RegionVO get(@SpringQueryMap RegionGet params, @SpringQueryMap String... projection);

    @PutMapping(value = "/regions")
    Integer modify(RegionModify params);

    @GetMapping(value = "/regions/delete")
    Integer delete(@SpringQueryMap RegionDelete params);
}
