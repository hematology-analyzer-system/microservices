//package com.example.user.init;
//
//import com.example.user.model.Role;
//import com.example.user.model.User;
//import com.example.user.model.UserAuditInfo;
//import com.example.user.repository.RoleRepository;
//import com.example.user.repository.UserRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.context.event.EventListener;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.*;
//
//@Component
//@RequiredArgsConstructor
////@DependsOn("dataInitializer")
//@Slf4j
//public class DefaultUserInitializer {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    private static final String DEFAULT_PASSWORD = "Secure@123";
//    private static final String MALE_PIC = "/upload/images/defaultMale.png";
//    private static final String FEMALE_PIC = "/upload/images/defaultFemale.png";
//
//    private final UserAuditInfo auditInfo = new UserAuditInfo(
//            0L,
//            "Nguyen Huu Thanh",
//            "smtp.verify.token@gmail.com",
//            "ID0"
//    );
//
////    @PostConstruct
//    @EventListener(ApplicationReadyEvent.class)
//    public void insertDefaultUsers() {
//        insertUserIfNotExists("admin@sys.com", "Administrator", "ADMIN", "male", "0001");
//        insertUserIfNotExists("manager@sys.com", "Manager", "MANAGER", "male", "0002");
//        insertUserIfNotExists("service@sys.com", "Service", "SERVICE", "female", "0003");
//        insertUserIfNotExists("labuser@sys.com", "Lab User", "LAB_USER", "female", "0004");
//    }
//
//    private void insertUserIfNotExists(String email, String fullName, String roleCode, String gender, String identifyNum) {
//        if (userRepository.existsByEmail(email)) {
//            log.info("User {} already exists. Skipping.", email);
//            return;
//        }
//
//        Optional<Role> roleOpt = roleRepository.findByCode(roleCode);
//        if (roleOpt.isEmpty()) {
//            log.warn("Role {} not found. Skipping user {}", roleCode, email);
//            return;
//        }
//
//        User user = new User();
//        user.setFullName(fullName);
//        user.setEmail(email);
//        user.setPhone("0123456789");
//        user.setAddress("System Address");
//        user.setGender(gender);
//        user.setDate_of_Birth(LocalDate.of(1990, 1, 1).toString());
//        user.setIdentifyNum(identifyNum);
//        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
//        user.setProfilePic(gender.equalsIgnoreCase("female") ? FEMALE_PIC : MALE_PIC);
//        user.setStatus(User.status.ACTIVE.toString());
//        user.setRoles(Set.of(roleOpt.get()));
//        user.setCreatedBy(auditInfo);
//        user.setUpdatedBy(auditInfo);
//        user.setCreatedAt(LocalDate.now().atStartOfDay());
//        user.setUpdate_at(LocalDate.now().atStartOfDay());
//
//        userRepository.save(user);
//        log.info("Inserted default user: {} with role: {}", email, roleCode);
//    }
//}
