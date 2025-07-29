package com.example.user.controller;

import com.example.user.dto.role.PageRoleResponse;
import com.example.user.dto.role.RoleRequest;
import com.example.user.model.Role;
import com.example.user.service.RoleService;
import com.example.user.dto.role.UpdateRoleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<UpdateRoleRequest> create(@RequestBody UpdateRoleRequest role) {
        if (roleService.createRole(role).isPresent()) return ResponseEntity.ok(role);
        else return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateRoleRequest> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest dto) {
        dto.setRoleId(id);
        Optional<UpdateRoleRequest> updated = roleService.updateRole(dto);
        if (updated.isPresent()) return ResponseEntity.ok(dto);
        else return ResponseEntity.badRequest().build();
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

//    @DeleteMapping("/{roleId}/privileges/{privilegeId}")
//    public ResponseEntity<String> removePrivilege(
//            @PathVariable Long roleId,
//            @PathVariable Long privilegeId) {
//        roleService.removePrivilegeFromRole(roleId, privilegeId);
//        return ResponseEntity.ok("Privilege removed from role successfully.");
//    }
//

    @GetMapping("/paging")
    public ResponseEntity<PageRoleResponse> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return ResponseEntity.ok(roleService.getAllRoles(page, size, sortBy, sortDirection));
    }

    @PostMapping("/filter")
    public ResponseEntity<PageRoleResponse> searchRoles(@RequestBody RoleRequest request) {
        return ResponseEntity.ok(roleService.searchRoles(request));
    }
}