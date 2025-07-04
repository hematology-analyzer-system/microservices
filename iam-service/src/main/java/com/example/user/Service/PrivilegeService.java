package com.example.user.service;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.example.user.model.Privilege;
import com.example.user.repository.PrivilegeRepository;

@Service
public class PrivilegeService {
    private final PrivilegeRepository privilegeRepository;

    public PrivilegeService(PrivilegeRepository privilegeRepository) {
        this.privilegeRepository = privilegeRepository;
    }

    public Privilege createPrivilege(Privilege privilege) {
        return privilegeRepository.save(privilege);
    }

    public List<Privilege> getAllPrivileges() {
        return privilegeRepository.findAll();
    }

    public Optional<Privilege> getPrivilegeById(Long id) {
        return privilegeRepository.findById(id);
    }

    public void deletePrivilege(Long id) {
        privilegeRepository.deleteById(id);
    }
}
