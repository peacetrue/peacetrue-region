package com.github.peacetrue.region;

import com.github.peacetrue.spring.util.BeanUtils;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

/**
 * @author : xiayx
 * @since : 2020-05-22 16:43
 **/
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
        R2dbcAutoConfiguration.class,
        R2dbcDataAutoConfiguration.class,
        R2dbcRepositoriesAutoConfiguration.class,
        R2dbcTransactionManagerAutoConfiguration.class,
        RegionServiceImplTestConfiguration.class
})
@ActiveProfiles("region-service-test")
@EnableAutoConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegionServiceImplTest {

    @Autowired
    private RegionServiceImpl regionService;
    private final EasyRandom easyRandom = new EasyRandom();
    private static RegionVO vo;

    //TODO rollback invalid
    @Test
    @Rollback
    @Order(10)
    void test001Add() {
        RegionAdd params = easyRandom.nextObject(RegionAdd.class);
        regionService.add(params)
                .as(StepVerifier::create)
                .assertNext(data -> {
                    Assertions.assertEquals(data.getCode(), params.getCode());
                    vo = data;
                })
                .verifyComplete();
    }

    @Test
    @Order(20)
    void test002Query() {
        RegionQuery params = BeanUtils.map(vo, RegionQuery.class);
        regionService.query(params, PageRequest.of(0, 10))
                .as(StepVerifier::create)
                .assertNext(page -> Assertions.assertEquals(1, page.getTotalElements()))
                .verifyComplete();
    }

    @Test
    @Order(30)
    void test003Query() {
        RegionQuery params = BeanUtils.map(vo, RegionQuery.class);
        regionService.query(params)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @Order(40)
    void test004get() {
        RegionGet params = BeanUtils.map(vo, RegionGet.class);
        regionService.get(params)
                .as(StepVerifier::create)
                .assertNext(item -> Assertions.assertEquals(vo.getCode(), item.getCode()))
                .verifyComplete();
    }

    @Test
    @Order(50)
    void test005modify() {
        RegionModify params = easyRandom.nextObject(RegionModify.class);
        params.setId(vo.getId());
        regionService.modify(params)
                .as(StepVerifier::create)
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    @Order(60)
    void test006delete() {
        RegionDelete params = easyRandom.nextObject(RegionDelete.class);
        params.setId(vo.getId());
        regionService.delete(params)
                .as(StepVerifier::create)
                .expectNext(1)
                .verifyComplete();
    }
}
