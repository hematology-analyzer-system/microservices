package com.example.user.controller;

import com.example.user.model.User;
import com.example.user.service.UserService;
import com.example.user.dto.userdto.UpdateUserRequest;
import com.example.user.dto.userdto.PageUserResponse;
import com.example.user.dto.userdto.CreateUserRequest;
import com.example.user.dto.userdto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<String> assignRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        userService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok("Role assigned to user successfully.");
    }
//    @GetMapping("/search")
//    public ResponseEntity<List<User>> searchByName(@RequestParam String name) {
//        return ResponseEntity.ok(userService.searchUsersByName(name));
//    }
//    @GetMapping("/search")
//    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword) {
//        return ResponseEntity.ok(userService.searchUserByName(keyword));
//    }

    @GetMapping("/search")
    public ResponseEntity<PageUserResponse> searchUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(userService.searchUsers(page, size, keyword, sortBy, direction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest dto) {
        return ResponseEntity.ok(userService.updateUser(id, dto));
    }

}
