package com.sctrcd.csd.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.sctrcd.csd.crypto.CryptoApiApplication;
import com.sctrcd.csd.eureka.EurekaApplication;
import com.sctrcd.csd.language.LanguageApiApplication;
import com.sctrcd.csd.web.CsdWebApplication;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

/**
 * Starts the whole demo in one JVM: the Eureka server, the crypto and language APIs, and the web
 * application, each in its own Spring context on a random port. The APIs register with Eureka; the web
 * application finds the language API through Eureka and renders the languages it returns.
 *
 * <p>All four modules share this classpath, so their {@code application.yml} files would shadow one another.
 * {@code spring.config.name} points at a name none of them uses, and each context is given the settings
 * it needs here instead, with Eureka's intervals cut to a second so that registration is quick. The
 * Eureka server also puts Jersey on the shared classpath, so the clients are told not to use it for their
 * Eureka transport, as they would not when run on their own.
 */
class ServiceDiscoveryTest {

    private static final Duration DISCOVERY_TIMEOUT = Duration.ofSeconds(60);

    private static final List<ConfigurableApplicationContext> contexts = new ArrayList<>();

    private static ConfigurableApplicationContext web;

    @BeforeAll
    static void startTheServices() {
        ConfigurableApplicationContext eureka = start(
                EurekaApplication.class,
                Map.of(
                        "spring.application.name", "eureka",
                        "eureka.client.register-with-eureka", "false",
                        "eureka.client.fetch-registry", "false",
                        "eureka.server.wait-time-in-ms-when-sync-empty", "0",
                        "eureka.server.enable-self-preservation", "false",
                        "eureka.server.use-read-only-response-cache", "false"));
        String eurekaUrl = "http://localhost:" + port(eureka) + "/eureka/";

        start(LanguageApiApplication.class, client("language-api", eurekaUrl));
        start(CryptoApiApplication.class, client("crypto-api", eurekaUrl));
        web = start(CsdWebApplication.class, client("api-client", eurekaUrl));
    }

    @AfterAll
    static void stopTheServices() {
        contexts.reversed().forEach(ConfigurableApplicationContext::close);
    }

    @Test
    void shouldRenderTheLanguagesFromTheLanguageApiFoundThroughEureka() {
        awaitInstancesOf("language-api");

        String page = RestClient.create("http://localhost:" + port(web))
                .get()
                .uri("/")
                .retrieve()
                .body(String.class);

        assertThat(page)
                .contains("<h2>Languages</h2>")
                .contains("<li>German</li>", "<li>English</li>", "<li>Spanish</li>")
                .contains("<li>French</li>", "<li>Portuguese</li>");
    }

    @Test
    void shouldReachTheCryptoApiByItsServiceName() {
        awaitInstancesOf("crypto-api");

        // The web application's load-balanced RestTemplate resolves "crypto-api" through Eureka.
        Map<String, String> hash = web.getBean(RestTemplate.class)
                .exchange(
                        "http://crypto-api/bcrypt/hello",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, String>>() {})
                .getBody();

        assertThat(hash).containsEntry("plaintext", "hello").containsKeys("hashtext", "salt");
        assertThat(Objects.requireNonNull(hash).get("hashtext")).startsWith("$2a$");
    }

    private static void awaitInstancesOf(String serviceId) {
        DiscoveryClient discovery = web.getBean(DiscoveryClient.class);
        await().atMost(DISCOVERY_TIMEOUT)
                .pollInterval(Duration.ofMillis(250))
                .untilAsserted(() -> assertThat(discovery.getInstances(serviceId))
                        .extracting(ServiceInstance::getPort)
                        .isNotEmpty()
                        .doesNotContain(0));
    }

    private static Map<String, Object> client(String name, String eurekaUrl) {
        return Map.of(
                "spring.application.name", name,
                "eureka.client.service-url.defaultZone", eurekaUrl,
                "eureka.client.registry-fetch-interval-seconds", "1",
                "eureka.client.initial-instance-info-replication-interval-seconds", "0",
                "eureka.client.instance-info-replication-interval-seconds", "1",
                "eureka.client.jersey.enabled", "false",
                "eureka.instance.hostname", "localhost",
                "eureka.instance.lease-renewal-interval-in-seconds", "1");
    }

    private static ConfigurableApplicationContext start(Class<?> application, Map<String, ?> properties) {
        Map<String, Object> all = new HashMap<>(properties);
        all.put("spring.config.name", "csd-integration-tests");
        all.put("server.port", "0");
        all.put("spring.main.banner-mode", "off");
        if (application != LanguageApiApplication.class) {
            // Only the language API uses JPA; the others would otherwise get an empty HSQLDB of their own.
            all.put(
                    "spring.autoconfigure.exclude",
                    "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration");
        }
        ConfigurableApplicationContext context =
                new SpringApplicationBuilder(application).properties(all).run();
        contexts.add(context);
        return context;
    }

    private static int port(ConfigurableApplicationContext context) {
        return Integer.parseInt(Objects.requireNonNull(context.getEnvironment().getProperty("local.server.port")));
    }
}
