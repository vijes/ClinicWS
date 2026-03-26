package com.clinica.api.config.security.jwt;

import com.clinica.api.config.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private final JwtService jwtService;
  private final UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;
    
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      logger.debug("No Bearer token found in request to {}", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }
    
    try {
      jwt = authHeader.substring(7);
      logger.info("Extracted JWT: {}", jwt);
      
      username = jwtService.extractUsername(jwt);
      logger.info("Extracted Username from JWT: {}", username);
      
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        logger.info("Loaded UserDetails for: {}", userDetails.getUsername());
        
        if (jwtService.isTokenValid(jwt, userDetails)) {
          logger.info("Token is valid. Setting security context.");
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities()
          );
          authToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request)
          );
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          logger.warn("Token is NOT valid for user: {}", username);
        }
      }
    } catch (Exception e) {
      // Log the exception, but do not throw it, so the filter chain continues as unauthenticated
      // This handles UsernameNotFoundException if the DB was wiped, or MalformedJwtException etc.
      logger.error("Cannot set user authentication: {} - {}", e.getClass().getName(), e.getMessage());
    }
    
    filterChain.doFilter(request, response);
  }
}
