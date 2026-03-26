package com.clinica.api.config.security.services;

import com.clinica.api.modules.usuarios.domain.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class UserDetailsImpl implements UserDetails {
  private static final long serialVersionUID = 1L;

  private UUID id;
  private String username;
  
  @JsonIgnore
  private String password;

  private Collection<? extends GrantedAuthority> authorities;
  private UUID clinicaId;
  private String companyCode;
  private String fullName;

  public UserDetailsImpl(UUID id, String username, String password,
      Collection<? extends GrantedAuthority> authorities, UUID clinicaId, String companyCode, String fullName) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
    this.clinicaId = clinicaId;
    this.companyCode = companyCode;
    this.fullName = fullName;
  }

  public static UserDetailsImpl build(Usuario user) {
    List<GrantedAuthority> authorities = user.getUserRoles().stream()
        .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getNombre()))
        .collect(Collectors.toList());

    String fullName = user.getPersona() != null ? 
        user.getPersona().getPrimerNombre() + " " + user.getPersona().getPrimerApellido() : 
        user.getUsername();

    return new UserDetailsImpl(
        user.getId(), 
        user.getUsername(), 
        user.getPassword(), 
        authorities,
        user.getClinica() != null ? user.getClinica().getId() : null,
        user.getCompanyCode(),
        fullName);
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }
}
