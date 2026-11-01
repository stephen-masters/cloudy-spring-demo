package com.sctrcd.csd.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * A simple service for hashing text.
 *
 * @author Stephen Masters
 */
@Service
public class CryptoService {

    private static final Logger log = LoggerFactory.getLogger(CryptoService.class);

    public CryptoService() {}

    /**
     * Create a hash using the BCrypt algorithm. A random salt will be
     * generated.
     *
     * @param plaintext
     *            The plaintext to be hashed.
     * @return A {@link BCryptHash} encapsulating the plaintext, hash and salt.
     */
    public BCryptHash bcrypt(String plaintext) {
        return bcrypt(plaintext, BCrypt.gensalt());
    }

    /**
     * Create a hash using the BCrypt algorithm. This uses a predefined salt.
     * This enables us to check whether a password has changed.
     *
     * @param plaintext
     *            The plaintext to be hashed.
     * @return A {@link BCryptHash} encapsulating the plaintext, hash and salt.
     */
    public BCryptHash bcrypt(String plaintext, String salt) {
        String hashText = BCrypt.hashpw(plaintext, salt);

        BCryptHash hash = new BCryptHash(plaintext, hashText, salt);

        log.info("[" + plaintext + "] hashed to: " + hash);

        return hash;
    }

    /**
     * Confirms whether hash text is a valid BCrypt hash of the plaintext.
     *
     * @param plaintext
     *            The plaintext.
     * @param hashtext
     *            The BCrypt hash.
     * @return A {@link BCryptHashValidation} representing the values requested
     *         and whether they match.
     */
    public BCryptHashValidation isCorrectBCrypt(String plaintext, String hashtext) {
        BCryptHashValidation result = new BCryptHashValidation();
        result.setHashtext(hashtext);
        result.setPlaintext(plaintext);

        try {
            result.setIsCorrect(BCrypt.checkpw(plaintext, hashtext));
        } catch (IllegalArgumentException ex) {
            result.setIsCorrect(false);
        }

        return result;
    }
}
