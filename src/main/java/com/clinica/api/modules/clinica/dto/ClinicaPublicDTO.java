package com.clinica.api.modules.clinica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicaPublicDTO {
    private UUID id;
    private String nombre; // Nombre comercial o Razón Social o Nombre Natural
}
