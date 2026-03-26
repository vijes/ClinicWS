package com.clinica.api.modules.pacientes.repository;

import com.clinica.api.modules.pacientes.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, UUID>, JpaSpecificationExecutor<Paciente> {
    Optional<Paciente> findByPersonaId(UUID personaId);
    Optional<Paciente> findByPersonaDocumento(String documento);
}
