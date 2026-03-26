package com.clinica.api.modules.personas.domain;

import com.clinica.api.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing a person (base for users and patients).
 */
@Entity
@Table(name = "PERSONAS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(name = "TIPO_DOCUMENTO", nullable = false, length = 20)
    private String tipoDocumento;

    @Column(name = "DOCUMENTO", nullable = false, unique = true, length = 20)
    private String documento;

    @Column(name = "PRIMER_NOMBRE", nullable = false, length = 50)
    private String primerNombre;

    @Column(name = "SEGUNDO_NOMBRE", length = 50)
    private String segundoNombre;

    @Column(name = "PRIMER_APELLIDO", nullable = false, length = 50)
    private String primerApellido;

    @Column(name = "SEGUNDO_APELLIDO", length = 50)
    private String segundoApellido;

    @Column(name = "NOMBRE_COMPLETO", length = 220)
    private String nombreCompleto;

    @Column(name = "FECHA_NACIMIENTO", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "SEXO", length = 20)
    private String sexo;

    @Column(name = "DIRECCION", length = 255)
    private String direccion;

    @Column(name = "TELEFONO", length = 20)
    private String telefono;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @PrePersist
    @PreUpdate
    public void updateNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (primerNombre != null) sb.append(primerNombre);
        if (segundoNombre != null && !segundoNombre.isBlank()) sb.append(" ").append(segundoNombre);
        if (primerApellido != null) sb.append(" ").append(primerApellido);
        if (segundoApellido != null && !segundoApellido.isBlank()) sb.append(" ").append(segundoApellido);
        this.nombreCompleto = sb.toString().trim();
    }
}
