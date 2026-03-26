package com.clinica.api.modules.usuarios.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class SignupRequest {
    private String username;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private Set<String> role;

    @NotBlank
    private String tipoDocumento;

    @NotBlank
    private String documento;

    @NotBlank
    @Size(max = 50)
    private String primerNombre;

    private String segundoNombre;

    @NotBlank
    @Size(max = 50)
    private String primerApellido;

    private String segundoApellido;

    @NotNull
    private LocalDate fechaNacimiento;

    private String email;

    private String codigoAccesoPortal;

    private UUID clinicaId;
}
