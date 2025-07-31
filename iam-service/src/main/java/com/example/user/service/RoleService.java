package com.example.user.service;
import com.example.user.dto.role.*;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.ModifiedHistory;
import com.example.user.model.Privilege;
import com.example.user.model.Role;
import com.example.user.model.UserAuditInfo;
import com.example.user.repository.ModifiedHistoryRepository;
import com.example.user.repository.PrivilegeRepository;
import com.example.user.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    private final AuditorAware<UserAuditInfo> auditorAware;
    private final ModifiedHistoryRepository historyRepository;

    public Optional<UpdateRoleRequest> createRole(UpdateRoleRequest dto) {
        Role role = new Role();
        applyRoleData(role, dto);
        roleRepository.save(role);

        return Optional.of(buildResponse(role));
    }

    public Optional<UpdateRoleRequest> updateRole(UpdateRoleRequest dto) {
        return roleRepository.findById(dto.getRoleId()).map(role -> {
            applyRoleData(role, dto);
            roleRepository.save(role);
            return buildResponse(role);
        });
    }

    private void applyRoleData(Role role, UpdateRoleRequest dto) {
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setCode(dto.getCode());

        if (dto.getPrivilegesIds() != null) {
            Set<Long> newPrivilegeIds = new HashSet<>(dto.getPrivilegesIds());
            Set<Long> currentPrivilegeIds = role.getPrivileges() == null ? Set.of() :
                    role.getPrivileges().stream().map(Privilege::getPrivilegeId).collect(Collectors.toSet());
            if (!newPrivilegeIds.equals(currentPrivilegeIds)) {
                Set<Privilege> privileges = new HashSet<>(privilegeRepository.findAllById(dto.getPrivilegesIds()));
                role.setPrivileges(privileges);
            }
        }
    }

    private UpdateRoleRequest buildResponse(Role role) {
        List<Long> privilegeIds = role.getPrivileges().stream()
                .map(Privilege::getPrivilegeId)
                .toList();

        return new UpdateRoleRequest(role.getRoleId(), role.getCode(), role.getName(), role.getDescription(), privilegeIds);
    }

    public Set<Long> getAllPrivilegesIds(List<Long> roleIds) {
        Set<Long> prilegeIds = new HashSet<>();
        for (Long roleId : roleIds) {
            Set<Privilege> privilegeSet = roleRepository.findPrivilegesByRoleId(roleId);
            if (privilegeSet != null) {
                prilegeIds.addAll(privilegeSet.stream().map(Privilege::getPrivilegeId).collect(Collectors.toSet()));
            }
        }
        return prilegeIds;
    }

    public void removePrivileges(Long roleId, List<Long> privilegeIds) {
        try {
            Optional<Role> roleOptional = roleRepository.findById(roleId);
            roleOptional.ifPresent(role -> role.getPrivileges().removeIf(privilege -> privilegeIds.contains(privilege.getPrivilegeId())));
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    public void assignPrivileges(Long roleId, List<Long> privilegeIds) {
        try {
            Optional<Role> roleOptional = roleRepository.findById(roleId);
            if (roleOptional.isPresent()) {
                Role role = roleOptional.get();
                List<Privilege> privileges = privilegeRepository.findAllById(privilegeIds);
                role.setPrivileges(new HashSet<>(privileges));
                roleRepository.save(role);
            } else {
                throw new EntityNotFoundException("Role not found with id: " + roleId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign privileges", e);
        }
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
        UserAuditInfo currentUser = auditorAware.getCurrentAuditor().orElse(null);
        role.setUpdatedBy(currentUser);
        role.setUpdated_at(LocalDateTime.now());
        if (currentUser != null) {
            ModifiedHistory history = new ModifiedHistory();
            history.setUpdatedAt(LocalDateTime.now());
            history.setUpdatedBy(currentUser);
            historyRepository.save(history);
        }
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
                .map(role -> new RoleResponse(role.getRoleId(), role.getName(),role.getCode(), role.getDescription(), role.getPrivileges()))
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

    public PageRoleResponse searchRoles(RoleRequest request) {
        String keyword = request.getFilter() != null ? request.getFilter() : "";
        String sortBy = request.getSort() != null ? request.getSort() : "id";
        int page = request.getPage_num() > 0 ? request.getPage_num() - 1 : 0;
        int pageSize = request.getPage_size(); // hoặc có thể cho phép cấu hình từ client

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sortBy).ascending());

        Page<Role> rolePage;

        if ("code".equalsIgnoreCase(sortBy)) {
            rolePage = roleRepository.findByCodeContainingIgnoreCase(keyword, pageable);
        } else {
            rolePage = roleRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }

        List<RoleResponse> roles = rolePage.getContent().stream()
                .map(r -> new RoleResponse(r.getRoleId(), r.getName(), r.getCode(), r.getDescription(), r.getPrivileges()))
                .collect(Collectors.toList());

        return new PageRoleResponse(
                roles,
                rolePage.getTotalElements(),
                page,
                pageSize,
                sortBy,
                "asc"
        );
    }

    public Page<RoleSearchDTO> getFilteredRoles(
            String searchText,
            Map<String, String> filter, // Use String for map values as per controller
            String sortBy,
            String direction,
            int offsetPage,
            int limitOnePage
    ) {
        // Prepare Pageable for pagination and sorting
        // offsetPage - 1 because Spring Data JPA uses 0-indexed pages
        Pageable pageable = PageRequest.of(offsetPage - 1, limitOnePage,
                Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

        // Start with a base specification that's always true
        Specification<Role> spec = (root, query, cb) -> cb.conjunction();

        // Full-text search on relevant Role fields (e.g., name, code, description)
        if (searchText != null && !searchText.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + searchText.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("code")), "%" + searchText.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + searchText.toLowerCase() + "%")
            ));
        }

        // Apply filters based on the 'filter' map
        if (filter != null) {
            // Example: Filter by role name (exact match or like, depending on requirement)
            if (filter.containsKey("name")) {
                String name = filter.get("name").toString();
                spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            // Example: Filter by role code
            if (filter.containsKey("code")) {
                String code = filter.get("code").toString();
                spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("code")), "%" + code.toLowerCase() + "%"));
            }
            // Example: Filter by privilege code/description
            if (filter.containsKey("privilege")) {
                String privilegeText = filter.get("privilege").toString();
                spec = spec.and((root, query, cb) -> cb.or(
                        cb.like(cb.lower(root.join("privileges").get("code")), "%" + privilegeText.toLowerCase() + "%"),
                        cb.like(cb.lower(root.join("privileges").get("description")), "%" + privilegeText.toLowerCase() + "%")
                ));
            }
            // Add more filters as needed (e.g., createdBy, updatedBy, etc.)
        }

        // Execute the query with the built specification and pageable
        Page<Role> rolesPage = roleRepository.findAll(spec, pageable);

        // Map Page<Role> entities to Page<RoleSearchDTO>
        return rolesPage.map(role -> {
            RoleSearchDTO dto = new RoleSearchDTO();
            dto.setId(role.getRoleId()); // Assuming Role has getRoleId()
            dto.setName(role.getName());
            dto.setCode(role.getCode());
            dto.setDescription(role.getDescription());
            dto.setCreated_at(role.getCreated_at() != null ? LocalDateTime.parse(role.getCreated_at().toString()) : null); // Assuming Role has getCreate_at()
            dto.setUpdated_at(role.getUpdated_at() != null ? LocalDateTime.parse(role.getUpdated_at().toString()) : null); // Assuming Role has getUpdate_at()

            // Map privileges to descriptions or codes
            dto.setPrivileges(role.getPrivileges().stream()
                    .map(Privilege::getDescription) // Or .map(Privilege::getCode) if preferred
                    .collect(Collectors.toSet()));
            return dto;
        });
    }
}

