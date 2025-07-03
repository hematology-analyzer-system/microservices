package com.example.user.Controller;

import com.example.user.Model.Role;
import com.example.user.Service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
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
}