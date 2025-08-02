package com.example.user.controller;

import com.example.user.dto.role.PageRoleResponse;
import com.example.user.dto.role.RoleRequest;
import com.example.user.model.Role;
import com.example.user.model.UserAuditLog;
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
    UserAuditLog auditLog = new UserAuditLog();

    public RoleController(RoleService roleService, RabbitTemplate rabbitTemplate) {
        this.roleService = roleService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<UpdateRoleRequest> create(@RequestBody UpdateRoleRequest role) {
        if (roleService.createRole(role).isPresent()) {
            // Return the created role
            auditLog.setDetails("Role created: " + role.getName());
            rabbitTemplate.convertAndSend("appExchange", "role.create", auditLog);
            return ResponseEntity.ok(role);
        }
        else return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateRoleRequest> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest dto) {
        dto.setRoleId(id);
        Optional<UpdateRoleRequest> updated = roleService.updateRole(dto);
        if (updated.isPresent()) {
            auditLog.setDetails("Role updated: " + dto.getName());
            rabbitTemplate.convertAndSend("appExchange", "role.update", auditLog);
            return ResponseEntity.ok(dto);
        }
        else {
            auditLog.setDetails("Role update failed: id=" + id);
            rabbitTemplate.convertAndSend("appExchange", "role.update.failed", auditLog);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Role>> list() {
        List<Role> roles = roleService.getAllRoles();
        // Send RabbitMQ message after listing roles
        auditLog.setDetails("Roles listed: count=" + roles.size());
        rabbitTemplate.convertAndSend("appExchange", "role.list", auditLog);
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> get(@PathVariable Long id) {
        var result = roleService.getRoleById(id);
        // Send RabbitMQ message after getting role
        auditLog.setDetails("Role get: id=" + id + ", found=" + result.isPresent());
        rabbitTemplate.convertAndSend("appExchange", "role.get", auditLog);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        // Send RabbitMQ message after deleting role
        auditLog.setDetails("Role deleted: id=" + id);
        rabbitTemplate.convertAndSend("appExchange", "role.delete", auditLog);
        return ResponseEntity.noContent().build();
    }

//    @DeleteMapping("/{roleId}/privileges/{privilegeId}")
//    public ResponseEntity<String> removePrivilege(
//            @PathVariable Long roleId,
//            @PathVariable Long privilegeId) {
//        roleService.removePrivilegeFromRole(roleId, privilegeId);
//        return ResponseEntity.ok("Privilege removed from role successfully.");
//    }

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