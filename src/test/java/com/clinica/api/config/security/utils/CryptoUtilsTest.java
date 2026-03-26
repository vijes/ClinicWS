package com.clinica.api.config.security.utils;

import org.junit.jupiter.api.Test;

public class CryptoUtilsTest {

    @Test
    public void encrypt(){
        String encryptValue = CryptoUtils.encrypt("Password01@");
        System.out.println("Password: " + encryptValue);
    }


    @Test
    public void decrypt(){
        String encryptValue = CryptoUtils.decrypt("QT6PdL/IY9l8IL+OQ577JWTAm6aM8z9tdIKxcBdQuq8=");
        System.out.println("Password: " + encryptValue);
    }

}
