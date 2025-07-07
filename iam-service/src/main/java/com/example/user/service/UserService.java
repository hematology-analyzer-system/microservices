package com.example.user.service;
import com.example.user.dto.userdto.*;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.User;
import com.example.user.model.Role;
import com.example.user.repository.UserRepository;
import com.example.user.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    public UserService(UserRepository userRepository,RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

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
}
