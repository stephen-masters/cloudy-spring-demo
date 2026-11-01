package com.sctrcd.csd.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A simple REST-style API endpoint for hashing text.
 *
 * @author Stephen Masters
 */
@RestController
public class CryptoController {

    private static final Logger log = LoggerFactory.getLogger(CryptoController.class);

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    /**
     * A simple GET-based endpoint.
     *
     * @param plaintext
     *            The text the client wishes to be hashed.
     * @return A {@link BCryptHash} with the resulting plaintext, hashtext and
     *         salt.
     */
    @RequestMapping(
            value = "/bcrypt/{plaintext}",
            method = {RequestMethod.GET})
    public BCryptHash hashGet(@PathVariable String plaintext) {

        log.info("Hash requested for: " + plaintext);

        return cryptoService.bcrypt(plaintext);
    }

    /**
     * Given all the funny characters a user should be able to put in their
     * password, it may be necessary to URL encode the value when sending to the
     * GET API. As an alternative, here is a POST-based endpoint.
     *
     * @param plaintext
     *            The text the client wishes to be hashed.
     * @return A {@link BCryptHash} with the resulting plaintext, hashtext and
     *         salt.
     */
    @RequestMapping(
            value = "/bcrypt",
            method = {RequestMethod.GET, RequestMethod.POST})
    public BCryptHash bcrypt(
            @RequestParam(value = "plaintext", required = true) String plaintext,
            @RequestParam(value = "salt", required = false) String salt) {

        log.info("Hash requested for plaintext=[" + plaintext + "] and salt=[" + salt + "]");

        if (salt == null) {
            return cryptoService.bcrypt(plaintext);
        }
        return cryptoService.bcrypt(plaintext, salt);
    }

    /**
     * Checks whether some hashtext is a valid BCrypt hash of the plaintext.
     *
     * @param plaintext
     *            The plaintext text.
     * @param hashtext
     *            The hash that we wish to compare to the plaintext.
     *
     * @return A {@link BCryptHashValidation} result with the resulting
     *         plaintext, hashtext and whether the hashtext is valid for the
     *         plaintext.
     */
    @RequestMapping(
            value = "/bcryptcheck",
            method = {RequestMethod.GET, RequestMethod.POST})
    public BCryptHashValidation bcryptCheck(@RequestBody HashValidationRequest req) {

        log.info("Checking plaintext [" + req.getPlaintext() + "] against hashtext [" + req.getHashtext() + "].");

        return cryptoService.isCorrectBCrypt(req.getPlaintext(), req.getHashtext());
    }
}
