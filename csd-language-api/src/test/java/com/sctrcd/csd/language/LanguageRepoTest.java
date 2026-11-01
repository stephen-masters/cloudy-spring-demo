package com.sctrcd.csd.language;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "eureka.client.enabled=false")
class LanguageRepoTest {

    @Autowired
    private LanguageRepository languageRepo;

    @Test
    void shouldFindAllLanguages() {
        List<Language> languages = languageRepo.findAll();

        assertEquals(
                List.of("de", "en", "es", "fr", "pt"),
                languages.stream().map(Language::getIsoCode).sorted().toList(),
                "import.sql should have loaded five languages.");
    }

    @Test
    void shouldFindOneLanguage() {
        Language language = languageRepo.findById("fr").orElseThrow();

        assertEquals("fr", language.getIsoCode(), "ISO code of the language found should be 'fr'.");
        assertEquals("French", language.getName(), "The English name of the language found should be 'French'");
        assertEquals(
                "Français", language.getNativeName(), "The French name of the language found should be 'Français'");
    }

    @Test
    void shouldNotFindAnUnknownLanguage() {
        assertTrue(languageRepo.findById("xx").isEmpty());
    }
}
