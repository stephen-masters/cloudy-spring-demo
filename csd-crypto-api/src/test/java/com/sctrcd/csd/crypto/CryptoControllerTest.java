package com.sctrcd.csd.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

/**
 * The endpoints, through the whole Spring context. Registration with Eureka is switched off: there is no
 * Eureka server in a unit test, and csd-integration-tests covers discovery.
 */
@SpringBootTest(properties = "eureka.client.enabled=false")
@AutoConfigureMockMvc
class CryptoControllerTest {

    private final JsonMapper mapper = JsonMapper.builder().build();

    @Autowired
    private MockMvc mvc;

    @Test
    void shouldHaveBCryptEndpoint() throws Exception {
        String response = this.mvc
                .perform(post("/bcrypt?plaintext=hello"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BCryptHash responseHash = mapper.readValue(response, BCryptHash.class);

        assertEquals("hello", responseHash.getPlaintext());
        assertNotNull(responseHash.getHashtext());
        assertNotNull(responseHash.getSalt());

        // The salt is randomised, so we can't check that the response salt or
        // hash is a specific value. However, we can at least check that the
        // response salt/hash combo validates as a hash of the plaintext.
        assertTrue(BCrypt.checkpw("hello", responseHash.getHashtext()));

        String response2 = this.mvc
                .perform(post("/bcrypt").param("plaintext", "hello").param("salt", responseHash.getSalt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        BCryptHash responseHash2 = mapper.readValue(response2, BCryptHash.class);

        assertEquals("hello", responseHash2.getPlaintext());
        assertEquals(responseHash.getSalt(), responseHash2.getSalt());
        assertEquals(responseHash.getHashtext(), responseHash2.getHashtext());
    }

    @Test
    void shouldHashThePathVariableOnGet() throws Exception {
        String response = this.mvc
                .perform(get("/bcrypt/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plaintext").value("hello"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        BCryptHash hash = mapper.readValue(response, BCryptHash.class);
        assertTrue(BCrypt.checkpw("hello", hash.getHashtext()));
    }

    @Test
    void shouldRequirePlaintext() throws Exception {
        this.mvc.perform(post("/bcrypt")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldHavePasswordCheckEndpoint() throws Exception {
        HashValidationRequest request =
                new HashValidationRequest("hello", "$2a$10$LVZVbyTzXeiXnah/ZKE89ubtF0qH0ZDTvpyF1BwQuLtQsJstxw6PK");

        this.mvc
                .perform(post("/bcryptcheck")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.plaintext").value("hello"))
                .andExpect(jsonPath("$.isCorrect").value(true));
    }

    @Test
    void shouldReportAMismatchFromThePasswordCheckEndpoint() throws Exception {
        HashValidationRequest request =
                new HashValidationRequest("goodbye", "$2a$10$LVZVbyTzXeiXnah/ZKE89ubtF0qH0ZDTvpyF1BwQuLtQsJstxw6PK");

        this.mvc
                .perform(post("/bcryptcheck")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCorrect").value(false));
    }

    @Test
    void shouldAllowCrossOriginRequests() throws Exception {
        this.mvc.perform(get("/bcrypt/hello")).andExpect(header().string("Access-Control-Allow-Origin", "*"));
    }
}
