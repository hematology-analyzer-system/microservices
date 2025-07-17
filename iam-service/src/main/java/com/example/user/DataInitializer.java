//package com.example.user;
//
//import com.example.user.model.Privilege;
//import com.example.user.model.Role;
//import com.example.user.model.User;
//import com.example.user.repository.PrivilegeRepository;
//import com.example.user.repository.RoleRepository;
//import com.example.user.repository.UserRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer {
//
//    private final PrivilegeRepository privilegeRepository;
//    private final RoleRepository roleRepository;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @PostConstruct
//    public void init() {
//        // Step 1: Insert privileges if not exist
//        List<Privilege> allPrivileges = getAllPrivileges();
//        for (Privilege privilege : allPrivileges) {
//            privilegeRepository.findByCode(privilege.getCode())
//                    .orElseGet(() -> privilegeRepository.save(privilege));
//        }
//
//        // Step 2: Fetch all privileges
//        List<Privilege> savedPrivileges = privilegeRepository.findAll();
//
//        // ADMIN: all privileges
//        Role adminRole = createRoleIfNotFound(
//                "Administrator",
//                "Have right to access, add, update, and delete information in all services.",
//                "ADMIN",
//                new HashSet<>(savedPrivileges)
//        );
//
//        // MANAGER: IAM (user + role)
//        Set<Privilege> managerPrivileges = savedPrivileges.stream()
//                .filter(p -> Set.of(
//                        "VIEW_USER", "CREATE_USER", "MODIFY_USER", "DELETE_USER", "LOCK_UNLOCK_USER",
//                        "VIEW_ROLE", "CREATE_ROLE", "UPDATE_ROLE", "DELETE_ROLE"
//                ).contains(p.getCode()))
//                .collect(Collectors.toSet());
//
//        Role managerRole = createRoleIfNotFound(
//                "Manager",
//                "Have right to manage users/roles in IAM",
//                "MANAGER",
//                managerPrivileges
//        );
//
//        // SERVICE: QA + limited Monitoring
//        Set<Privilege> servicePrivileges = savedPrivileges.stream()
//                .filter(p -> Set.of(
//                        "VIEW_CONFIGURATION", "CREATE_CONFIGURATION", "MODIFY_CONFIGURATION", "DELETE_CONFIGURATION",
//                        "READ_ONLY"
//                ).contains(p.getCode()))
//                .collect(Collectors.toSet());
//
//        Role roleService = createRoleIfNotFound(
//                "Role Service",
//                "Have right to access, add, update, and delete information in Quality Assurance Service and limited in Monitoring Service",
//                "ROLE_SERVICE",
//                servicePrivileges
//        );
//
//        // LAB USERS: full PatientService, limited Monitoring
//        Set<Privilege> labUserPrivileges = savedPrivileges.stream()
//                .filter(p -> Set.of(
//                        "READ_ONLY", "CREATE_TEST_ORDER", "MODIFY_TEST_ORDER", "DELETE_TEST_ORDER", "REVIEW_TEST_ORDER",
//                        "ADD_COMMENT", "MODIFY_COMMENT", "DELETE_COMMENT"
//                ).contains(p.getCode()))
//                .collect(Collectors.toSet());
//
//        Role labUserRole = createRoleIfNotFound(
//                "Lab User",
//                "Have right to manage patient data, limited access to Monitoring.",
//                "LAB_USER",
//                labUserPrivileges
//        );
//
//        // Step 3: Create default users for testing
//        createUserIfNotFound("admin@example.com", "admin", "admin123", "00001", "00001", adminRole);
//        createUserIfNotFound("manager@example.com", "manager", "manager123", "00002", "00002", managerRole);
//        createUserIfNotFound("service@example.com", "service", "service123", "00003", "00003", roleService);
//        createUserIfNotFound("labuser@example.com", "labuser", "lab123", "00004", "00004", labUserRole);
//    }
//
//    private Role createRoleIfNotFound(String name, String description, String code, Set<Privilege> privileges) {
//        return roleRepository.findByCode(code)
//                .orElseGet(() -> {
//                    Role role = new Role();
//                    role.setName(name);
//                    role.setDescription(description);
//                    role.setCode(code);
//                    role.setPrivileges(privileges);
//                    return roleRepository.save(role);
//                });
//    }
//
//    private void createUserIfNotFound(String email, String fullName, String rawPassword, String phone, String idNum, Role role) {
//        if (userRepository.findByEmail(email) == null) {
//            User user = new User();
//            user.setEmail(email);
//            user.setFullName(fullName);
//            user.setPassword(passwordEncoder.encode(rawPassword));
//            user.setPhone(phone);
//            user.setGender("Male");
//            user.setDate_of_Birth("1990-01-01");
//            user.setAge(Integer.valueOf(32));
//            user.setAddress("Default Address");
//            user.setStatus("ACTIVE");
//            user.setIdentifyNum(idNum);
//            user.setRoles(Set.of(role));
//            userRepository.save(user);
//        }
//    }
//
//    private List<Privilege> getAllPrivileges() {
//        List<Privilege> privileges = new ArrayList<>();
//
//        Privilege p;
//
//        p = new Privilege();
//        p.setCode("READ_ONLY");
//        p.setDescription("Only have right to view patient test orders and patient test order results.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("CREATE_TEST_ORDER");
//        p.setDescription("Have right to create a new patient test order");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("MODIFY_TEST_ORDER");
//        p.setDescription("Have right to modify information a patient test order.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("DELETE_TEST_ORDER");
//        p.setDescription("Have right to delete an exist test order.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("REVIEW_TEST_ORDER");
//        p.setDescription("Have right to review, modify test result of test order");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("ADD_COMMENT");
//        p.setDescription("Have right to add a new comment for test result");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("MODIFY_COMMENT");
//        p.setDescription("Have right to modify a comment.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("DELETE_COMMENT");
//        p.setDescription("Have right to delete a comment.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("VIEW_CONFIGURATION");
//        p.setDescription("Have right to view, add, modify and delete configurations.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("CREATE_CONFIGURATION");
//        p.setDescription("Have right to add a new configuration.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("MODIFY_CONFIGURATION");
//        p.setDescription("Have right to modify a configuration.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("DELETE_CONFIGURATION");
//        p.setDescription("Have right to delete a configuration.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("VIEW_USER");
//        p.setDescription("Have right to view all user profiles");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("CREATE_USER");
//        p.setDescription("Have right to create a new user.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("MODIFY_USER");
//        p.setDescription("Have right to modify an user.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("DELETE_USER");
//        p.setDescription("Have right to delete an user.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("LOCK_UNLOCK_USER");
//        p.setDescription("Have right to lock or unlock an user.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("VIEW_ROLE");
//        p.setDescription("Have right to view all role privileges.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("CREATE_ROLE");
//        p.setDescription("Have right to create a new custom role.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("UPDATE_ROLE");
//        p.setDescription("Have right to modify privileges of custom role.");
//        privileges.add(p);
//
//        p = new Privilege();
//        p.setCode("DELETE_ROLE");
//        p.setDescription("Have right to delete a custom role.");
//        privileges.add(p);
//
//        return privileges;
//    }
//
//}
