package com.sctrcd.csd.web.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

/**
 * The home page, with discovery mocked. Eureka is switched off and Spring Cloud's simple discovery client
 * answers for "language-api" instead, so the load-balanced {@link RestTemplate} still has to resolve the
 * service name to an instance. {@link MockRestServiceServer} sits below the load balancer and plays the
 * language API at the address that instance gives. csd-integration-tests runs the same page against a
 * real Eureka server and a real language API.
 */
@SpringBootTest(
        properties = {
            "eureka.client.enabled=false",
            "spring.cloud.discovery.client.simple.instances.language-api[0].uri=http://language-api.test:9999"
        })
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer languageApi;

    @BeforeEach
    void setUp() {
        languageApi = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Test
    void shouldListTheLanguagesFromTheDiscoveredLanguageApi() throws Exception {
        languageApi
                .expect(requestTo("http://language-api.test:9999/languages"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"isoCode":"en","name":"English","nativeName":"English"},
                         {"isoCode":"fr","name":"French","nativeName":"Français"}]
                        """, MediaType.APPLICATION_JSON));

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/index"))
                .andExpect(content().string(containsString("<h2>Languages</h2>")))
                .andExpect(content().string(containsString("<li>English</li>")))
                .andExpect(content().string(containsString("<li>French</li>")));

        languageApi.verify();
    }

    @Test
    void shouldLeaveOutTheListWhenThereAreNoLanguages() throws Exception {
        languageApi
                .expect(requestTo("http://language-api.test:9999/languages"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Demo of data returned from a REST API")))
                .andExpect(content().string(not(containsString("<h2>Languages</h2>"))));

        languageApi.verify();
    }
}
