package com.clinica.api.modules.usuarios.domain;

import com.clinica.api.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "USUARIO_ROLES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRole extends BaseEntity {

    @EmbeddedId
    private UsuarioRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "USUARIO_ID")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "ROLE_ID")
    private Role role;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UsuarioRoleId implements Serializable {
        @Column(name = "USUARIO_ID", columnDefinition = "CHAR(36)")
        private UUID usuarioId;

        @Column(name = "ROLE_ID", columnDefinition = "CHAR(36)")
        private UUID roleId;
    }
}
