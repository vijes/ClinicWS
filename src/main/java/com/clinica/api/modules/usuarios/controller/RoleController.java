package com.clinica.api.modules.usuarios.controller;

import com.clinica.api.modules.usuarios.domain.Role;
import com.clinica.api.modules.usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(usuarioService.getAllRoles());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> toggleRoleStatus(@PathVariable UUID id, @RequestParam boolean active) {
        usuarioService.toggleRoleStatus(id, active);
        return ResponseEntity.noContent().build();
    }
}
