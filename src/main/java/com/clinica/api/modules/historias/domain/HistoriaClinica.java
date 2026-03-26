package com.clinica.api.modules.historias.domain;

import com.clinica.api.common.domain.BaseEntity;
import com.clinica.api.modules.pacientes.domain.Paciente;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing a clinical history record for a patient.
 */
@Entity
@Table(name = "HISTORIAS_CLINICAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriaClinica extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PACIENTE_ID", nullable = false)
    private Paciente paciente;

    @Column(name = "DIAGNOSTICO", length = 1500, nullable = false)
    private String diagnostico;

    @Column(name = "TRATAMIENTO", length = 1500, nullable = false)
    private String tratamiento;

    @Column(name = "RECETA", length = 1500)
    private String receta;
}
