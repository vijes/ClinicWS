package com.clinica.api.config.security.utils;

import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public final  class CryptoUtils {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String KEY = "ClinicaSecretKey"; // 16 characters for AES-128

    public static String encrypt(String value) {
//        try {
//            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            byte[] encryptedValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
//            return Base64.getEncoder().encodeToString(encryptedValue);
//        } catch (Exception e) {
//            throw new RuntimeException("Error during encryption: " + e.getMessage(), e);
//        }
        try {

            byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // generar IV aleatorio
            byte[] iv = new byte[16];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

            // combinar IV + ciphertext
            byte[] encryptedIVAndText = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, iv.length);
            System.arraycopy(encrypted, 0, encryptedIVAndText, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(encryptedIVAndText);

        } catch (Exception e) {
            throw new RuntimeException("Error during encryption", e);
        }
    }

    public static String decrypt(String encryptedValue) {
//        try {
//            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
//            byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
//            byte[] decryptedValue = cipher.doFinal(decodedValue);
//            return new String(decryptedValue, StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            throw new RuntimeException("Error during decryption: " + e.getMessage(), e);
//        }
        try {

            byte[] encryptedIvTextBytes = Base64.getDecoder().decode(encryptedValue);

            byte[] iv = new byte[16];
            byte[] encryptedBytes = new byte[encryptedIvTextBytes.length - 16];

            System.arraycopy(encryptedIvTextBytes, 0, iv, 0, 16);
            System.arraycopy(encryptedIvTextBytes, 16, encryptedBytes, 0, encryptedBytes.length);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            byte[] decrypted = cipher.doFinal(encryptedBytes);

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error during decryption", e);
        }
    }
}
