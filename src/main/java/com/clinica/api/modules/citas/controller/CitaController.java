package com.clinica.api.modules.citas.controller;

import com.clinica.api.modules.citas.dto.CitaDTO;
import com.clinica.api.modules.citas.service.CitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @GetMapping
    public ResponseEntity<List<CitaDTO>> getAll(
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fechaDesde,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime fechaHasta) {
        
        if (documento == null && fechaDesde == null && fechaHasta == null) {
            return ResponseEntity.ok(citaService.findAll());
        }
        
        return ResponseEntity.ok(citaService.findByFilters(documento, fechaDesde, fechaHasta));
    }

    @PostMapping
    public ResponseEntity<CitaDTO> create(@Valid @RequestBody CitaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.save(dto));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<CitaDTO> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(citaService.cancel(id));
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<CitaDTO> reschedule(
            @PathVariable UUID id, 
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime nuevaFecha) {
        return ResponseEntity.ok(citaService.reschedule(id, nuevaFecha));
    }
}
