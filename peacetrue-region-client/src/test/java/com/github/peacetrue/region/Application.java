package com.github.peacetrue.region;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author : xiayx
 * @since : 2020-05-23 21:55
 **/
@EnableFeignClients
@SpringBootApplication
public class Application {

    @Bean
    public Module pageJacksonModule() {
        return new PageJacksonModule();
    }

//    @Bean
//    public HttpMessageConverters httpMessageConverters(List<HttpMessageConverter<?>> httpMessageConverters) {
//        return new HttpMessageConverters(false, httpMessageConverters);
//    }

}
