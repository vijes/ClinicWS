package com.clinica.api.modules.pacientes.controller;

import com.clinica.api.modules.pacientes.dto.PacienteDTO;
import com.clinica.api.modules.pacientes.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.clinica.api.modules.pacientes.dto.PacienteFilterDTO;
import com.clinica.api.modules.pacientes.dto.PacienteQueryDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing patients.
 * Delegates business logic to PacienteService.
 */
@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    public ResponseEntity<List<PacienteDTO>> getAll(
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String primerNombre,
            @RequestParam(required = false) String segundoNombre,
            @RequestParam(required = false) String primerApellido,
            @RequestParam(required = false) String segundoApellido) {
        
        if (documento == null && primerNombre == null && segundoNombre == null && primerApellido == null && segundoApellido == null) {
            return ResponseEntity.ok(pacienteService.findAll());
        }
        
        return ResponseEntity.ok(pacienteService.findByFilters(documento, primerNombre, segundoNombre, primerApellido, segundoApellido));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(pacienteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PacienteDTO> create(@Valid @RequestBody PacienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> update(@PathVariable UUID id, @Valid @RequestBody PacienteDTO dto) {
        return ResponseEntity.ok(pacienteService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Nuevos endpoints solicitados:
    
    @PostMapping("/createPacient")
    public ResponseEntity<?> createPacient(@Valid @RequestBody PacienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteService.createPacient(dto));
    }

    @PostMapping("/obtenerPaciente")
    public ResponseEntity<?> obtenerPaciente(@RequestBody PacienteQueryDTO query) {
        return ResponseEntity.ok(pacienteService.obtenerPaciente(query));
    }

    @PostMapping("/obtenerListaPacientes")
    public ResponseEntity<List<PacienteDTO>> obtenerListaPacientes(@RequestBody PacienteFilterDTO filter) {
        return ResponseEntity.ok(pacienteService.obtenerListaPacientes(filter));
    }
}
