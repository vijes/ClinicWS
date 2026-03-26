package com.clinica.api.modules.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshRequest {
  @NotBlank
  private String refreshToken;
}
