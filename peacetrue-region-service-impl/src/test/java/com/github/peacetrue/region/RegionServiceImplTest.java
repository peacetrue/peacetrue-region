package com.github.peacetrue.region;

import com.github.peacetrue.spring.util.BeanUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import reactor.test.StepVerifier;

/**
 * @author : xiayx
 * @since : 2020-05-22 16:43
 **/
@SpringBootTest(classes = RegionServiceImplTestAutoConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegionServiceImplTest {

    @Autowired
    private RegionServiceImpl regionService;

    @Test
    @Order(10)
    void add() {
        regionService.add(RegionServiceImplTestAutoConfiguration.ADD)
                .as(StepVerifier::create)
                .assertNext(data -> {
                    Assertions.assertEquals(data.getCode(), RegionServiceImplTestAutoConfiguration.ADD.getCode());
                    RegionServiceImplTestAutoConfiguration.vo = data;
                })
                .verifyComplete();
    }

    @Test
    @Order(20)
    void queryForPage() {
        RegionQuery params = BeanUtils.map(RegionServiceImplTestAutoConfiguration.vo, RegionQuery.class);
        regionService.query(params, PageRequest.of(0, 10))
                .as(StepVerifier::create)
                .assertNext(page -> Assertions.assertEquals(1, page.getTotalElements()))
                .verifyComplete();
    }

    @Test
    @Order(30)
    void queryForList() {
        RegionQuery params = BeanUtils.map(RegionServiceImplTestAutoConfiguration.vo, RegionQuery.class);
        regionService.query(params)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    @Order(40)
    void get() {
        RegionGet params = BeanUtils.map(RegionServiceImplTestAutoConfiguration.vo, RegionGet.class);
        regionService.get(params)
                .as(StepVerifier::create)
                .assertNext(item -> Assertions.assertEquals(RegionServiceImplTestAutoConfiguration.vo.getCode(), item.getCode()))
                .verifyComplete();
    }

    @Test
    @Order(50)
    void modify() {
        RegionModify params = RegionServiceImplTestAutoConfiguration.MODIFY;
        params.setId(RegionServiceImplTestAutoConfiguration.vo.getId());
        regionService.modify(params)
                .as(StepVerifier::create)
                .expectNext(1)
                .verifyComplete();
    }

    @Test
    @Order(60)
    void delete() {
        RegionDelete params = RegionServiceImplTestAutoConfiguration.EASY_RANDOM.nextObject(RegionDelete.class);
        params.setId(RegionServiceImplTestAutoConfiguration.vo.getId());
        regionService.delete(params)
                .as(StepVerifier::create)
                .expectNext(1)
                .verifyComplete();
    }
}
