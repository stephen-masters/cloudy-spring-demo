package com.sctrcd.csd.language;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LanguageController {

    private final LanguageRepository languageRepo;

    public LanguageController(LanguageRepository languageRepo) {
        this.languageRepo = languageRepo;
    }

    @RequestMapping(value = "/languages")
    public List<Language> findLanguages() {
        return languageRepo.findAll();
    }

    /**
     * Finds one language by its ISO 639-1 code. As before, an unknown code gives an empty 200 response
     * rather than a 404.
     */
    @RequestMapping(value = "/languages/{isocode}")
    public Language findLanguages(@PathVariable String isocode) {
        return languageRepo.findById(isocode).orElse(null);
    }
}
