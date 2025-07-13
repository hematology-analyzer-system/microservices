package com.example.user.service;
import com.example.user.dto.search.searchDTO;
import com.example.user.dto.userdto.*;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Privilege;
import com.example.user.model.User;
import com.example.user.model.Role;
import com.example.user.model.UserAuditInfo;
import com.example.user.repository.UserRepository;
import com.example.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditorAware<UserAuditInfo> auditorAware;


    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setIdentifyNum(request.getIdentify_num());
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setPassword(request.getPassword());
        user.setAge(request.getAge());
        user.setDate_of_Birth(request.getDate_of_Birth());
        user.setCreate_at(LocalDateTime.now());
        user.setUpdate_at(LocalDateTime.now());
        UserAuditInfo audit = auditorAware.getCurrentAuditor().orElse(null);
        user.setCreatedBy(audit);
        user.setUpdatedBy(audit);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        user.getRoles().add(role);
        userRepository.save(user);
    }
    public List<User> searchUsersByName(String namePart) {
        return userRepository.findByFullNameContainingIgnoreCase(namePart);
    }
//    public List<UserResponse> searchUserByName(String keyword) {
//        return userRepository.searchByName(keyword);
//    }
    public User updateUser(Long id, UpdateUserRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setFullName(dto.getFullName());
        user.setDate_of_Birth(dto.getDate_of_Birth());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setGender(dto.getGender());
        user.setAge(dto.getAge());
        UserAuditInfo currentUser = auditorAware.getCurrentAuditor().orElse(null);
        user.setUpdatedBy(currentUser);
        return userRepository.save(user);
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
                        user.getAge(),
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
                    cb.like(cb.lower(root.get("phone")), "%" + searchText.toLowerCase() + "%")
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
            dto.setCreatedAt(user.getCreate_at() != null ? user.getCreate_at().toString() : null);
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
