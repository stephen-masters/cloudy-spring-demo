package com.sctrcd.csd.crypto;

/**
 * This class is intended to encapsulate a BCrypt Hash which has just been
 * created. It includes the salt and the plaintext to help the requester
 * validate that the plaintext which has been hashed is the plaintext they
 * requested.
 *
 * @author Stephen Masters
 */
public class BCryptHashValidation {

    private String plaintext;
    private String hashtext;
    private Boolean isCorrect;

    public BCryptHashValidation() {}

    public BCryptHashValidation(String plaintext, String hashtext, Boolean isCorrect) {
        this.plaintext = plaintext;
        this.hashtext = hashtext;
        this.isCorrect = isCorrect;
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

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    @Override
    public String toString() {
        return "Hash:{ plaintext=[" + plaintext + "], hashtext=[" + hashtext + "], isCorrect=[" + isCorrect + "] }";
    }
}
