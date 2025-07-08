package com.example.user.controller;

import com.example.user.dto.role.PageRoleResponse;
import com.example.user.model.Role;
import com.example.user.service.RoleService;
import com.example.user.dto.role.UpdateRoleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iam/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Role> create(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @GetMapping
    public ResponseEntity<List<Role>> list() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> get(@PathVariable Long id) {
        return roleService.getRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{roleId}/privileges/{privilegeId}")
    public ResponseEntity<String> assignPrivilegeToRole(
            @PathVariable Long roleId,
            @PathVariable Long privilegeId) {

        roleService.assignPrivilegeToRole(roleId, privilegeId);
        return ResponseEntity.ok("Privilege assigned to role successfully.");
    }
    @DeleteMapping("/{roleId}/privileges/{privilegeId}")
    public ResponseEntity<String> removePrivilege(
            @PathVariable Long roleId,
            @PathVariable Long privilegeId) {
        roleService.removePrivilegeFromRole(roleId, privilegeId);
        return ResponseEntity.ok("Privilege removed from role successfully.");
    }
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest dto) {
        Role updated = roleService.updateRole(id, dto);
        return ResponseEntity.ok(updated);
    }
    @GetMapping("/paging")
    public ResponseEntity<PageRoleResponse> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(roleService.getAllRoles(page, size, sortBy, sortDirection));
    }


}