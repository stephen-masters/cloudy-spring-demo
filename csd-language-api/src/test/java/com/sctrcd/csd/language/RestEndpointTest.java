package com.sctrcd.csd.language;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/**
 * The endpoints, through the whole Spring context. Registration with Eureka is switched off: there is no
 * Eureka server in a unit test, and csd-integration-tests covers discovery.
 */
@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class RestEndpointTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void shouldReturnListOfLanguages() throws Exception {
        this.mvc
                .perform(get("/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[*].isoCode", containsInAnyOrder("de", "en", "es", "fr", "pt")));
    }

    @Test
    void shouldFindLanguageByIsoCode() throws Exception {
        this.mvc
                .perform(get("/languages/fr"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isoCode").value("fr"))
                .andExpect(jsonPath("$.name").value("French"))
                .andExpect(jsonPath("$.nativeName").value("Français"));
    }

    @Test
    void shouldReturnAnEmptyBodyForAnUnknownIsoCode() throws Exception {
        this.mvc
                .perform(get("/languages/xx"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void shouldAllowCrossOriginRequests() throws Exception {
        this.mvc.perform(get("/languages")).andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}
