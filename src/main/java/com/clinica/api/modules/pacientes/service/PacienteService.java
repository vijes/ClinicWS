package com.clinica.api.modules.pacientes.service;

import com.clinica.api.modules.pacientes.domain.Paciente;
import com.clinica.api.modules.pacientes.dto.PacienteDTO;
import com.clinica.api.modules.pacientes.repository.PacienteRepository;
import com.clinica.api.modules.personas.domain.Persona;
import com.clinica.api.modules.personas.dto.PersonaDTO;
import com.clinica.api.modules.personas.repository.PersonaRepository;
import com.clinica.api.config.security.utils.SecurityUtils;
import com.clinica.api.modules.clinica.repository.ClinicaRepository;
import com.clinica.api.modules.clinica.domain.Clinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.clinica.api.modules.pacientes.dto.PacienteFilterDTO;
import com.clinica.api.modules.pacientes.dto.PacienteQueryDTO;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PersonaRepository personaRepository;
    private final ClinicaRepository clinicaRepository;
    private final PacienteValidationService pacienteValidationService;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<PacienteDTO> findAll() {
        if (securityUtils.isSuperAdmin()) {
            return pacienteRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        }
        
        UUID clinicaId = securityUtils.getCurrentClinicaId();
        if (clinicaId == null) return new ArrayList<>();

        return pacienteRepository.findAll((root, query, cb) -> 
            cb.equal(root.get("clinica").get("id"), clinicaId)
        ).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PacienteDTO> findByFilters(String documento, String primerNombre, String segundoNombre, String primerApellido, String segundoApellido) {
        Specification<Paciente> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (documento != null && !documento.isEmpty()) {
                predicates.add(cb.like(root.get("persona").get("documento"), "%" + documento + "%"));
            }
            if (primerNombre != null && !primerNombre.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("persona").get("primerNombre")), "%" + primerNombre.toLowerCase() + "%"));
            }
            if (segundoNombre != null && !segundoNombre.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("persona").get("segundoNombre")), "%" + segundoNombre.toLowerCase() + "%"));
            }
            if (primerApellido != null && !primerApellido.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("persona").get("primerApellido")), "%" + primerApellido.toLowerCase() + "%"));
            }
            if (segundoApellido != null && !segundoApellido.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("persona").get("segundoApellido")), "%" + segundoApellido.toLowerCase() + "%"));
            }
            
            if (!securityUtils.isSuperAdmin()) {
                UUID clinicaId = securityUtils.getCurrentClinicaId();
                if (clinicaId != null) {
                    predicates.add(cb.equal(root.get("clinica").get("id"), clinicaId));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return pacienteRepository.findAll(spec).stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PacienteDTO findById(UUID id) {
        return pacienteRepository.findById(id)
            .map(this::toDTO)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
    }

    @Transactional
    public PacienteDTO save(PacienteDTO dto) {
        PersonaDTO personaDTO = dto.getPersona();
        Optional<Persona> personaOpt = personaRepository.findByDocumento(personaDTO.getDocumento());
        
        Persona persona;
        if (personaOpt.isPresent()) {
            persona = personaOpt.get();
            if (pacienteRepository.findByPersonaId(persona.getId()).isPresent()) {
                throw new RuntimeException("La persona ya está registrada como paciente");
            }
        } else {
            persona = Persona.builder()
                .tipoDocumento(personaDTO.getTipoDocumento())
                .documento(personaDTO.getDocumento())
                .primerNombre(personaDTO.getPrimerNombre())
                .segundoNombre(personaDTO.getSegundoNombre())
                .primerApellido(personaDTO.getPrimerApellido())
                .segundoApellido(personaDTO.getSegundoApellido())
                .fechaNacimiento(personaDTO.getFechaNacimiento())
                .sexo(personaDTO.getSexo())
                .direccion(personaDTO.getDireccion())
                .telefono(personaDTO.getTelefono())
                .email(personaDTO.getEmail())
                .build();
            persona = personaRepository.save(persona);
        }

        UUID clinicaId = securityUtils.getCurrentClinicaId();
        Clinica clinica = null;
        if (clinicaId != null) {
            clinica = clinicaRepository.findById(clinicaId)
                    .orElseThrow(() -> new RuntimeException("Clínica del usuario no encontrada"));
        } else if (!securityUtils.isSuperAdmin()) {
            throw new RuntimeException("El usuario debe pertenecer a una clínica para registrar pacientes");
        }

        Paciente paciente = Paciente.builder()
            .persona(persona)
            .clinica(clinica)
            .tipoSangre(dto.getTipoSangre())
            .seguroMedico(dto.getSeguroMedico())
            .contactoEmergencia(dto.getContactoEmergencia())
            .observaciones(dto.getObservaciones())
            .build();

        return toDTO(pacienteRepository.save(paciente));
    }

    @Transactional
    public PacienteDTO update(UUID id, PacienteDTO dto) {
        Paciente paciente = pacienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        
        Persona persona = paciente.getPersona();
        PersonaDTO pDto = dto.getPersona();
        persona.setTipoDocumento(pDto.getTipoDocumento());
        persona.setDocumento(pDto.getDocumento());
        persona.setPrimerNombre(pDto.getPrimerNombre());
        persona.setSegundoNombre(pDto.getSegundoNombre());
        persona.setPrimerApellido(pDto.getPrimerApellido());
        persona.setSegundoApellido(pDto.getSegundoApellido());
        persona.setFechaNacimiento(pDto.getFechaNacimiento());
        persona.setSexo(pDto.getSexo());
        persona.setDireccion(pDto.getDireccion());
        persona.setTelefono(pDto.getTelefono());
        persona.setEmail(pDto.getEmail());
        personaRepository.save(persona);

        paciente.setTipoSangre(dto.getTipoSangre());
        paciente.setSeguroMedico(dto.getSeguroMedico());
        paciente.setContactoEmergencia(dto.getContactoEmergencia());
        paciente.setObservaciones(dto.getObservaciones());

        return toDTO(pacienteRepository.save(paciente));
    }

    @Transactional
    public void delete(UUID id) {
        pacienteRepository.deleteById(id);
    }

    // --- Nuevos metodos solicitados ---

    @Transactional
    public PacienteDTO createPacient(PacienteDTO dto) {
        // Principio SOLID: Delegamos la validación a un Service específico de validaciones
        pacienteValidationService.validateCreation(dto);
        return save(dto);
    }

    @Transactional(readOnly = true)
    public PacienteDTO obtenerPaciente(PacienteQueryDTO query) {
        Optional<Persona> personaOpt = personaRepository.findByDocumento(query.getDocumento());
        if (personaOpt.isPresent() && personaOpt.get().getTipoDocumento().equalsIgnoreCase(query.getTipoDocumento())) {
            Optional<Paciente> pacienteOpt = pacienteRepository.findByPersonaId(personaOpt.get().getId());
            if (pacienteOpt.isPresent()) {
                return toDTO(pacienteOpt.get());
            }
        }
        throw new IllegalArgumentException("El paciente no existe.");
    }

    @Transactional(readOnly = true)
    public List<PacienteDTO> obtenerListaPacientes(PacienteFilterDTO filter) {
        return findByFilters(
            filter.getDocumento(),
            filter.getPrimerNombre(),
            filter.getSegundoNombre(),
            filter.getPrimerApellido(),
            filter.getSegundoApellido()
        );
    }

    private PacienteDTO toDTO(Paciente paciente) {
        Persona p = paciente.getPersona();
        PersonaDTO pDto = PersonaDTO.builder()
            .id(p.getId())
            .tipoDocumento(p.getTipoDocumento())
            .documento(p.getDocumento())
            .primerNombre(p.getPrimerNombre())
            .segundoNombre(p.getSegundoNombre())
            .primerApellido(p.getPrimerApellido())
            .segundoApellido(p.getSegundoApellido())
            .nombreCompleto(p.getNombreCompleto())
            .fechaNacimiento(p.getFechaNacimiento())
            .sexo(p.getSexo())
            .direccion(p.getDireccion())
            .telefono(p.getTelefono())
            .email(p.getEmail())
            .build();

        return PacienteDTO.builder()
            .id(paciente.getId())
            .persona(pDto)
            .tipoSangre(paciente.getTipoSangre())
            .seguroMedico(paciente.getSeguroMedico())
            .contactoEmergencia(paciente.getContactoEmergencia())
            .observaciones(paciente.getObservaciones())
            .build();
    }
}
