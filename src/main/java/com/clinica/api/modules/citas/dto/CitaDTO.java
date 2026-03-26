package com.clinica.api.modules.citas.dto;

import com.clinica.api.modules.pacientes.dto.PacienteDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaDTO {

    private UUID id;

    @NotNull(message = "El paciente es obligatorio")
    private PacienteDTO paciente;

    @NotNull(message = "El profesional es obligatorio")
    private UUID profesionalId; // ID del usuario médico

    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    @NotBlank(message = "El motivo es obligatorio")
    private String motivo;

    private String estado;
}
