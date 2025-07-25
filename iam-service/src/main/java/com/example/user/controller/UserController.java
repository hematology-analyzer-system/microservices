package com.example.user.controller;

import com.example.user.dto.userdto.*;
import com.example.user.model.User;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.example.user.dto.search.searchDTO;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    public UserController(UserService userService, org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate) {
        this.userService = userService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUserRequest request) {
        rabbitTemplate.convertAndSend("appExchange", "user.create", "User created: " + request.getFullName());
        return userService.createUser(request);
    }


//    @GetMapping
//    public ResponseEntity<List<User>> list() {
//        return ResponseEntity.ok(userService.getAllUsers());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<FetchUserResponse> get(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        rabbitTemplate.convertAndSend("appExchange", "user.delete", "User deleted: id=" + id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProfilePic(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "/upload/images/";
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + filename);
        Files.copy(file.getInputStream(), path);

        String publicUrl = "http://localhost:8080/upload/images/" + filename;
        rabbitTemplate.convertAndSend("appExchange", "user.uploadProfilePic", "Profile picture uploaded: " + publicUrl);
        return ResponseEntity.ok(Collections.singletonMap("url", publicUrl));
    }

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
    public ResponseEntity<FetchUserResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest dto) {
        return userService.FetchUserDetails(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok("Password changed successfully.");
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<searchDTO>> filterUsers(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) Map<String, Object> filter,
            @RequestParam(defaultValue = "fullName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(defaultValue = "1") int offsetPage,
            @RequestParam(defaultValue = "12") int limitOnePage
    ) {
        return ResponseEntity.ok(
                userService.getFilteredUsers(searchText, filter, sortBy, direction, offsetPage, limitOnePage)
        );
    }
}
