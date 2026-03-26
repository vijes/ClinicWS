package com.clinica.api.modules.clinica.service;

import com.clinica.api.common.service.EmailService;
import com.clinica.api.common.util.RucValidator;
import com.clinica.api.modules.clinica.domain.Clinica;
import com.clinica.api.modules.clinica.dto.ClinicaDTO;
import com.clinica.api.modules.clinica.repository.ClinicaRepository;
import com.clinica.api.modules.personas.domain.Persona;
import com.clinica.api.modules.personas.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicaServiceImpl implements ClinicaService {

    private final ClinicaRepository clinicaRepository;
    private final PersonaRepository personaRepository;
    private final RucValidator rucValidator;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public ClinicaDTO registerClinica(ClinicaDTO dto) {
        if (!rucValidator.isValid(dto.getRuc())) {
            throw new RuntimeException("RUC no válido para el Ecuador.");
        }

        if (clinicaRepository.existsByRuc(dto.getRuc())) {
            throw new RuntimeException("El RUC ya está registrado.");
        }

        // 1. Manejar representante
        Persona representante = personaRepository.findByDocumento(dto.getCedulaRepresentante())
                .orElseGet(() -> {
                    Persona newPersona = Persona.builder()
                            .documento(dto.getCedulaRepresentante())
                            .tipoDocumento("CEDULA")
                            .primerNombre(dto.getPrimerNombre())
                            .primerApellido(dto.getPrimerApellido())
                            .email(dto.getEmail())
                            .build();
                    return personaRepository.save(newPersona);
                });

        // 2. Generar código de acceso
        String accessCode = generateAccessCode(dto);

        // 3. Crear Clinica
        Clinica clinica = Clinica.builder()
                .ruc(dto.getRuc())
                .razonSocial(dto.getRazonSocial())
                .nombreComercial(dto.getNombreComercial())
                .primerNombre(dto.getPrimerNombre())
                .segundoNombre(dto.getSegundoNombre())
                .primerApellido(dto.getPrimerApellido())
                .segundoApellido(dto.getSegundoApellido())
                .esEmpresa(dto.isEsEmpresa())
                .telefonoCelular(dto.getTelefonoCelular())
                .telefonoConvencional(dto.getTelefonoConvencional())
                .email(dto.getEmail())
                .representante(representante)
                .codigoAccesoPortal(accessCode)
                .build();
        
        // Asignar company code (audit requirement)
        clinica.setCompanyCode(dto.getRuc().substring(0, 10));
        clinica.setUserCreate("ADMIN_SYSTEM"); // Placeholder

        clinica = clinicaRepository.save(clinica);

        // 4. Enviar correos
        sendWelcomeEmails(clinica);

        return mapToDTO(clinica);
    }

    private String generateAccessCode(ClinicaDTO dto) {
        String base = dto.getRuc().substring(0, 2);
        String namePart = dto.isEsEmpresa() ? dto.getRazonSocial() : dto.getPrimerNombre();
        String initial = namePart != null && !namePart.isEmpty() ? namePart.substring(0, 1).toUpperCase() : "X";
        
        String code;
        do {
            int num = random.nextInt(10000000);
            code = String.format("%s%s%07d", base, initial, num);
        } while (clinicaRepository.existsByCodigoAccesoPortal(code));
        
        return code;
    }

    private void sendWelcomeEmails(Clinica clinica) {
        String subject = "Bienvenido al portal clínica";
        String bodyTemplate = "Bienvenido al portal clínica.\n\n" +
                "Su clínica fue registrada con éxito.\n\n" +
                "Para registrar nuevos doctores en el sistema utilice el siguiente código de acceso al portal:\n\n" +
                "%s\n\n" +
                "Si tiene alguna novedad comuníquese con el administrador del sistema.";
        
        String body = String.format(bodyTemplate, clinica.getCodigoAccesoPortal());
        
        emailService.sendEmail(clinica.getEmail(), subject, body);
        if (clinica.getRepresentante() != null && clinica.getRepresentante().getEmail() != null) {
            emailService.sendEmail(clinica.getRepresentante().getEmail(), subject, body);
        }
    }

    @Override
    public ClinicaDTO getClinicaById(UUID id) {
        return clinicaRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Clínica no encontrada"));
    }

    @Override
    public List<ClinicaDTO> getAllClinicas() {
        return clinicaRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClinicaDTO updateClinica(UUID id, ClinicaDTO dto) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Clínica no encontrada"));
        
        clinica.setRazonSocial(dto.getRazonSocial());
        clinica.setNombreComercial(dto.getNombreComercial());
        clinica.setTelefonoCelular(dto.getTelefonoCelular());
        clinica.setTelefonoConvencional(dto.getTelefonoConvencional());
        clinica.setEmail(dto.getEmail());
        
        return mapToDTO(clinicaRepository.save(clinica));
    }

    @Override
    @Transactional
    public void deleteClinica(UUID id) {
        clinicaRepository.deleteById(id);
    }

    @Override
    public boolean validateAccessCode(UUID clinicaId, String code) {
        return clinicaRepository.existsByIdAndCodigoAccesoPortal(clinicaId, code);
    }

    private ClinicaDTO mapToDTO(Clinica clinica) {
        return ClinicaDTO.builder()
                .id(clinica.getId())
                .ruc(clinica.getRuc())
                .razonSocial(clinica.getRazonSocial())
                .nombreComercial(clinica.getNombreComercial())
                .primerNombre(clinica.getPrimerNombre())
                .segundoNombre(clinica.getSegundoNombre())
                .primerApellido(clinica.getPrimerApellido())
                .segundoApellido(clinica.getSegundoApellido())
                .esEmpresa(clinica.isEsEmpresa())
                .telefonoCelular(clinica.getTelefonoCelular())
                .telefonoConvencional(clinica.getTelefonoConvencional())
                .email(clinica.getEmail())
                .cedulaRepresentante(clinica.getRepresentante() != null ? clinica.getRepresentante().getDocumento() : null)
                .codigoAccesoPortal(clinica.getCodigoAccesoPortal())
                .build();
    }
}
