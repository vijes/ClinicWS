package com.clinica.api.modules.clinica.controller;

import com.clinica.api.modules.clinica.dto.ClinicaPublicDTO;
import com.clinica.api.modules.clinica.service.ClinicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/clinicas")
@RequiredArgsConstructor
public class PublicClinicaController {

    private final ClinicaService clinicaService;

    @GetMapping("/list")
    public ResponseEntity<List<ClinicaPublicDTO>> getPublicList() {
        return ResponseEntity.ok(clinicaService.getAllClinicas().stream()
                .map(c -> ClinicaPublicDTO.builder()
                        .id(c.getId())
                        .nombre(c.isEsEmpresa() ? c.getRazonSocial() : c.getPrimerNombre() + " " + c.getPrimerApellido())
                        .build())
                .collect(Collectors.toList()));
    }

    @GetMapping("/validate-code/{id}/{code}")
    public ResponseEntity<Boolean> validateAccessCode(@PathVariable UUID id, @PathVariable String code) {
        return ResponseEntity.ok(clinicaService.validateAccessCode(id, code));
    }
}
