package com.example.user.service;
import com.example.user.config.SchedulerConfig;
<<<<<<< HEAD
<<<<<<< HEAD
import com.example.user.controller.AuthController;
=======
>>>>>>> 1acd5c3 (fix(iam): fix feature assign role and create role)
=======
import com.example.user.controller.AuthController;
>>>>>>> 1a38ff0 (fix(iam): fix RGAB)
import com.example.user.dto.search.searchDTO;
import com.example.user.dto.userdto.*;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.*;
import com.example.user.repository.ModifiedHistoryRepository;
import com.example.user.repository.UserRepository;
import com.example.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

<<<<<<< HEAD
<<<<<<< HEAD
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
=======
>>>>>>> 1acd5c3 (fix(iam): fix feature assign role and create role)
=======
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
>>>>>>> 1a38ff0 (fix(iam): fix RGAB)
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditorAware<UserAuditInfo> auditorAware;
    private final ModifiedHistoryRepository historyRepository;
    private static final Logger log = (Logger) LoggerFactory.getLogger(UserService.class);
<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> 1a38ff0 (fix(iam): fix RGAB)
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    @Autowired
    private EmailService emailService;

    String defaultMalePic = "/images/defaultMale.png";
    String defaultFemalePic = "/images/defaultFemale.png";
<<<<<<< HEAD
=======
>>>>>>> 1acd5c3 (fix(iam): fix feature assign role and create role)
=======
>>>>>>> 1a38ff0 (fix(iam): fix RGAB)

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailWithoutAuditing(email);
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
        String frontendLink = "http://localhost:3000/activation?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8) + "&flow=activation";

        String emailBody = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "  <style>" +
                "    .button {" +
                "      background-color: #4CAF50;" +
                "      color: white;" +
                "      padding: 12px 20px;" +
                "      text-align: center;" +
                "      text-decoration: none;" +
                "      display: inline-block;" +
                "      font-size: 16px;" +
                "      border-radius: 5px;" +
                "    }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <p>Hello,</p>" +
                "  <p>Thank you for registering. Please click the button below to activate your account:</p>" +
                "  <a href=\"" + frontendLink + "\" class=\"button\">Activate Account</a>" +
                "  <p>If the button doesn't work, you can copy and paste this link into your browser:</p>" +
                "  <p>" + frontendLink + "</p>" +
                "</body>" +
                "</html>";

        emailService.sendEmail(email, subject, emailBody);
    }

<<<<<<< HEAD
<<<<<<< HEAD

=======
>>>>>>> 1acd5c3 (fix(iam): fix feature assign role and create role)
=======

>>>>>>> 1a38ff0 (fix(iam): fix RGAB)
    public Optional<FetchUserResponse> getUserById(Long id) {
        User newUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return  Optional.of(new FetchUserResponse(newUser, newUser.getUpdate_at(), newUser.getUpdatedBy().getEmail()));
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
<<<<<<< HEAD
<<<<<<< HEAD
=======
//        userId = 67L;
>>>>>>> 1acd5c3 (fix(iam): fix feature assign role and create role)
=======
>>>>>>> 1a38ff0 (fix(iam): fix RGAB)
        Long finalUserId = userId;
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", finalUserId));

<<<<<<< HEAD
<<<<<<< HEAD
        if (user.getStatus().equals("PENDING_VERIFICATION") || user.getStatus().equals("PENDING_ACTIVATION")) {
             throw new IllegalArgumentException("Invalid user status");
         }

=======
        if (user.getStatus().equals("PENDING_VERIFICATION") || user.getStatus().equals("INACTIVE") || user.getStatus().equals("PENDING_ACTIVATION")) {
             throw new IllegalArgumentException("Invalid user status");
         }
>>>>>>> 1acd5c3 (fix(iam): fix feature assign role and create role)
=======
        if (user.getStatus().equals("PENDING_VERIFICATION") || user.getStatus().equals("PENDING_ACTIVATION")) {
             throw new IllegalArgumentException("Invalid user status");
         }

>>>>>>> 1a38ff0 (fix(iam): fix RGAB)
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
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new IllegalArgumentException("Old password is incorrect.");
        }
        // So sánh password cũ và mới
        if (user.getPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("New password must be different from the old password.");
        }

        // Cập nhật mật khẩu
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
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
