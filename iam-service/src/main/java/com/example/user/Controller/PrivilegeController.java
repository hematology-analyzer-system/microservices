package com.example.user.controller;

import com.example.user.model.Privilege;
import com.example.user.service.PrivilegeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/privileges")
public class PrivilegeController {
    private final PrivilegeService privilegeService;

    public PrivilegeController(PrivilegeService privilegeService) {
        this.privilegeService = privilegeService;
    }

    @PostMapping
    public ResponseEntity<Privilege> create(@RequestBody Privilege privilege) {
        return ResponseEntity.ok(privilegeService.createPrivilege(privilege));
    }

    @GetMapping
    public ResponseEntity<List<Privilege>> list() {
        return ResponseEntity.ok(privilegeService.getAllPrivileges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Privilege> get(@PathVariable Long id) {
        return privilegeService.getPrivilegeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        privilegeService.deletePrivilege(id);
        return ResponseEntity.noContent().build();
    }
}
