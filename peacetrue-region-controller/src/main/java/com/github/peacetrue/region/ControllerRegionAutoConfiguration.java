package com.github.peacetrue.region;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * @author xiayx
 */
@Configuration
@EnableConfigurationProperties(ControllerRegionProperties.class)
@ComponentScan(basePackageClasses = ControllerRegionAutoConfiguration.class)
@PropertySource("classpath:/application-region-controller.yml")
public class ControllerRegionAutoConfiguration {

}
