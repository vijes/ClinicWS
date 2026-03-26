package com.clinica.api.modules.clinica.repository;

import com.clinica.api.modules.clinica.domain.Clinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClinicaRepository extends JpaRepository<Clinica, UUID> {
    Optional<Clinica> findByRuc(String ruc);
    Optional<Clinica> findByCodigoAccesoPortal(String codigoAccesoPortal);
    boolean existsByRuc(String ruc);
    boolean existsByCodigoAccesoPortal(String codigoAccesoPortal);
    boolean existsByIdAndCodigoAccesoPortal(UUID id, String codigoAccesoPortal);
}
