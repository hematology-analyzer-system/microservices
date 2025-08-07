//package com.example.user.init;
//
//import com.example.user.dto.role.UpdateRoleRequest;
//import com.example.user.model.Privilege;
//import com.example.user.model.UserAuditInfo;
//import com.example.user.repository.PrivilegeRepository;
//import com.example.user.repository.RoleRepository;
//import com.example.user.service.RoleService;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.stereotype.Component;
//import org.springframework.context.event.EventListener;
//
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer {
//
//    private final PrivilegeRepository privilegeRepository;
//    private final RoleRepository roleRepository;
//    private final RoleService roleService;
//
////    @PostConstruct
//    @EventListener(ApplicationReadyEvent.class)
//    public void init() {
//        // Avoid duplicates on restart
//        System.out.println(">>> DataInitializer running...");
//        log.info("Initializing data...");
//        if (!privilegeRepository.findAll().isEmpty() || !roleRepository.findAll().isEmpty()) return;
//
//        Map<String, String> privileges = Map.ofEntries(
//                Map.entry("READ_ONLY", "Only have right to view patient test orders and patient test order results."),
//                Map.entry("CREATE_TEST_ORDER", "Have right to create a new patient test order"),
//                Map.entry("MODIFY_TEST_ORDER", "Have right to modify information a patient test order."),
//                Map.entry("DELETE_TEST_ORDER", "Have right to delete an exist test order."),
//                Map.entry("REVIEW_TEST_ORDER", "Have right to review, modify test result of test order"),
//                Map.entry("ADD_COMMENT", "Have right to add a new comment for test result"),
//                Map.entry("MODIFY_COMMENT", "Have right to modify a comment."),
//                Map.entry("DELETE_COMMENT", "Have right to delete a comment."),
//                Map.entry("VIEW_CONFIG", "Have right to view, add, modify and delete configurations."),
//                Map.entry("CREATE_CONFIG", "Have right to add a new configuration."),
//                Map.entry("MODIFY_CONFIG", "Have right to modify a configuration."),
//                Map.entry("DELETE_CONFIG", "Have right to delete a configuration."),
//                Map.entry("VIEW_USER", "Have right to view all user profiles"),
//                Map.entry("CREATE_USER", "Have right to create a new user."),
//                Map.entry("MODIFY_USER", "Have right to modify an user."),
//                Map.entry("DELETE_USER", "Have right to delete an user."),
//                Map.entry("LOCK_UNLOCK_USER", "Have right to lock or unlock an user."),
//                Map.entry("VIEW_ROLE", "Have right to view all role privileges."),
//                Map.entry("CREATE_ROLE", "Have right to create a new custom role."),
//                Map.entry("UPDATE_ROLE", "Have right to modify privileges of custom role."),
//                Map.entry("DELETE_ROLE", "Have right to delete a custom role.")
//        );
//
//        Map<String, Long> privilegeIdMap = new HashMap<>();
//        for (Map.Entry<String, String> entry : privileges.entrySet()) {
//            Privilege p = new Privilege();
//            p.setCode(entry.getKey());
//            p.setDescription(entry.getValue());
//            Privilege saved = privilegeRepository.save(p);
//            privilegeIdMap.put(entry.getKey(), saved.getPrivilegeId());
//        }
//
//        // Define privileges per role
//        List<String> adminPrivileges = new ArrayList<>(privilegeIdMap.keySet());
//
//        List<String> managerPrivileges = List.of(
//                "VIEW_USER", "CREATE_USER", "MODIFY_USER", "DELETE_USER", "LOCK_UNLOCK_USER",
//                "VIEW_ROLE", "CREATE_ROLE", "UPDATE_ROLE", "DELETE_ROLE"
//        );
//
//        List<String> servicePrivileges = List.of(
//                "READ_ONLY", "CREATE_TEST_ORDER", "MODIFY_TEST_ORDER", "DELETE_TEST_ORDER", "REVIEW_TEST_ORDER",
//                "ADD_COMMENT", "MODIFY_COMMENT", "DELETE_COMMENT",
//                "VIEW_CONFIG", "CREATE_CONFIG", "MODIFY_CONFIG", "DELETE_CONFIG"
//        );
//
//        List<String> labUserPrivileges = List.of(
//                "READ_ONLY", "CREATE_TEST_ORDER", "MODIFY_TEST_ORDER", "DELETE_TEST_ORDER", "REVIEW_TEST_ORDER",
//                "ADD_COMMENT", "MODIFY_COMMENT", "DELETE_COMMENT"
//        );
//
//        insertRole("ADMIN", "Administrator", "Have right to access, add, update, and delete information in all services.", adminPrivileges, privilegeIdMap);
//        insertRole("MANAGER", "Manager", "Have right to access, add, modify and delete users. Have right to access, add, modify, delete roles privileges in Identify Access Management Service", managerPrivileges, privilegeIdMap);
//        insertRole("SERVICE", "Service", "Have right to access, add, update, and delete information in Quality Assurance Service and limited in Monitoring Service", servicePrivileges, privilegeIdMap);
//        insertRole("LAB_USER", "Lab users", "Have right to access, add, update, and delete information in Patient Service and limited in Monitoring Service.", labUserPrivileges, privilegeIdMap);
//    }
//
//    private void insertRole(String code, String name, String description, List<String> privilegeCodes, Map<String, Long> privilegeIdMap) {
//        List<Long> privilegeIds = privilegeCodes.stream()
//                .map(privilegeIdMap::get)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        UpdateRoleRequest dto = new UpdateRoleRequest();
//        dto.setCode(code);
//        dto.setName(name);
//        dto.setDescription(description);
//        dto.setPrivilegesIds(privilegeIds);
//
//        Optional<UpdateRoleRequest> createdRole = roleService.createRole(dto);
//
//        // Set audit info manually after creation
//        createdRole.ifPresent(roleDto -> {
//            roleRepository.findById(roleDto.getRoleId()).ifPresent(role -> {
//                UserAuditInfo audit = new UserAuditInfo();
//                audit.setFullName("Nguyen Huu Thanh");
//                audit.setEmail("smtp.verify.token@gmail.com");
//                audit.setUserId(0L);
//                audit.setIdentifyNum("ID0");
//
//                role.setCreatedBy(audit);
//                role.setUpdatedBy(audit);
//                role.setCreated_at(LocalDateTime.now());
//                role.setUpdated_at(LocalDateTime.now());
//
//                roleRepository.save(role);
//            });
//        });
//    }
//
//}
//


package com.example.user.init;

import com.example.user.dto.role.UpdateRoleRequest;
import com.example.user.model.Privilege;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.UserAuditInfo;
import com.example.user.repository.PrivilegeRepository;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import com.example.user.service.RoleService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PrivilegeRepository privilegeRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "Secure@123";
    private static final String MALE_PIC = "/upload/images/defaultMale.png";
    private static final String FEMALE_PIC = "/upload/images/defaultFemale.png";

    private final UserAuditInfo auditInfo = new UserAuditInfo(
            0L,
            "Nguyen Huu Thanh",
            "smtp.verify.token@gmail.com",
            "ID0"
    );

    @EventListener(ApplicationReadyEvent.class)
//    @PostConstruct
    @Transactional
    public void initializeSystemData() {
        log.info(">>> Running SystemDataInitializer...");
        if (privilegeRepository.count() > 0 || roleRepository.count() > 0 || userRepository.count() > 0) {
            log.info("System data already initialized. Skipping...");
            return;
        }

        // 1. Insert Privileges and Roles
        Map<String, Long> privilegeIdMap = insertPrivilegesAndRoles();

        // 2. Insert Default Users
        log.info("User data initializing ...");
        insertDefaultUsers();
        log.info("User data initialized ...");
    }

    private Map<String, Long> insertPrivilegesAndRoles() {
        Map<String, String> privileges = Map.ofEntries(
                Map.entry("READ_ONLY", "Only have right to view patient test orders and patient test order results."),
                Map.entry("CREATE_TEST_ORDER", "Have right to create a new patient test order"),
                Map.entry("MODIFY_TEST_ORDER", "Have right to modify information a patient test order."),
                Map.entry("DELETE_TEST_ORDER", "Have right to delete an exist test order."),
                Map.entry("REVIEW_TEST_ORDER", "Have right to review, modify test result of test order"),
                Map.entry("ADD_COMMENT", "Have right to add a new comment for test result"),
                Map.entry("MODIFY_COMMENT", "Have right to modify a comment."),
                Map.entry("DELETE_COMMENT", "Have right to delete a comment."),
                Map.entry("VIEW_CONFIG", "Have right to view, add, modify and delete configurations."),
                Map.entry("CREATE_CONFIG", "Have right to add a new configuration."),
                Map.entry("MODIFY_CONFIG", "Have right to modify a configuration."),
                Map.entry("DELETE_CONFIG", "Have right to delete a configuration."),
                Map.entry("VIEW_USER", "Have right to view all user profiles"),
                Map.entry("CREATE_USER", "Have right to create a new user."),
                Map.entry("MODIFY_USER", "Have right to modify an user."),
                Map.entry("DELETE_USER", "Have right to delete an user."),
                Map.entry("LOCK_UNLOCK_USER", "Have right to lock or unlock an user."),
                Map.entry("VIEW_ROLE", "Have right to view all role privileges."),
                Map.entry("CREATE_ROLE", "Have right to create a new custom role."),
                Map.entry("UPDATE_ROLE", "Have right to modify privileges of custom role."),
                Map.entry("DELETE_ROLE", "Have right to delete a custom role.")
        );

        Map<String, Long> privilegeIdMap = new HashMap<>();
        for (Map.Entry<String, String> entry : privileges.entrySet()) {
            Privilege p = new Privilege();
            p.setCode(entry.getKey());
            p.setDescription(entry.getValue());
            Privilege saved = privilegeRepository.save(p);
            privilegeIdMap.put(entry.getKey(), saved.getPrivilegeId());
        }

        insertRole("ADMIN", "Administrator", "Full system access", new ArrayList<>(privilegeIdMap.keySet()), privilegeIdMap);
        insertRole("MANAGER", "Manager", "User and role manager", List.of(
                "VIEW_USER", "CREATE_USER", "MODIFY_USER", "DELETE_USER", "LOCK_UNLOCK_USER",
                "VIEW_ROLE", "CREATE_ROLE", "UPDATE_ROLE", "DELETE_ROLE"
        ), privilegeIdMap);

        insertRole("SERVICE", "Service", "Test and config management", List.of(
                "READ_ONLY", "CREATE_TEST_ORDER", "MODIFY_TEST_ORDER", "DELETE_TEST_ORDER", "REVIEW_TEST_ORDER",
                "ADD_COMMENT", "MODIFY_COMMENT", "DELETE_COMMENT",
                "VIEW_CONFIG", "CREATE_CONFIG", "MODIFY_CONFIG", "DELETE_CONFIG"
        ), privilegeIdMap);

        insertRole("LAB_USER", "Lab User", "Patient service limited access", List.of(
                "READ_ONLY", "CREATE_TEST_ORDER", "MODIFY_TEST_ORDER", "DELETE_TEST_ORDER", "REVIEW_TEST_ORDER",
                "ADD_COMMENT", "MODIFY_COMMENT", "DELETE_COMMENT"
        ), privilegeIdMap);

        return privilegeIdMap;
    }

    private void insertRole(String code, String name, String description, List<String> privilegeCodes, Map<String, Long> privilegeIdMap) {
        List<Long> privilegeIds = privilegeCodes.stream()
                .map(privilegeIdMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        UpdateRoleRequest dto = new UpdateRoleRequest();
        dto.setCode(code);
        dto.setName(name);
        dto.setDescription(description);
        dto.setPrivilegesIds(privilegeIds);

        roleService.createRole(dto).ifPresent(roleDto -> {
            roleRepository.findById(roleDto.getRoleId()).ifPresent(role -> {
                role.setCreatedBy(auditInfo);
                role.setUpdatedBy(auditInfo);
                role.setCreated_at(LocalDateTime.now());
                role.setUpdated_at(LocalDateTime.now());
                roleRepository.save(role);
            });
        });
    }

    private void insertDefaultUsers() {
        insertUserIfNotExists("admin@sys.com", "Administrator", "ADMIN", "male", "0001");
        insertUserIfNotExists("manager@sys.com", "Manager", "MANAGER", "male", "0002");
        insertUserIfNotExists("service@sys.com", "Service", "SERVICE", "female", "0003");
        insertUserIfNotExists("labuser@sys.com", "Lab User", "LAB_USER", "female", "0004");
    }

    private void insertUserIfNotExists(String email, String fullName, String roleCode, String gender, String identifyNum) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.info("User {} already exists. Skipping.", email);
            return;
        }

        Optional<Role> roleOpt = roleRepository.findByCode(roleCode);
        if (roleOpt.isEmpty()) {
            log.warn("Role {} not found. Skipping user {}", roleCode, email);
            return;
        }

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(identifyNum);
        user.setAddress("System Address");
        user.setGender(gender);
        user.setDate_of_Birth(LocalDate.of(1990, 1, 1).toString());
        user.setIdentifyNum(identifyNum);
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setProfilePic(gender.equalsIgnoreCase("female") ? FEMALE_PIC : MALE_PIC);
        user.setStatus(User.status.ACTIVE.toString());
        user.setRoles(Set.of(roleOpt.get()));
        user.setCreatedBy(auditInfo);
        user.setUpdatedBy(auditInfo);
        user.setCreatedAt(LocalDate.now().atStartOfDay());
        user.setUpdate_at(LocalDate.now().atStartOfDay());

        userRepository.save(user);
        log.info("Inserted default user: {} with role: {}", email, roleCode);
    }
}
