package com.clinica.api.config.security.utils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

public class CustomPasswordEncoder implements PasswordEncoder {

    private final CryptoUtils cryptoUtils;

    public CustomPasswordEncoder(CryptoUtils cryptoUtils) {
        this.cryptoUtils = cryptoUtils;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) return null;
        return cryptoUtils.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            System.out.println("[AUTH DEBUG] Password or encoded value is null");
            return false;
        }
        try {
            System.out.println("[AUTH DEBUG] Attempting match...");
            System.out.println("[AUTH DEBUG] Raw password received: [" + rawPassword + "]");
            System.out.println("[AUTH DEBUG] Encoded password from DB: [" + encodedPassword + "]");
            
            String decrypted = cryptoUtils.decrypt(encodedPassword);
            System.out.println("[AUTH DEBUG] Decrypted value from DB: [" + decrypted + "]");
            
            boolean matches = rawPassword.toString().equals(decrypted);
            System.out.println("[AUTH DEBUG] Match result: " + matches);
            return matches;
        } catch (Exception e) {
            System.out.println("[AUTH DEBUG] Decryption failed: " + e.getMessage());
            return false;
        }
    }
}
