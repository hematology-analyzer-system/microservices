package com.example.user.service;
import com.example.user.dto.search.searchDTO;
import com.example.user.dto.userdto.*;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.*;
import com.example.user.model.UserAuditLog;
import com.example.user.repository.ModifiedHistoryRepository;
import com.example.user.repository.UserRepository;
import com.example.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditorAware<UserAuditInfo> auditorAware;
    private final ModifiedHistoryRepository historyRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SimpMessagingTemplate messageTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    String defaultMalePic = "/images/defaultMale.png";
    String defaultFemalePic = "/images/defaultFemale.png";

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailWithoutAuditing(email);
    }

    public boolean changePassword(Long UserId, String oldPassword, String newPassword) {
        User user = userRepository.findById(UserId).get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }

    private boolean validatePrivileges(User currentUser, Long privilegeId) {
        if (currentUser == null) return false;
        if (!currentUser.getStatus().equals("ACTIVE")) return  false;
        List<Long> roleIds = getAllRolesById(currentUser.getId());
        Set<Long> privilegeIds = roleService.getAllPrivilegesIds(roleIds);
        return  privilegeIds.contains(privilegeId);
    }

    public ResponseEntity<?> createUser(CreateUserRequest request) {
        try {
            User currentUser = getCurrentUser();
            if (validatePrivileges(currentUser, 14L)) {
                List<String> duplicateFields = new ArrayList<>();
                if (userRepository.existsByEmail(request.getEmail())) {
                    duplicateFields.add("email");
                    log.info("Registration failed: Email {} already exists", request.getEmail());
                }
                if (request.getPhone() != null && userRepository.existsByPhone(request.getPhone())) {
                    duplicateFields.add("phone");
                    log.info("Registration failed: Phone {} already exists", request.getPhone());
                }
                if (request.getIdentifyNum() != null && userRepository.existsByIdentifyNum(request.getIdentifyNum())) {
                    duplicateFields.add("identify");
                    log.info("Registration failed: Identify Number {} already exists", request.getIdentifyNum());
                }
                if (!duplicateFields.isEmpty()) {
                    return ResponseEntity.badRequest().body(Collections.singletonMap("error", duplicateFields));
                }
                User user = new User();
                user.setFullName(request.getFullName());
                user.setEmail(request.getEmail());
                user.setPhone(request.getPhone());
                user.setGender(request.getGender());
                user.setDate_of_Birth(request.getDate_of_Birth());
                user.setAddress(request.getAddress());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setStatus(User.status.PENDING_ACTIVATION.toString()); // Set status to PENDING_ACTIVATION
                user.setIdentifyNum(request.getIdentifyNum());

                String gender = request.getGender();
                if ("female".equalsIgnoreCase(gender)) {
                    user.setProfilePic(defaultFemalePic);
                } else {
                    user.setProfilePic(defaultMalePic);
                }
                user.setCreatedBy(new UserAuditInfo(
                        null, // userId is not yet available here
                        currentUser.getFullName(),
                        currentUser.getEmail(),
                        currentUser.getIdentifyNum()
                ));
                user.setUpdatedBy(new UserAuditInfo(
                        null, // userId is not yet available here
                        currentUser.getFullName(),
                        currentUser.getEmail(),
                        currentUser.getIdentifyNum()
                ));
                userRepository.save(user);
                assignRoleToUser(user.getId(), request.getRoleIds());
                userRepository.save(user);
                log.info("User {} registered successfully with PENDING_ACTIVATION status.", user.getEmail());
                messageTemplate.convertAndSend("/topic/userCreated", user);
                sendMailActivation(user.getEmail());
                return ResponseEntity.ok(Collections.singletonMap("message", "User is created successfully!"));
            }
        } catch (Exception e) {
            log.error("User creation failed", e);
            return  ResponseEntity.badRequest().body(Collections.singletonMap("error", e));
        }
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid request"));
    }

private void sendMailActivation(String email) {
    String subject = "Activate Your Account";
    String frontendLink = "https://fhard.khoa.email/api/iam/activation?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8) + "&flow=activation";

    String emailBody =
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
                    "  <tr><td align=\"center\">" +
                    "    <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border:1px solid #dddddd; border-radius:6px; font-family:Arial, sans-serif;\">" +
                    "      <tr>" +
                    "        <td style=\"padding: 20px; text-align: center; background-color: #f8f8f8;\">" +
                    "          <h2 style=\"margin: 0; font-size: 20px; color: #333;\">Account Activation</h2>" +
                    "        </td>" +
                    "      </tr>" +
                    "      <tr>" +
                    "        <td style=\"padding: 20px; color: #555555; font-size: 16px;\">" +
                    "          Hello,<br><br>" +
                    "          Please click the button below to verify your email address and activate your account." +
                    "        </td>" +
                    "      </tr>" +
                    "      <tr>" +
                    "        <td align=\"center\" style=\"padding: 20px;\">" +
                    "          <a href=\"" + frontendLink + "\" style=\"display: inline-block; padding: 12px 24px; background-color: #007bff; color: #ffffff; text-decoration: none; border-radius: 4px; font-weight: bold;\">" +
                    "            Activate Account" +
                    "          </a>" +
                    "        </td>" +
                    "      </tr>" +
                    "      <tr>" +
                    "        <td style=\"padding: 20px; font-size: 14px; color: #999999;\">" +
                    "          If the button doesn't work, copy and paste this link into your browser:<br>" +
                    "          <span style=\"color: #007bff; word-break: break-all;\">" + frontendLink + "</span>" +
                    "        </td>" +
                    "      </tr>" +
                    "      <tr>" +
                    "        <td style=\"padding: 20px; font-size: 12px; color: #cccccc; text-align: center;\">" +
                    "          &copy; 2025 Your Company. All rights reserved." +
                    "        </td>" +
                    "      </tr>" +
                    "    </table>" +
                    "  </td></tr>" +
                    "</table>";

    emailService.sendEmail(email, subject, emailBody);
}



    public Optional<FetchUserResponse> getUserById(Long id) {
        User newUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return  Optional.of(new FetchUserResponse(newUser, newUser.getUpdate_at(), newUser.getUpdatedBy().getEmail()));
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public int softDeleteUser(Long id) {
        User currentUser = getCurrentUser();
        if (validatePrivileges(currentUser, 16L) || validatePrivileges(currentUser, 17L)) {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty()) {
                return 2;
            }
            user.get().setStatus("INACTIVE");
            userRepository.save(user.get());
            // Send RabbitMQ message for audit logging
            UserAuditLog auditLog = new UserAuditLog();
            auditLog.setUserId(user.get().getId());
            auditLog.setFullName(user.get().getFullName());
            auditLog.setAction("DELETE_USER");
            auditLog.setDetails("User soft deleted by " + currentUser.getFullName());
            rabbitTemplate.convertAndSend("appExchange", "user.softDelete", auditLog);
            return 1;
        }

        return 0;
    }

    public int lockUser(Long id) {
        User currentUser = getCurrentUser();
        if (validatePrivileges(currentUser, 17L)) {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty()) {
                log.info("User with id {} not found", id);
                return 2;
            }
            user.get().setStatus("INACTIVE");
            userRepository.save(user.get());
            
            // Send RabbitMQ message for audit logging
            UserAuditLog auditLog = new UserAuditLog();
            auditLog.setUserId(user.get().getId());
            // auditLog.setUsername(user.get().getEmail()); // Removed: No such method in UserAuditLog
            auditLog.setFullName(user.get().getFullName());
            auditLog.setEmail(user.get().getEmail());
            auditLog.setIdentifyNum(user.get().getIdentifyNum());
            auditLog.setDetails("User locked by " + currentUser.getFullName());
            
            System.out.println("UserService - Sending lock message to RabbitMQ: " + auditLog);
            rabbitTemplate.convertAndSend("appExchange", "user.lock", auditLog);
            
            // Removed direct WebSocket message to prevent duplicates
            // WebSocket notifications are now handled by UserAuditLogConsumer
            return 1; 
        }
        return 0;
    }

    public int unlockUser(Long id) {
        User currentUser = getCurrentUser();
        if (validatePrivileges(currentUser, 17L)) {
            Optional<User> user = userRepository.findById(id);
            if (user.isEmpty()) {
                return 2;
            }
            user.get().setStatus("ACTIVE");
            userRepository.save(user.get());
            
            // Send RabbitMQ message for audit logging
            UserAuditLog auditLog = new UserAuditLog();
            auditLog.setUserId(user.get().getId());
            // auditLog.setUsername(user.get().getEmail()); // Removed: No such method in UserAuditLog
            auditLog.setFullName(user.get().getFullName());
            auditLog.setEmail(user.get().getEmail());
            auditLog.setIdentifyNum(user.get().getIdentifyNum());
            auditLog.setDetails("User unlocked by " + currentUser.getFullName());
            
            System.out.println("UserService - Sending unlock message to RabbitMQ: " + auditLog);
            rabbitTemplate.convertAndSend("appExchange", "user.unlock", auditLog);
            
            // Removed direct WebSocket message to prevent duplicates
            // WebSocket notifications are now handled by UserAuditLogConsumer
            return 1;
        }
        return 0;
    }

    public boolean assignRoleToUser(Long userId, List<Long> roleIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        for (Long roleId : roleIds) {
            user.getRoles().add(roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId)));
        }
        return true;
    }

    public List<Long> getAllRolesById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return user.getRoles().stream().map(Role::getRoleId).collect(Collectors.toList());
    }

    public boolean removeRoleFromUser(Long userId, List<Long> roleIds) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return user.getRoles().removeIf(role -> roleIds.contains(role.getRoleId()));
    }

    public Optional<FetchUserResponse> FetchUserDetails(Long userId, UpdateUserRequest dto) {
        Long finalUserId = userId;
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", finalUserId));

        if (user.getStatus().equals("PENDING_VERIFICATION") || user.getStatus().equals("PENDING_ACTIVATION")) {
             throw new IllegalArgumentException("Invalid user status");
         }

         user.setFullName(dto.getFullName());
         user.setEmail(dto.getEmail());
         user.setGender(dto.getGender());
         user.setPhone(dto.getPhone());
         user.setDate_of_Birth(dto.getDate_of_Birth());
         user.setAddress(dto.getAddress());
         user.setStatus(dto.getStatus());
         user.setIdentifyNum(dto.getIdentifyNum());

         List<Long> roleIds = dto.getRoleIds();
         List<Long> roleIdsFromResquest = getAllRolesById(userId);

         log.info("Size : " + String.valueOf(roleIds.size()));

         if (removeRoleFromUser(userId, roleIdsFromResquest) || roleIdsFromResquest.isEmpty()) {
             if (assignRoleToUser(userId, roleIds)) {
                 userRepository.save(user);
                 User newUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", finalUserId));
                 return  Optional.of(new FetchUserResponse(newUser, newUser.getUpdate_at(),newUser.getUpdatedBy().getEmail()));
             }
             else if (roleIds.isEmpty()) {
                 user.setStatus("INACTIVE");
                 userRepository.save(user);
                 User newUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", finalUserId));
                 return  Optional.of(new FetchUserResponse(newUser, newUser.getUpdate_at(),newUser.getUpdatedBy().getEmail()));
             }
         }
        return Optional.empty();
    }

    public PageUserResponse searchUsers(int page, int size, String keyword, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage;

        if (keyword == null || keyword.isBlank()) {
            userPage = userRepository.findAll(pageable);
        } else {
            userPage = userRepository.findByFullNameContainingIgnoreCase(keyword, pageable);
        }

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(user -> new UserResponse(
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getIdentifyNum(),
                        user.getGender(),
//                        user.getAge(),
                        user.getAddress(),
                        user.getDate_of_Birth()

                ))
                .collect(Collectors.toList());

        if (userResponses.isEmpty()) {
            return PageUserResponse.empty(page, size, sortBy, direction);
        }

        return new PageUserResponse(
                userResponses,
                userPage.getTotalElements(),
                page,
                size,
                sortBy,
                direction
        );
    }
    public boolean changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (!user.getPassword().equals(request.getOldPassword())) {
//            throw new IllegalArgumentException("Old password is incorrect.");
            return false;
        }
        // So sánh password cũ và mới
        if (user.getPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from the old password.");
        }

        // Cập nhật mật khẩu
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return true;
    }

    public Page<searchDTO> getFilteredUsers(
            String searchText,
            Map<String, Object> filter,
            String sortBy,
            String direction,
            int offsetPage,
            int limitOnePage
    ) {
        Pageable pageable = PageRequest.of(offsetPage - 1, limitOnePage,
                Sort.by(direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

//        Specification<User> spec = Specification.where(null);
        Specification<User> spec = (root, query, cb) -> cb.conjunction(); // start with no-op predicate


        // Full-text search
        if (searchText != null && !searchText.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("fullName")), "%" + searchText.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + searchText.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("phone")), "%" + searchText.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("status")), "%" + searchText.toLowerCase() + "%")
            ));
        }

        // Filters
        if (filter != null) {
            if (filter.containsKey("gender")) {
                String gender = filter.get("gender").toString();
                spec = spec.and((root, query, cb) -> cb.equal(root.get("gender"), gender));
            }
            if (filter.containsKey("status")) {
                String status = filter.get("status").toString();
                spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
            }
            if (filter.containsKey("role")) {
                String roleName = filter.get("role").toString();
                spec = spec.and((root, query, cb) -> cb.equal(root.join("roles").get("name"), roleName));
            }
        }

        Page<User> users = userRepository.findAll(spec, pageable);

        return users.map(user -> {
            searchDTO dto = new searchDTO();
            dto.setId(user.getId());
            dto.setFullName(user.getFullName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setGender(user.getGender());
            dto.setStatus(user.getStatus());
            dto.setAddress(user.getAddress());
            dto.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
            dto.setUpdatedAt(user.getUpdate_at() != null ? user.getUpdate_at().toString() : null);

            dto.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));

            dto.setPrivileges(user.getRoles().stream()
                    .flatMap(role -> role.getPrivileges().stream())
                    .map(Privilege::getDescription)
                    .collect(Collectors.toSet()));

            return dto;
        });
    }
}
