package com.github.peacetrue.region;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * @author xiayx
 */
@Slf4j
@RestController
@RequestMapping(value = "/regions")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<RegionVO> addByForm(RegionAdd params) {
        log.info("新增信息(请求方法+表单参数)[{}]", params);
        return regionService.add(params);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RegionVO> addByJson(@RequestBody RegionAdd params) {
        log.info("新增信息(请求方法+JSON参数)[{}]", params);
        return regionService.add(params);
    }

    @GetMapping(params = "page")
    public Mono<Page<RegionVO>> query(RegionQuery params, Pageable pageable, String... projection) {
        log.info("分页查询信息(请求方法+参数变量)[{}]", params);
        return regionService.query(params, pageable, projection);
    }

    @GetMapping
    public Flux<RegionVO> query(RegionQuery params, Sort sort, String... projection) {
        log.info("全量查询信息(请求方法+参数变量)[{}]", params);
        return regionService.query(params, sort, projection);
    }

    @GetMapping("/{id}")
    public Mono<RegionVO> getByUrlPathVariable(@PathVariable Long id, String... projection) {
        log.info("获取信息(请求方法+路径变量)详情[{}]", id);
        return regionService.get(new RegionGet(id), projection);
    }

    @RequestMapping("/get")
    public Mono<RegionVO> getByPath(RegionGet params, String... projection) {
        log.info("获取信息(请求路径+参数变量)详情[{}]", params);
        return regionService.get(params, projection);
    }

    @PutMapping(value = {"", "/*"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<Integer> modifyByForm(RegionModify params) {
        log.info("修改信息(请求方法+表单参数)[{}]", params);
        return regionService.modify(params);
    }

    @PutMapping(value = {"", "/*"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Integer> modifyByJson(@RequestBody RegionModify params) {
        log.info("修改信息(请求方法+JSON参数)[{}]", params);
        return regionService.modify(params);
    }

    @DeleteMapping("/{id}")
    public Mono<Integer> deleteByUrlPathVariable(@PathVariable Long id) {
        log.info("删除信息(请求方法+URL路径变量)[{}]", id);
        return regionService.delete(new RegionDelete(id));
    }

    @DeleteMapping(params = "id")
    public Mono<Integer> deleteByUrlParamVariable(RegionDelete params) {
        log.info("删除信息(请求方法+URL参数变量)[{}]", params);
        return regionService.delete(params);
    }

    @RequestMapping(path = "/delete")
    public Mono<Integer> deleteByPath(RegionDelete params) {
        log.info("删除信息(请求路径+URL参数变量)[{}]", params);
        return regionService.delete(params);
    }

}
