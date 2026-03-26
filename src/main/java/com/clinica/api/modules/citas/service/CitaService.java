package com.clinica.api.modules.citas.service;

import com.clinica.api.modules.citas.domain.Cita;
import com.clinica.api.modules.citas.dto.CitaDTO;
import com.clinica.api.modules.citas.repository.CitaRepository;
import com.clinica.api.modules.pacientes.domain.Paciente;
import com.clinica.api.modules.pacientes.dto.PacienteDTO;
import com.clinica.api.modules.pacientes.service.PacienteService;
import com.clinica.api.modules.usuarios.domain.Usuario;
import com.clinica.api.modules.usuarios.repository.UsuarioRepository;
import com.clinica.api.config.security.utils.SecurityUtils;
import com.clinica.api.modules.clinica.repository.ClinicaRepository;
import com.clinica.api.modules.clinica.domain.Clinica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteService pacienteService;
    private final UsuarioRepository usuarioRepository;
    private final ClinicaRepository clinicaRepository;
    private final SecurityUtils securityUtils;

    @Transactional(readOnly = true)
    public List<CitaDTO> findAll() {
        if (securityUtils.isSuperAdmin()) {
            return citaRepository.findAll().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }

        UUID clinicaId = securityUtils.getCurrentClinicaId();
        if (clinicaId == null) return new ArrayList<>();

        return citaRepository.findAll((root, query, cb) -> 
            cb.equal(root.get("clinica").get("id"), clinicaId)
        ).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> findByFilters(String documento, LocalDateTime fechaDesde, LocalDateTime fechaHasta) {
        Specification<Cita> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (documento != null && !documento.isEmpty()) {
                predicates.add(cb.like(root.get("paciente").get("persona").get("documento"), "%" + documento + "%"));
            }
            if (fechaDesde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), fechaDesde));
            }
            if (fechaHasta != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaHora"), fechaHasta));
            }
            
            if (!securityUtils.isSuperAdmin()) {
                UUID clinicaId = securityUtils.getCurrentClinicaId();
                if (clinicaId != null) {
                    predicates.add(cb.equal(root.get("clinica").get("id"), clinicaId));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return citaRepository.findAll(spec).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CitaDTO save(CitaDTO dto) {
        PacienteDTO pacienteDTO;
        
        // Si no tiene ID de paciente, intentamos buscarlo por documento o crear uno nuevo
        if (dto.getPaciente().getId() == null) {
            pacienteDTO = pacienteService.save(dto.getPaciente());
        } else {
            pacienteDTO = pacienteService.findById(dto.getPaciente().getId());
        }

        Usuario profesional = usuarioRepository.findById(dto.getProfesionalId())
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));

        UUID clinicaId = securityUtils.getCurrentClinicaId();
        Clinica clinica = null;
        if (clinicaId != null) {
            clinica = clinicaRepository.findById(clinicaId)
                    .orElseThrow(() -> new RuntimeException("Clínica del usuario no encontrada"));
        } else if (!securityUtils.isSuperAdmin()) {
            throw new RuntimeException("El usuario debe pertenecer a una clínica para registrar citas");
        }

        Cita cita = Cita.builder()
                .paciente(Paciente.builder().id(pacienteDTO.getId()).build())
                .profesional(profesional)
                .clinica(clinica)
                .fechaHora(dto.getFechaHora())
                .motivo(dto.getMotivo())
                .estado("PENDIENTE")
                .build();

        return toDTO(citaRepository.save(cita));
    }

    @Transactional
    public CitaDTO updateStatus(UUID id, String status) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setEstado(status);
        return toDTO(citaRepository.save(cita));
    }

    @Transactional
    public CitaDTO cancel(UUID id) {
        return updateStatus(id, "CANCELADA");
    }

    @Transactional
    public CitaDTO reschedule(UUID id, LocalDateTime nuevaFecha) {
        if (nuevaFecha.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se puede programar una cita en una fecha pasada");
        }
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setFechaHora(nuevaFecha);
        cita.setEstado("PENDIENTE"); // Opcional: resetear estado si estaba cancelada? El requerimiento no lo dice pero suele ser útil.
        return toDTO(citaRepository.save(cita));
    }

    private CitaDTO toDTO(Cita cita) {
        return CitaDTO.builder()
                .id(cita.getId())
                .paciente(pacienteService.findById(cita.getPaciente().getId()))
                .profesionalId(cita.getProfesional().getId())
                .fechaHora(cita.getFechaHora())
                .motivo(cita.getMotivo())
                .estado(cita.getEstado())
                .build();
    }
}
