package com.clinica.api.modules.pacientes.domain;

import com.clinica.api.common.domain.BaseEntity;
import com.clinica.api.modules.personas.domain.Persona;
import com.clinica.api.modules.clinica.domain.Clinica;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "PACIENTES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "PERSONA_ID", nullable = false, unique = true)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLINICA_ID", nullable = false)
    private Clinica clinica;

    @Column(name = "TIPO_SANGRE", length = 5)
    private String tipoSangre;

    @Column(name = "SEGURO_MEDICO", length = 100)
    private String seguroMedico;

    @Column(name = "CONTACTO_EMERGENCIA")
    private String contactoEmergencia;

    @Column(name = "OBSERVACIONES", columnDefinition = "TEXT")
    private String observaciones;
}
