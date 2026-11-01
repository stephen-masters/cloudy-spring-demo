package com.sctrcd.csd.crypto;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * The Crypto API application startup class. This is a Spring Boot micro-service, hence the autoconfig
 * annotations. Having the Eureka client on the classpath is enough for it to register with Eureka.
 *
 * @author Stephen Masters
 */
@SpringBootApplication
public class CryptoApiApplication {

    private static final Logger log = LoggerFactory.getLogger(CryptoApiApplication.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(CryptoApiApplication.class, args);

        StringBuilder sb = new StringBuilder();

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);

        sb.append("\n    Application beans:\n");
        for (String beanName : beanNames) {
            sb.append("        ").append(beanName).append("\n");
        }
        log.info(sb.toString());
    }
}
