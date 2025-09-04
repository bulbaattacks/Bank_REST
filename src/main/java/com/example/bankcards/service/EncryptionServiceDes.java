package com.example.bankcards.service;

import io.jsonwebtoken.io.Decoders;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Service
public class EncryptionServiceDes {

    @Value("${application.encryption.key}")
    private String key;
    private SecretKey secretKey;
    private Cipher cipher;

    @PostConstruct
    private void build() throws GeneralSecurityException {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        DESKeySpec desKeySpec = new DESKeySpec(keyBytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        secretKey = keyFactory.generateSecret(desKeySpec);
        cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
    }

    public String encrypt(String original) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(original.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt", e);
        }
    }

    public String decrypt(String cypher) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] encryptedData = Base64.getDecoder().decode(cypher);
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt", e);
        }
    }
}
