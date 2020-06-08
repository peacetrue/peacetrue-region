package com.github.peacetrue.region;

import io.r2dbc.spi.ConnectionFactory;
import org.jeasy.random.EasyRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.r2dbc.connectionfactory.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;

@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration({
        R2dbcAutoConfiguration.class,
        R2dbcDataAutoConfiguration.class,
        R2dbcRepositoriesAutoConfiguration.class,
        R2dbcTransactionManagerAutoConfiguration.class,
})
@EnableAutoConfiguration
@ActiveProfiles("region-service-test")
public class RegionServiceImplTestAutoConfiguration {

    public static final EasyRandom EASY_RANDOM = new EasyRandom();
    public static final RegionAdd ADD = EASY_RANDOM.nextObject(RegionAdd.class);
    public static final RegionModify MODIFY = EASY_RANDOM.nextObject(RegionModify.class);

    static {
        ADD.setOperatorId(EASY_RANDOM.nextObject(String.class));
        MODIFY.setOperatorId(EASY_RANDOM.nextObject(String.class));
    }

    public static RegionVO vo;

    @Autowired
    public void initializeDatabase(ConnectionFactory connectionFactory) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource[] scripts = new Resource[]{
                resourceLoader.getResource("classpath:schema.sql"),
//                resourceLoader.getResource("classpath:data.sql")
        };
        new ResourceDatabasePopulator(scripts).execute(connectionFactory).block();
    }
}
