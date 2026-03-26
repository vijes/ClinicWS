package com.clinica.api.modules.usuarios.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "REFRESH_TOKENS", schema = "SCCLINIC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "USUARIO_ID", referencedColumnName = "ID")
    private Usuario user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false, name = "EXPIRY_DATE")
    private Instant expiryDate;

    // Audit fields
    @Column(name = "USER_CREATE", nullable = false)
    private String userCreate;

    @Column(name = "DATE_CREATE", nullable = false, updatable = false)
    private Instant dateCreate;

    @Column(name = "COMPANY_CODE", nullable = false)
    private String companyCode;

    @PrePersist
    protected void onCreate() {
        this.dateCreate = Instant.now();
        if (this.userCreate == null) this.userCreate = "SYSTEM";
        if (this.companyCode == null) this.companyCode = "CLINIC01";
    }
}
