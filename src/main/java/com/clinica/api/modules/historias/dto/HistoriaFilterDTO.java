package com.clinica.api.modules.historias.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class HistoriaFilterDTO {
    private UUID pacienteId;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
}
