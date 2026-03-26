package com.clinica.api.modules.usuarios.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private UUID id;
  private String refreshToken;
  private String username;
  private String fullName;
  private List<String> roles;

  public JwtResponse(String accessToken, String refreshToken, UUID id, String username, String fullName, List<String> roles) {
    this.token = accessToken;
    this.refreshToken = refreshToken;
    this.id = id;
    this.username = username;
    this.fullName = fullName;
    this.roles = roles;
  }
}
