package com.clinica.api;

import com.clinica.api.config.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JwtTest {

    @Autowired
    private JwtService jwtService;

    @Test
    public void testToken() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX0FETUlOIl0sInN1YiI6ImFkbWluQ2xpbmljIiwiaWF0IjoxNzczNzA5MjgxLCJleHAiOjE3NzM3MTEwODF9.XhKCzzXxoCGg1iMaqSMZ4VXCBw2hBdoF44VVK4Dg060";
        try {
            System.out.println("--- TESTING TOKEN ---");
            String username = jwtService.extractUsername(token);
            System.out.println("Extracted username: " + username);
            System.out.println("Token Valid (syntax): " + jwtService.validateJwtToken(token));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
