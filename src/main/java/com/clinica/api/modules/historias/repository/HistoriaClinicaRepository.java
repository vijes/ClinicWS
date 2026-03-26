package com.clinica.api.modules.historias.repository;

import com.clinica.api.modules.historias.domain.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HistoriaClinicaRepository extends JpaRepository<HistoriaClinica, UUID>, JpaSpecificationExecutor<HistoriaClinica> {
}
