package com.clinica.api.modules.personas.service;

import com.clinica.api.modules.personas.domain.Persona;
import com.clinica.api.modules.personas.dto.PersonaDTO;
import com.clinica.api.modules.personas.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaRepository personaRepository;

    @Transactional(readOnly = true)
    public List<PersonaDTO> findAll() {
        return personaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonaDTO findById(UUID id) {
        return personaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
    }

    @Transactional
    public PersonaDTO save(PersonaDTO dto) {
        if (personaRepository.findByDocumento(dto.getDocumento()).isPresent()) {
            throw new RuntimeException("El documento ya existe");
        }
        Persona persona = toEntity(dto);
        if (persona.getNombreCompleto() == null || persona.getNombreCompleto().isBlank()) {
            persona.setNombreCompleto(buildNombreCompleto(persona));
        }
        return toDTO(personaRepository.save(persona));
    }

    @Transactional
    public PersonaDTO update(UUID id, PersonaDTO dto) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        
        persona.setTipoDocumento(dto.getTipoDocumento());
        persona.setDocumento(dto.getDocumento());
        persona.setPrimerNombre(dto.getPrimerNombre());
        persona.setSegundoNombre(dto.getSegundoNombre());
        persona.setPrimerApellido(dto.getPrimerApellido());
        persona.setSegundoApellido(dto.getSegundoApellido());
        
        if (dto.getNombreCompleto() != null && !dto.getNombreCompleto().isBlank()) {
            persona.setNombreCompleto(dto.getNombreCompleto());
        } else {
            persona.setNombreCompleto(buildNombreCompleto(persona));
        }

        persona.setFechaNacimiento(dto.getFechaNacimiento());
        persona.setSexo(dto.getSexo());
        persona.setDireccion(dto.getDireccion());
        persona.setTelefono(dto.getTelefono());
        persona.setEmail(dto.getEmail());

        return toDTO(personaRepository.save(persona));
    }

    @Transactional
    public void delete(UUID id) {
        personaRepository.deleteById(id);
    }

    private String buildNombreCompleto(Persona p) {
        StringBuilder sb = new StringBuilder();
        if (p.getPrimerNombre() != null) sb.append(p.getPrimerNombre());
        if (p.getSegundoNombre() != null && !p.getSegundoNombre().isBlank()) sb.append(" ").append(p.getSegundoNombre());
        if (p.getPrimerApellido() != null) sb.append(" ").append(p.getPrimerApellido());
        if (p.getSegundoApellido() != null && !p.getSegundoApellido().isBlank()) sb.append(" ").append(p.getSegundoApellido());
        return sb.toString().trim();
    }

    private PersonaDTO toDTO(Persona persona) {
        return PersonaDTO.builder()
                .id(persona.getId())
                .tipoDocumento(persona.getTipoDocumento())
                .documento(persona.getDocumento())
                .primerNombre(persona.getPrimerNombre())
                .segundoNombre(persona.getSegundoNombre())
                .primerApellido(persona.getPrimerApellido())
                .segundoApellido(persona.getSegundoApellido())
                .nombreCompleto(persona.getNombreCompleto())
                .fechaNacimiento(persona.getFechaNacimiento())
                .sexo(persona.getSexo())
                .direccion(persona.getDireccion())
                .telefono(persona.getTelefono())
                .email(persona.getEmail())
                .build();
    }

    private Persona toEntity(PersonaDTO dto) {
        return Persona.builder()
                .tipoDocumento(dto.getTipoDocumento())
                .documento(dto.getDocumento())
                .primerNombre(dto.getPrimerNombre())
                .segundoNombre(dto.getSegundoNombre())
                .primerApellido(dto.getPrimerApellido())
                .segundoApellido(dto.getSegundoApellido())
                .nombreCompleto(dto.getNombreCompleto())
                .fechaNacimiento(dto.getFechaNacimiento())
                .sexo(dto.getSexo())
                .direccion(dto.getDireccion())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .build();
    }
}
