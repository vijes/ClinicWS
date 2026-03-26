package com.clinica.api.modules.pacientes.dto;

import com.clinica.api.modules.personas.dto.PersonaDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteDTO {

    private UUID id;

    @Valid
    private PersonaDTO persona;

    @Size(max = 5)
    private String tipoSangre;

    @Size(max = 100)
    private String seguroMedico;

    private String contactoEmergencia;

    private String observaciones;
}
