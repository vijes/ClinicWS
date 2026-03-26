package com.clinica.api.modules.historias.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriaClinicaDTO {
    private UUID id;
    private UUID pacienteId;
    private String diagnostico;
    private String tratamiento;
    private String receta;
    private LocalDateTime fechaRegistro; // Correspondiente al dateCreate
    private String medicoEvaluador; // Correspondiente al userCreate
}
