package com.clinica.api.config.security;

import com.clinica.api.config.security.jwt.AuthEntryPointJwt;
import com.clinica.api.config.security.jwt.JwtAuthenticationFilter;
import com.clinica.api.config.security.services.UserDetailsServiceImpl;
import com.clinica.api.config.security.utils.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.clinica.api.config.security.utils.CustomPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Value("${clinica.app.cors.allowed-origins}")
  private List<String> allowedOrigins;

  @Bean
  public CryptoUtils cryptoUtils() {
    return new CryptoUtils();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new CustomPasswordEncoder(cryptoUtils());
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .headers(headers -> headers.frameOptions(frame -> frame.disable()))
        .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> 
          auth.requestMatchers(AntPathRequestMatcher.antMatcher("/api/auth/**")).permitAll()
              .requestMatchers(AntPathRequestMatcher.antMatcher("/api/public/**")).permitAll()
              .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
              .requestMatchers(AntPathRequestMatcher.antMatcher("/api/test/**")).permitAll()
              .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
              .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll()
              .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui.html")).permitAll()
              .requestMatchers(AntPathRequestMatcher.antMatcher(org.springframework.http.HttpMethod.OPTIONS, "/**")).permitAll()
              .anyRequest().authenticated()
        );
    
    http.authenticationProvider(authenticationProvider());

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(allowedOrigins);
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Auth-Token"));
    configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
