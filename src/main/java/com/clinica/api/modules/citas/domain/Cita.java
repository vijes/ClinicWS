package com.clinica.api.modules.citas.domain;

import com.clinica.api.common.domain.BaseEntity;
import com.clinica.api.modules.pacientes.domain.Paciente;
import com.clinica.api.modules.usuarios.domain.Usuario;
import com.clinica.api.modules.clinica.domain.Clinica;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CITAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cita extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "PACIENTE_ID", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "PROFESIONAL_ID", nullable = false)
    private Usuario profesional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLINICA_ID", nullable = false)
    private Clinica clinica;

    @Column(name = "FECHA_HORA", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "MOTIVO", nullable = false)
    private String motivo;

    @Column(name = "ESTADO", length = 20)
    private String estado = "PENDIENTE"; // PENDIENTE, CANCELADA, COMPLETADA
}
