package com.example.user.controller;

import com.example.user.dto.role.PageRoleResponse;
import com.example.user.dto.role.RoleRequest;
import com.example.user.model.Role;
import com.example.user.service.RoleService;
import com.example.user.dto.role.UpdateRoleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;
    private final RabbitTemplate rabbitTemplate;

    public RoleController(RoleService roleService, RabbitTemplate rabbitTemplate) {
        this.roleService = roleService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<UpdateRoleRequest> create(@RequestBody UpdateRoleRequest role) {
        if (roleService.createRole(role).isPresent()) {
            // Return the created role
            rabbitTemplate.convertAndSend("appExchange", "role.create", "Role created: " + role.getName());
            return ResponseEntity.ok(role);
        }
        else return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateRoleRequest> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest dto) {
        dto.setRoleId(id);
        Optional<UpdateRoleRequest> updated = roleService.updateRole(dto);
        if (updated.isPresent()) {
            rabbitTemplate.convertAndSend("appExchange", "role.update", "Role updated: " + dto.getName());
            return ResponseEntity.ok(dto);
        }
        else return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<Role>> list() {
        List<Role> roles = roleService.getAllRoles();
        // Send RabbitMQ message after listing roles
        rabbitTemplate.convertAndSend("appExchange", "role.list", "Roles listed: count=" + roles.size());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> get(@PathVariable Long id) {
        var result = roleService.getRoleById(id);
        // Send RabbitMQ message after getting role
        rabbitTemplate.convertAndSend("appExchange", "role.get", "Role get: id=" + id + ", found=" + result.isPresent());
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        // Send RabbitMQ message after deleting role
        rabbitTemplate.convertAndSend("appExchange", "role.delete", "Role deleted: id=" + id);
        return ResponseEntity.noContent().build();
    }

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 1a38ff0 (fix(iam): fix RGAB)
=======
>>>>>>> b0b8fb6 (Intergrating RabbitMQ in project)
//    @DeleteMapping("/{roleId}/privileges/{privilegeId}")
//    public ResponseEntity<String> removePrivilege(
//            @PathVariable Long roleId,
//            @PathVariable Long privilegeId) {
//        roleService.removePrivilegeFromRole(roleId, privilegeId);
//        return ResponseEntity.ok("Privilege removed from role successfully.");
//    }
//
<<<<<<< HEAD

=======
        roleService.assignPrivilegeToRole(roleId, privilegeId);
        rabbitTemplate.convertAndSend("appExchange", "role.assignPrivilege", "Assigned privilege " + privilegeId + " to role " + roleId);
        return ResponseEntity.ok("Privilege assigned to role successfully.");
    }
<<<<<<< HEAD

=======
>>>>>>> b0b8fb6 (Intergrating RabbitMQ in project)
    @DeleteMapping("/{roleId}/privileges/{privilegeId}")
    public ResponseEntity<String> removePrivilege(
            @PathVariable Long roleId,
            @PathVariable Long privilegeId) {
        roleService.removePrivilegeFromRole(roleId, privilegeId);
        rabbitTemplate.convertAndSend("appExchange", "role.removePrivilege", "Removed privilege " + privilegeId + " from role " + roleId);
        return ResponseEntity.ok("Privilege removed from role successfully.");
    }
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> b0b8fb6 (Intergrating RabbitMQ in project)
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest dto) {
        Role updated = roleService.updateRole(id, dto);
        return ResponseEntity.ok(updated);
    }
>>>>>>> 06c6b8d (Intergrating RabbitMQ in project)
<<<<<<< HEAD
=======


=======
>>>>>>> 1a38ff0 (fix(iam): fix RGAB)

>>>>>>> 1acd5c3 (fix(iam): fix feature assign role and create role)
=======
>>>>>>> b0b8fb6 (Intergrating RabbitMQ in project)
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