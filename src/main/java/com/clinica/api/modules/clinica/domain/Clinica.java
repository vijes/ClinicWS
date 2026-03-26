package com.clinica.api.modules.clinica.domain;

import com.clinica.api.common.domain.BaseEntity;
import com.clinica.api.modules.personas.domain.Persona;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "CLINICA", schema = "SCCLINIC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clinica extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(name = "RUC", nullable = false, unique = true, length = 13)
    private String ruc;

    @Column(name = "RAZON_SOCIAL", length = 200)
    private String razonSocial;

    @Column(name = "NOMBRE_COMERCIAL", length = 200)
    private String nombreComercial;

    @Column(name = "PRIMER_NOMBRE", length = 50)
    private String primerNombre;

    @Column(name = "SEGUNDO_NOMBRE", length = 50)
    private String segundoNombre;

    @Column(name = "PRIMER_APELLIDO", length = 50)
    private String primerApellido;

    @Column(name = "SEGUNDO_APELLIDO", length = 50)
    private String segundoApellido;

    @Column(name = "ES_EMPRESA", nullable = false)
    private boolean esEmpresa;

    @Column(name = "TELEFONO_CELULAR", length = 20)
    private String telefonoCelular;

    @Column(name = "TELEFONO_CONVENCIONAL", length = 20)
    private String telefonoConvencional;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPRESENTANTE_ID")
    private Persona representante;

    @Column(name = "CODIGO_ACCESO_PORTAL", nullable = false, unique = true, length = 10)
    private String codigoAccesoPortal;
}
