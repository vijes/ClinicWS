package com.clinica.api.common.util;

import org.springframework.stereotype.Component;

@Component
public class RucValidator {

    public boolean isValid(String ruc) {
        if (ruc == null || ruc.length() != 13 || !ruc.matches("\\d{13}")) {
            return false;
        }

        String cedulaInput = ruc.substring(0, 10);
        String establishment = ruc.substring(10, 13);

        if ("000".equals(establishment)) {
            return false;
        }

        // Basic CEDULA check for natural persons (most common)
        return isValidCedula(cedulaInput);
    }

    private boolean isValidCedula(String cedula) {
        if (cedula.length() != 10) return false;
        
        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) return false;

        int thirdDigit = Integer.parseInt(cedula.substring(2, 3));
        if (thirdDigit > 6) return false;

        int[] coefficients = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int total = 0;
        for (int i = 0; i < 9; i++) {
            int val = Integer.parseInt(cedula.substring(i, i + 1)) * coefficients[i];
            total += val > 9 ? val - 9 : val;
        }

        int checkDigit = Integer.parseInt(cedula.substring(9, 10));
        int calculated = (total % 10 == 0) ? 0 : 10 - (total % 10);

        return checkDigit == calculated;
    }
}
