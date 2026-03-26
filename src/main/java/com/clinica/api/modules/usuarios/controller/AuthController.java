package com.clinica.api.modules.usuarios.controller;

import com.clinica.api.config.security.jwt.JwtService;
import com.clinica.api.config.security.services.UserDetailsImpl;
import com.clinica.api.modules.personas.domain.Persona;
import com.clinica.api.modules.personas.repository.PersonaRepository;
import com.clinica.api.modules.usuarios.domain.RefreshToken;
import com.clinica.api.modules.usuarios.domain.Role;
import com.clinica.api.modules.usuarios.domain.Usuario;
import com.clinica.api.modules.usuarios.dto.*;
import com.clinica.api.modules.usuarios.repository.RoleRepository;
import com.clinica.api.modules.usuarios.repository.UsuarioRepository;
import com.clinica.api.modules.clinica.repository.ClinicaRepository;
import com.clinica.api.modules.clinica.domain.Clinica;
import com.clinica.api.modules.usuarios.service.AuthService;
import com.clinica.api.modules.usuarios.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for handling authentication-related requests.
 * Only handles request/response mapping, delegating logic to AuthService.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(authService.authenticateUser(loginRequest));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest)
      throws Exception {
    try{
        return ResponseEntity.status(HttpStatus.OK).body(authService.createUser(signUpRequest));
    } catch(Exception e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }



  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
    return ResponseEntity.ok(authService.refreshToken(request));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser() {
    authService.logoutUser();
    return ResponseEntity.ok(new MessageResponse("Log out successful!"));
  }

  @PostMapping("/recover")
  public ResponseEntity<?> recoverPassword(@Valid @RequestBody PasswordRecoveryRequest recoveryRequest) {
    try {
        return ResponseEntity.ok(authService.recoverPassword(recoveryRequest));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
    }
  }
}
