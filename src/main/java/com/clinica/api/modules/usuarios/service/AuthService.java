package com.clinica.api.modules.usuarios.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.clinica.api.modules.clinica.domain.Clinica;
import com.clinica.api.modules.clinica.repository.ClinicaRepository;
import com.clinica.api.modules.personas.domain.Persona;
import com.clinica.api.modules.personas.repository.PersonaRepository;
import com.clinica.api.modules.usuarios.domain.Role;
import com.clinica.api.modules.usuarios.domain.Usuario;
import com.clinica.api.modules.usuarios.domain.UsuarioRole;
import com.clinica.api.modules.usuarios.dto.*;
import com.clinica.api.modules.usuarios.repository.RoleRepository;
import com.clinica.api.modules.usuarios.repository.UsuarioRepository;
import com.clinica.api.config.security.jwt.JwtService;
import com.clinica.api.config.security.services.UserDetailsImpl;
import com.clinica.api.modules.usuarios.domain.RefreshToken;
import com.clinica.api.modules.usuarios.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service handling authentication, user registration, and security-related operations.
 * Following SOLID principles by centralizing business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository userRepository;
    private final PersonaRepository personaRepository;
    private final RoleRepository roleRepository;
    private final ClinicaRepository clinicaRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Metodo para crear y registrar el nuevo usuario.
     * @param signUpRequest
     * @return
     */
    public Usuario createUser(SignupRequest signUpRequest) throws Exception {
        Persona persona = null;
        try{
            // 1. Validar Clínica y Código
            Clinica clinica = null;
            if (signUpRequest.getClinicaId() != null && signUpRequest.getCodigoAccesoPortal() != null) {
                clinica = clinicaRepository.findById(signUpRequest.getClinicaId())
                    .orElseThrow(() -> new RuntimeException("Error: Clínica no encontrada."));

                if (!clinica.getCodigoAccesoPortal().equals(signUpRequest.getCodigoAccesoPortal())) {
                    throw new RuntimeException("Error: El código de acceso no corresponde a la clínica seleccionada.");
                }
            } else {
                throw new RuntimeException("Error: El registro requiere selección de clínica y código de acceso.");
            }

            // Creating suggested username
            String username = generateUsernameSugerido(signUpRequest);


            Optional<Persona> personaBDD = personaRepository.findByDocumento(signUpRequest.getDocumento());
            if ( personaBDD == null || personaBDD.isEmpty()) {
                // 2. Crear la persona
                persona = Persona.builder()
                    .tipoDocumento(signUpRequest.getTipoDocumento())
                    .documento(signUpRequest.getDocumento())
                    .primerNombre(signUpRequest.getPrimerNombre())
                    .segundoNombre(signUpRequest.getSegundoNombre())
                    .primerApellido(signUpRequest.getPrimerApellido())
                    .segundoApellido(signUpRequest.getSegundoApellido())
                    .fechaNacimiento(signUpRequest.getFechaNacimiento())
                    .email(signUpRequest.getEmail())
                    .build();
                persona = personaRepository.save(persona);
            } else {
                persona = personaBDD.get();
            }

            // 3. Crear el usuario
            Usuario user = Usuario.builder()
                .username(username)
                .password(encoder.encode(signUpRequest.getPassword()))
                .persona(persona)
                .clinica(clinica)
                .activo(true)
                .build();

            user.setCompanyCode(clinica != null ? clinica.getId().toString() : "1");

            Set<String> strRoles = signUpRequest.getRole();
            Set<UsuarioRole> userRoles = new HashSet<>();

            if (clinica != null) {
                // Si viene de clínica, forzar ROLE_MEDICO
                Role medicoRole = roleRepository.findByNombre("ROLE_MEDICO")
                    .orElseThrow(() -> new RuntimeException("Error: Role no encontrado."));
                
                UsuarioRole userRole = new UsuarioRole();
                userRole.setId(new UsuarioRole.UsuarioRoleId(user.getId(), medicoRole.getId()));
                userRole.setUsuario(user);
                userRole.setRole(medicoRole);
                userRoles.add(userRole);
            } else {
                // Lógica para administración interna
                if (strRoles != null && !strRoles.isEmpty()) {
                    for (String roleStr : strRoles) {
                        if (roleStr.equalsIgnoreCase("admin")) {
                            Role adminRole = roleRepository.findByNombre("ROLE_ADMIN")
                                .orElseThrow(() -> new RuntimeException("Error: Role no encontrado."));
                            
                            UsuarioRole userRole = new UsuarioRole();
                            userRole.setId(new UsuarioRole.UsuarioRoleId(user.getId(), adminRole.getId()));
                            userRole.setUsuario(user);
                            userRole.setRole(adminRole);
                            userRoles.add(userRole);
                        }
                    }
                }
                if (userRoles.isEmpty()) {
                    throw new RuntimeException("Error: Registro público requiere código de clínica para ser médico.");
                }
            }

            user.setUserRoles(userRoles);
            userRepository.save(user);
            return user;
        } catch(Exception e) {
            log.error("No se pudo registrar  el usuario", e);
            throw new Exception("Error al registrar el nuevo usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Metodo para generar el nombre de usuario sugerido para el sistema.
     * @param request
     * @return
     */
    public String generateUsernameSugerido(SignupRequest request) {
        String pNombre = request.getPrimerNombre().trim().toLowerCase();
        String pApellido = request.getPrimerApellido().trim().toLowerCase();
        String base = (pNombre.substring(0, 1) + pApellido).replaceAll("\\s+", "");
        return base + (int)(Math.random() * 9000 + 1000);
    }

    /**
     * Metodo para generar el nombre de usuario sugerido para el sistema.
     * @param signUpRequest
     * @return
     */
    /**
     * Authenticates a user and returns a JWT response.
     * @param loginRequest The login details.
     * @return JwtResponse containing the token and user details.
     */
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        
        String jwt = jwtService.generateToken(userDetails);
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        
        List<String> roles = userDetails.getAuthorities().stream()
            .map(item -> item.getAuthority())
            .collect(Collectors.toList());

        return new JwtResponse(jwt, 
                             refreshToken.getToken(),
                             userDetails.getId(), 
                             userDetails.getUsername(), 
                             userDetails.getFullName(),
                             roles);
    }

    /**
     * Refreshes the JWT token using a refresh token.
     * @param request The token refresh request.
     * @return TokenRefreshResponse containing the new access token.
     */
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
            .map(refreshTokenService::verifyExpiration)
            .map(RefreshToken::getUser)
            .map(user -> {
              String token = jwtService.generateToken(UserDetailsImpl.build(user));
              return new TokenRefreshResponse(token, requestRefreshToken);
            })
            .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    /**
     * Logs out the current user.
     */
    public void logoutUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            refreshTokenService.deleteByUserId(userDetails.getId());
        }
    }

    /**
     * Handles password recovery.
     * @param recoveryRequest The recovery request.
     * @return MessageResponse indicating the result.
     */
    public MessageResponse recoverPassword(PasswordRecoveryRequest recoveryRequest) {
        boolean exists = userRepository.findByUsername(recoveryRequest.getUsername())
                .map(u -> u.getPersona() != null && recoveryRequest.getEmail().equalsIgnoreCase(u.getPersona().getEmail()))
                .orElse(false);

        if (exists) {
            return new MessageResponse("Se ha enviado un correo de recuperación a " + recoveryRequest.getEmail());
        } else {
            throw new RuntimeException("Error: El usuario o correo electrónico no son válidos.");
        }
    }
}
