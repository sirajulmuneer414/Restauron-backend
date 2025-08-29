package dev.siraj.restauron.service.encryption.idEncryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


// This class is used as a service to set up encryption throughout the application
// when sending sensitive information like ID to the front-end

@Service
public class IdEncryptionServiceImp implements IdEncryptionService{

    private static final String ALGORITHM = "AES";
    private final SecretKeySpec secretKey;

    public IdEncryptionServiceImp(@Value("${app.encryption.key}") String secret) throws IllegalAccessException {
        System.out.println(secret);
        System.out.println(secret.length());
        if(secret == null || secret.length() != 32){
            throw new IllegalAccessException("Encryption key must be 32 characters long.");
        }
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    // Method for encrypting Long id

    @Override
    public String encryptLongId(Long id) {
        if(id == null) return null;

        try{
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(String.valueOf(id).getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptedBytes);


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    // Method for decrypting to long id from String

    @Override
    public Long decryptToLongId(String encryptedId) {

        if(encryptedId == null || encryptedId.isEmpty()) {
            return null;
        }

        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedId));

            return Long.parseLong(new String(decryptedBytes, StandardCharsets.UTF_8));
        }
        catch (Exception e){
            throw new IllegalArgumentException("Invalid encrypted Id");
        }


    }
}
