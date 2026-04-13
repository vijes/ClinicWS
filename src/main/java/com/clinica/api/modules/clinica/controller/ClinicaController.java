package com.clinica.api.modules.clinica.controller;

import com.clinica.api.modules.clinica.dto.ClinicaDTO;
import com.clinica.api.modules.clinica.service.ClinicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinicas")
@RequiredArgsConstructor
public class ClinicaController {

    private final ClinicaService clinicaService;

    @PostMapping("/createClinica")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClinicaDTO> registerClinica(@Valid @RequestBody ClinicaDTO clinicaDTO) {
        return ResponseEntity.ok(clinicaService.registerClinica(clinicaDTO));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClinicaDTO>> getAllClinicas() {
        return ResponseEntity.ok(clinicaService.getAllClinicas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClinicaDTO> getClinicaById(@PathVariable UUID id) {
        return ResponseEntity.ok(clinicaService.getClinicaById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClinicaDTO> updateClinica(@PathVariable UUID id, @Valid @RequestBody ClinicaDTO clinicaDTO) {
        return ResponseEntity.ok(clinicaService.updateClinica(id, clinicaDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClinica(@PathVariable UUID id) {
        clinicaService.deleteClinica(id);
        return ResponseEntity.noContent().build();
    }
}
