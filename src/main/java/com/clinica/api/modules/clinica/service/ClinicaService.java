package com.clinica.api.modules.clinica.service;

import com.clinica.api.modules.clinica.dto.ClinicaDTO;
import java.util.List;
import java.util.UUID;

public interface ClinicaService {
    ClinicaDTO registerClinica(ClinicaDTO clinicaDTO);
    ClinicaDTO getClinicaById(UUID id);
    List<ClinicaDTO> getAllClinicas();
    ClinicaDTO updateClinica(UUID id, ClinicaDTO clinicaDTO);
    void deleteClinica(UUID id);
    boolean validateAccessCode(UUID clinicaId, String code);
}
