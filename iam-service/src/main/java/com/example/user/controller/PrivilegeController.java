package com.example.user.controller;

import com.example.user.model.Privilege;
import com.example.user.service.PrivilegeService;
import com.example.user.model.UserAuditLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/privileges")
public class PrivilegeController {
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;
    private final PrivilegeService privilegeService;
    UserAuditLog auditLog = new UserAuditLog();

    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @PostMapping
    public ResponseEntity<Privilege> create(@RequestBody Privilege privilege) {
        Privilege created = privilegeService.createPrivilege(privilege);
        // Send RabbitMQ message after privilege creation
        // auditLog.setDetails("Privilege created: " + created.getPrivilegeId());
        // rabbitTemplate.convertAndSend("appExchange", "privilege.create", auditLog);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Privilege>> list() {
        List<Privilege> privileges = privilegeService.getAllPrivileges();
        // Send RabbitMQ message after listing privileges
        auditLog.setDetails("Privileges listed: count=" + privileges.size());
        rabbitTemplate.convertAndSend("appExchange", "privilege.list", auditLog);
        return ResponseEntity.ok(privileges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Privilege> get(@PathVariable Long id) {
        var result = privilegeService.getPrivilegeById(id);
        // Send RabbitMQ message after getting privilege
        auditLog.setDetails("Privilege get: id=" + id + ", found=" + result.isPresent());
        rabbitTemplate.convertAndSend("appExchange", "privilege.get", auditLog);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        privilegeService.deletePrivilege(id);
        // Send RabbitMQ message after deleting privilege
        auditLog.setDetails("Privilege deleted: id=" + id);
        rabbitTemplate.convertAndSend("appExchange", "privilege.delete", auditLog);
        return ResponseEntity.noContent().build();
    }
}
