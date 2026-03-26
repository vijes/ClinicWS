package com.clinica.api.modules.usuarios.domain;

import com.clinica.api.common.domain.BaseEntity;
import com.clinica.api.modules.personas.domain.Persona;
import com.clinica.api.modules.clinica.domain.Clinica;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Entity representing a system user.
 */
@Entity
@Table(name = "USUARIOS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", columnDefinition = "CHAR(36)")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "PERSONA_ID", unique = true)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLINICA_ID")
    private Clinica clinica;

    @Column(name = "USERNAME", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;

    @Column(name = "ACTIVO", nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioRole> userRoles = new HashSet<>();

    public Set<Role> getRoles() {
        return userRoles.stream()
                .map(UsuarioRole::getRole)
                .collect(Collectors.toSet());
    }
}
