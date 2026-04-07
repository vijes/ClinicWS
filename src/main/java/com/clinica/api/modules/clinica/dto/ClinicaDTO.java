package com.clinica.api.modules.clinica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicaDTO {
    private UUID id;

    @NotBlank
    @Size(min = 13, max = 13)
    @Pattern(regexp = "\\d{13}", message = "El RUC debe tener exactamente 13 dígitos")
    private String ruc;

    private String razonSocial;
    private String nombreComercial;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    private boolean esEmpresa;

    @NotBlank
    private String telefonoCelular;
    private String telefonoConvencional;

    @NotBlank
    @Email
    @Size(min = 10, max = 100)
    private String email;

    @NotBlank(message = "La cédula del representante es obligatoria")
    private String cedulaRepresentante;

    @NotBlank(message = "El primer nombre del representante es obligatorio")
    private String repPrimerNombre;
    private String repSegundoNombre;
    @NotBlank(message = "El primer apellido del representante es obligatorio")
    private String repPrimerApellido;
    private String repSegundoApellido;
    
    /*@jakarta.validation.constraints.NotNull(message = "La fecha de nacimiento del representante es obligatoria")
    private java.time.LocalDate repFechaNacimiento;*/
    
    private String repEmail;
    private String repTelefono;

    private String codigoAccesoPortal;
}
