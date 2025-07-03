package com.example.user.Service;
import com.example.user.Model.Privilege;
import com.example.user.Model.Role;
import com.example.user.repository.PrivilegeRepository;
import com.example.user.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    public RoleService(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
    public void assignPrivilegeToRole(Long roleId, Long privilegeId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Privilege privilege = privilegeRepository.findById(privilegeId)
                .orElseThrow(() -> new RuntimeException("Privilege not found"));

        role.getPrivileges().add(privilege);
        roleRepository.save(role);
    }
}


