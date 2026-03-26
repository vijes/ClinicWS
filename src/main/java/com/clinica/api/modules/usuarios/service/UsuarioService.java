package com.clinica.api.modules.usuarios.service;

import com.clinica.api.modules.usuarios.domain.Role;
import com.clinica.api.modules.usuarios.repository.RoleRepository;
import com.clinica.api.modules.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public void toggleRoleStatus(UUID roleId, boolean active) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        if (!active) {
            long activeUsers = usuarioRepository.countByUserRolesRoleIdAndActivoTrue(roleId);
            if (activeUsers > 0) {
                throw new RuntimeException("No se puede desactivar el rol porque tiene " + activeUsers + " usuarios activos.");
            }
        }

        role.setActivo(active);
        roleRepository.save(role);
    }
}
