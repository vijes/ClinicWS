package com.clinica.api.modules.historias.controller;

import com.clinica.api.modules.historias.dto.HistoriaClinicaDTO;
import com.clinica.api.modules.historias.dto.HistoriaFilterDTO;
import com.clinica.api.modules.historias.service.HistoriaClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historias")
@RequiredArgsConstructor
public class HistoriaClinicaController {

    private final HistoriaClinicaService historiaService;

    @PostMapping("/crear")
    public ResponseEntity<HistoriaClinicaDTO> crearHistoria(@RequestBody HistoriaClinicaDTO dto) {
        return ResponseEntity.ok(historiaService.crearHistoria(dto));
    }

    @PostMapping("/filtrar")
    public ResponseEntity<List<HistoriaClinicaDTO>> filtrarHistorias(@RequestBody HistoriaFilterDTO filter) {
        return ResponseEntity.ok(historiaService.filtrarHistorias(filter));
    }
}
