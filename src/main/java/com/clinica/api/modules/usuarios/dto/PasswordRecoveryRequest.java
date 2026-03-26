package com.clinica.api.modules.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRecoveryRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String username;
}
