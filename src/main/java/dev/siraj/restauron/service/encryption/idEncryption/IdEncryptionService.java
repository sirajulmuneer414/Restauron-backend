package dev.siraj.restauron.service.encryption.idEncryption;

public interface IdEncryptionService {

    String encryptLongId(Long id);

    Long decryptToLongId(String encryptedId);
}
