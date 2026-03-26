package com.clinica.api.modules.pacientes.service;

import com.clinica.api.modules.pacientes.dto.PacienteDTO;
import com.clinica.api.modules.pacientes.repository.PacienteRepository;
import com.clinica.api.modules.personas.domain.Persona;
import com.clinica.api.modules.personas.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PacienteValidationService {

    private final PersonaRepository personaRepository;
    private final PacienteRepository pacienteRepository;

    public void validateCreation(PacienteDTO dto) {
        String documento = dto.getPersona().getDocumento();
        
        // 1. Validar unicidad del documento general en la base (Opcional si el modelo asume que una persona puede existir sin ser paciente, pero req dice: "No debe existir más de un paciente con el mismo documento")
        // Como el documento es único en la tabla personas, esto ya se restringe por DB, pero validamos a nivel lógico:
        Optional<Persona> personaOpt = personaRepository.findByDocumento(documento);
        
        if (personaOpt.isPresent()) {
            Persona persona = personaOpt.get();
            // 2. Si el paciente ya existe vamos a devolver un mensaje
            if (pacienteRepository.findByPersonaId(persona.getId()).isPresent()) {
                throw new IllegalArgumentException("El paciente ya esta registrado en la base.");
            }
        }
    }
}
