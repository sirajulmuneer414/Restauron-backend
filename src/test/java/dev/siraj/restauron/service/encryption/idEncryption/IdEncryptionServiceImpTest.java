
package dev.siraj.restauron.service.encryption.idEncryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdEncryptionServiceImpTest {

    private IdEncryptionService idEncryptionService;

    @BeforeEach
    void setUp() throws IllegalAccessException {
        // Provide a mock 32-character key for testing
        idEncryptionService = new IdEncryptionServiceImp("12345678901234567890123456789012");
    }

    @Test
    void testEncryptDecrypt() {
        Long originalId = 12345L;
        String encryptedId = idEncryptionService.encryptLongId(originalId);
        Long decryptedId = idEncryptionService.decryptToLongId(encryptedId);
        assertEquals(originalId, decryptedId);
    }

    @Test
    void testEncryptNull() {
        assertNull(idEncryptionService.encryptLongId(null));
    }

    @Test
    void testDecryptNullOrEmpty() {
        assertNull(idEncryptionService.decryptToLongId(null));
        assertNull(idEncryptionService.decryptToLongId(""));
    }

    @Test
    void testDecryptInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            idEncryptionService.decryptToLongId("invalid-encrypted-id");
        });
    }
}
