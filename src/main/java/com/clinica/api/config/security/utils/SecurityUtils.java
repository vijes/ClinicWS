package com.clinica.api.config.security.utils;

import com.clinica.api.config.security.services.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    public static UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return (UserDetailsImpl) principal;
        }
        return null;
    }

    public static UUID getCurrentClinicaId() {
        UserDetailsImpl user = getCurrentUser();
        return user != null ? user.getClinicaId() : null;
    }

    public static String getCurrentCompanyCode() {
        UserDetailsImpl user = getCurrentUser();
        return (user != null && user.getCompanyCode() != null) ? user.getCompanyCode() : "1";
    }

    public static boolean isSuperAdmin() {
        UserDetailsImpl user = getCurrentUser();
        if (user == null) return false;
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
