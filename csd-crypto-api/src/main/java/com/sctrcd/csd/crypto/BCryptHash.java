package com.sctrcd.csd.crypto;

/**
 * This class is intended to encapsulate a BCrypt Hash which has just been
 * created. It includes the salt and the plaintext to help the requester
 * validate that the plaintext which has been hashed is the plaintext they
 * requested.
 *
 * @author Stephen Masters
 */
public class BCryptHash {

    private String plaintext;
    private String hashtext;
    private String salt;

    public BCryptHash() {}

    public BCryptHash(String plaintext, String hashtext, String salt) {
        this.plaintext = plaintext;
        this.hashtext = hashtext;
        this.salt = salt;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof BCryptHash) {
            BCryptHash thatHash = ((BCryptHash) that);
            return this.plaintext.equals(thatHash.getPlaintext())
                    && this.hashtext.equals(thatHash.getHashtext())
                    && this.salt.equals(thatHash.getSalt());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (plaintext + " :: " + hashtext + " :: " + salt).hashCode();
    }

    @Override
    public String toString() {
        return "Hash:{ "
                + "plaintext=[" + plaintext + "], "
                + "hashtext=[" + hashtext + "], "
                + "salt=[" + salt + "] }";
    }
}
