package com.example.user.controller;

import com.example.user.model.Privilege;
import com.example.user.service.PrivilegeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/privileges")
public class PrivilegeController {
    @org.springframework.beans.factory.annotation.Autowired
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;
    private final PrivilegeService privilegeService;

    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @PostMapping
    public ResponseEntity<Privilege> create(@RequestBody Privilege privilege) {
        Privilege created = privilegeService.createPrivilege(privilege);
        // Send RabbitMQ message after privilege creation
        rabbitTemplate.convertAndSend("appExchange", "privilege.create", "Privilege created: " + created.getPrivilegeId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Privilege>> list() {
        List<Privilege> privileges = privilegeService.getAllPrivileges();
        // Send RabbitMQ message after listing privileges
        rabbitTemplate.convertAndSend("appExchange", "privilege.list", "Privileges listed: count=" + privileges.size());
        return ResponseEntity.ok(privileges);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Privilege> get(@PathVariable Long id) {
        var result = privilegeService.getPrivilegeById(id);
        // Send RabbitMQ message after getting privilege
        rabbitTemplate.convertAndSend("appExchange", "privilege.get", "Privilege get: id=" + id + ", found=" + result.isPresent());
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        privilegeService.deletePrivilege(id);
        // Send RabbitMQ message after deleting privilege
        rabbitTemplate.convertAndSend("appExchange", "privilege.delete", "Privilege deleted: id=" + id);
        return ResponseEntity.noContent().build();
    }
}
