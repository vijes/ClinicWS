package com.clinica.api.modules.historias.service;

import com.clinica.api.modules.historias.domain.HistoriaClinica;
import com.clinica.api.modules.historias.dto.HistoriaClinicaDTO;
import com.clinica.api.modules.historias.dto.HistoriaFilterDTO;
import com.clinica.api.modules.historias.repository.HistoriaClinicaRepository;
import com.clinica.api.modules.pacientes.domain.Paciente;
import com.clinica.api.modules.pacientes.repository.PacienteRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoriaClinicaService {

    private final HistoriaClinicaRepository historiaRepository;
    private final PacienteRepository pacienteRepository;

    @Transactional
    public HistoriaClinicaDTO crearHistoria(HistoriaClinicaDTO dto) {
        Paciente paciente = pacienteRepository.findById(dto.getPacienteId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        HistoriaClinica historia = HistoriaClinica.builder()
                .paciente(paciente)
                .diagnostico(dto.getDiagnostico())
                .tratamiento(dto.getTratamiento())
                .receta(dto.getReceta())
                .build();

        // userCreate y dateCreate se inyectan solos por JPA Auditing
        historia = historiaRepository.save(historia);

        return toDTO(historia);
    }

    @Transactional(readOnly = true)
    public List<HistoriaClinicaDTO> filtrarHistorias(HistoriaFilterDTO filter) {
        Specification<HistoriaClinica> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getPacienteId() != null) {
                predicates.add(cb.equal(root.get("paciente").get("id"), filter.getPacienteId()));
            }

            LocalDateTime desde = filter.getFechaDesde();
            LocalDateTime hasta = filter.getFechaHasta();

            // Lógica por defecto: si no hay fechas, traemos de los últimos 30 días
            if (desde == null && hasta == null) {
                desde = LocalDateTime.now().minusDays(30);
                hasta = LocalDateTime.now();
            }

            if (desde != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateCreate"), desde));
            }
            if (hasta != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateCreate"), hasta));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Ordenar descendentemente por fecha registro
        Sort sort = Sort.by(Sort.Direction.DESC, "dateCreate");

        return historiaRepository.findAll(spec, sort).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private HistoriaClinicaDTO toDTO(HistoriaClinica h) {
        return HistoriaClinicaDTO.builder()
                .id(h.getId())
                .pacienteId(h.getPaciente().getId())
                .diagnostico(h.getDiagnostico())
                .tratamiento(h.getTratamiento())
                .receta(h.getReceta())
                .fechaRegistro(h.getDateCreate())
                .medicoEvaluador(h.getUserCreate()) // Extraído directamente de la auditoría JPA
                .build();
    }
}
