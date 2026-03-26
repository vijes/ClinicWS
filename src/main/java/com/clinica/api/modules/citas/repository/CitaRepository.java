package com.clinica.api.modules.citas.repository;

import com.clinica.api.modules.citas.domain.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CitaRepository extends JpaRepository<Cita, UUID>, JpaSpecificationExecutor<Cita> {
    List<Cita> findByPacienteId(UUID pacienteId);
    List<Cita> findByProfesionalId(UUID profesionalId);
}
