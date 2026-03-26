package com.clinica.api.modules.personas.controller;

import com.clinica.api.modules.personas.dto.PersonaDTO;
import com.clinica.api.modules.personas.service.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping
    public ResponseEntity<List<PersonaDTO>> getAll() {
        return ResponseEntity.ok(personaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(personaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PersonaDTO> create(@Valid @RequestBody PersonaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personaService.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaDTO> update(@PathVariable UUID id, @Valid @RequestBody PersonaDTO dto) {
        return ResponseEntity.ok(personaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        personaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
