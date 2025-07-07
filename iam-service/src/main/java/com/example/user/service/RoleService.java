package com.example.user.service;
import com.example.user.dto.role.PageRoleResponse;
import com.example.user.dto.role.RoleResponse;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Privilege;
import com.example.user.model.Role;
import com.example.user.dto.role.UpdateRoleRequest;
import com.example.user.repository.PrivilegeRepository;
import com.example.user.repository.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    public RoleService(RoleRepository roleRepository, PrivilegeRepository privilegeRepository) {
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }

    public Role createRole(Role role) {
        // Nếu role chưa có privilege nào được gán
        if (role.getPrivileges() == null || role.getPrivileges().isEmpty()) {
            Privilege readOnly = privilegeRepository.findByCode("READ_ONLY")
                    .orElseGet(() -> {
                        // Nếu chưa có thì tạo mới
                        Privilege p = new Privilege();
                        p.setCode("READ_ONLY");
                        p.setDescription("Default read-only privilege");
                        return privilegeRepository.save(p);
                    });

            role.setPrivileges(Set.of(readOnly));
        }

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
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        Privilege privilege = privilegeRepository.findById(privilegeId)
                .orElseThrow(() -> new ResourceNotFoundException("Privilege", "id", privilegeId));

        role.getPrivileges().add(privilege);
        roleRepository.save(role);
    }
    public Role updateRole(Long id, UpdateRoleRequest dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(dto.getName());
        role.setDescription(dto.getDescription());

        return roleRepository.save(role);
    }
    public void removePrivilegeFromRole(Long roleId, Long privilegeId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        Privilege privilege = privilegeRepository.findById(privilegeId)
                .orElseThrow(() -> new RuntimeException("Privilege not found"));

        role.getPrivileges().remove(privilege);
        roleRepository.save(role);
    }
    public PageRoleResponse getAllRoles(int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Role> rolePage = roleRepository.findAll(pageable);

        List<RoleResponse> roleResponses = rolePage.getContent().stream()
                .map(role -> new RoleResponse( role.getName(),role.getCode(), role.getDescription(), role.getPrivileges()))
                .collect(Collectors.toList());

        if (roleResponses.isEmpty()) {
            return PageRoleResponse.empty(page, size, sortBy, sortDirection);
        }

        return new PageRoleResponse(roleResponses,
                rolePage.getTotalElements(),
                rolePage.getNumber(),
                rolePage.getSize(),
                sortBy,
                sortDirection);
    }
}


