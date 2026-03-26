package com.clinica.api.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedBy
    @Column(name = "USER_CREATE", nullable = false, length = 100, updatable = false)
    private String userCreate;

    @CreatedDate
    @Column(name = "DATE_CREATE", nullable = false, updatable = false)
    private LocalDateTime dateCreate;

    @LastModifiedBy
    @Column(name = "USER_MODIFICATION", length = 100)
    private String userModification;

    @LastModifiedDate
    @Column(name = "DATE_MODIFICATION")
    private LocalDateTime dateModification;

    @Column(name = "COMPANY_CODE", nullable = false, columnDefinition = "CHAR(36)")
    private String companyCode = "1";

    @jakarta.persistence.PrePersist
    public void prePersist() {
        if (this.companyCode == null || "DEFAULT".equals(this.companyCode) || this.companyCode.trim().isEmpty()) {
            this.companyCode = com.clinica.api.config.security.utils.SecurityUtils.getCurrentCompanyCode();
        }
    }
}
