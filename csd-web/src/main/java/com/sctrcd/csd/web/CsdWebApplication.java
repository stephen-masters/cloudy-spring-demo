package com.sctrcd.csd.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * The client application startup class. This is a Spring Boot microservice, hence the autoconfig
 * annotations. Having the Eureka client on the classpath is enough for it to register with Eureka and
 * fetch the registry.
 *
 * @author Stephen Masters
 */
@SpringBootApplication
public class CsdWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsdWebApplication.class, args);
    }

    /**
     * Ensures that API calls made via the {@link RestTemplate} will resolve endpoints via Eureka and the
     * Spring Cloud LoadBalancer client-side load balancer.
     *
     * @return A Eureka-enabled, load balanced {@link RestTemplate}.
     */
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
