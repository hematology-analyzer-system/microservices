package com.example.user.service;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.User;
import com.example.user.model.Role;
import com.example.user.dto.userdto.UpdateUserRequest;
import com.example.user.dto.userdto.UserResponse;
import com.example.user.dto.userdto.PageUserResponse;
import com.example.user.repository.UserRepository;
import com.example.user.repository.RoleRepository;
import com.example.user.dto.userdto.CreateUserRequest;
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
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setPassword(request.getPassword());
        user.setAge(request.getAge());
        user.setDate_of_Birth(request.getDoB());
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

        // 🔍 Kiểm tra trùng số điện thoại nếu phone mới khác
        if (!user.getPhone().equals(dto.getPhone()) && userRepository.existsByPhone(dto.getPhone())) {
            throw new IllegalArgumentException("Phone already exists");
        }
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setGender(dto.getGender());

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
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getAddress(),
                        user.getGender(),
                        user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
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
}
