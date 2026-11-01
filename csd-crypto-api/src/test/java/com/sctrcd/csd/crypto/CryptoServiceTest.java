package com.sctrcd.csd.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

class CryptoServiceTest {

    private final CryptoService cryptoService = new CryptoService();

    @Test
    void shouldAcceptPreviouslyGeneratedHashX() {
        BCryptHashValidation check = cryptoService.isCorrectBCrypt(
                "\"PortalCal3\"", "$2a$10$uxWNssYD45hMnV4ajPH9GuGIv5aioSRg01x0DcAJohW4XvwC0/CK2");

        assertTrue(check.getIsCorrect(), "The service should confirm that the password and hash are a valid match.");
    }

    @Test
    void shouldAcceptPreviouslyGeneratedHash() {
        BCryptHashValidation check = cryptoService.isCorrectBCrypt(
                "PortalCal3", "$2a$10$7aLeSDz81EbsI9DEziKCFeE9umMVqdUD2g8t8CadM2dxgupb8GjTC");

        assertTrue(check.getIsCorrect(), "The service should confirm that the password and hash are a valid match.");
    }

    @Test
    void shouldRejectTheWrongPlaintext() {
        BCryptHashValidation check = cryptoService.isCorrectBCrypt(
                "PortalCal4", "$2a$10$7aLeSDz81EbsI9DEziKCFeE9umMVqdUD2g8t8CadM2dxgupb8GjTC");

        assertFalse(check.getIsCorrect());
    }

    @Test
    void shouldRejectSomethingThatIsNotABCryptHash() {
        BCryptHashValidation check = cryptoService.isCorrectBCrypt("hello", "not a hash");

        assertFalse(check.getIsCorrect());
        assertEquals("hello", check.getPlaintext());
        assertEquals("not a hash", check.getHashtext());
    }

    @Test
    void shouldHashText() {
        BCryptHash hash = cryptoService.bcrypt("hello");
        assertTrue(BCrypt.checkpw("hello", hash.getHashtext()));

        BCryptHashValidation check = cryptoService.isCorrectBCrypt(hash.getPlaintext(), hash.getHashtext());
        assertTrue(check.getIsCorrect(), "The service should confirm that the password and hash are a valid match.");
    }

    @Test
    void shouldGiveTheSameHashForTheSameSalt() {
        BCryptHash first = cryptoService.bcrypt("hello");
        BCryptHash second = cryptoService.bcrypt("hello", first.getSalt());

        assertNotNull(first.getSalt());
        assertEquals(first, second);
    }
}
