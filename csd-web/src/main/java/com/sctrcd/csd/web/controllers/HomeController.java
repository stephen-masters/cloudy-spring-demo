package com.sctrcd.csd.web.controllers;

import com.sctrcd.csd.web.domain.Language;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private final RestTemplate restTemplate;

    public HomeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * This will render using the Thymeleaf template: <code>src/main/resources/templates/home/index.html</code>
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String index() {
        return "home/index";
    }

    @ModelAttribute("languages")
    public List<Language> languages() {
        log.info("Requesting the list of languages...");

        // Use the "smart" Eureka-aware RestTemplate: "language-api" is the name the language API
        // registers with Eureka, not a host name.
        ResponseEntity<List<Language>> exchange = this.restTemplate.exchange(
                "http://language-api/languages", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        log.info("Returned code [{}]: {}", exchange.getStatusCode(), exchange.getBody());
        return exchange.getBody();
    }
}
