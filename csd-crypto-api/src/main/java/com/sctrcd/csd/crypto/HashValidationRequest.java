package com.sctrcd.csd.crypto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a request to validate whether the plaintext of a password matches
 * a particular hash.
 *
 * @author Stephen Masters
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HashValidationRequest {

    private String plaintext;
    private String hashtext;

    public HashValidationRequest() {}

    public HashValidationRequest(String plaintext, String hashtext) {
        this.plaintext = plaintext;
        this.hashtext = hashtext;
    }

    public String getPlaintext() {
        return plaintext;
    }

    public void setPlaintext(String plaintext) {
        this.plaintext = plaintext;
    }

    public String getHashtext() {
        return hashtext;
    }

    public void setHashtext(String hashtext) {
        this.hashtext = hashtext;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": {" + " plaintext=" + plaintext + ", hashtext=" + hashtext + " }";
    }
}
