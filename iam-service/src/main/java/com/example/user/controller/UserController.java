package com.example.user.controller;

import com.example.user.dto.userdto.*;
import com.example.user.model.User;
import com.example.user.model.UserAuditLog;
import com.example.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.example.user.dto.search.searchDTO;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;
    UserAuditLog auditLog = new UserAuditLog();

    public UserController(UserService userService, RabbitTemplate rabbitTemplate) {
        this.userService = userService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUserRequest request) {
        UserAuditLog createAuditLog = new UserAuditLog();
        createAuditLog.setFullName(request.getFullName());
        createAuditLog.setEmail(request.getEmail());
        createAuditLog.setIdentifyNum(request.getIdentifyNum());
        // createAuditLog.setDetails("User created: " + request.getFullName());
        createAuditLog.setAction("CREATE_USER");
        rabbitTemplate.convertAndSend("appExchange", "user.create", createAuditLog);
        return userService.createUser(request);
    }


//    @GetMapping
//    public ResponseEntity<List<User>> list() {
//        return ResponseEntity.ok(userService.getAllUsers());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<FetchUserResponse> get(@PathVariable Long id) {
        auditLog.setUserId(id);
        auditLog.setDetails("User get: id=" + id);
        rabbitTemplate.convertAndSend("appExchange", "user.get", auditLog);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        auditLog.setUserId(id);
        auditLog.setAction("DELETE_USER");
        auditLog.setFullName("User with ID " + id + " deleted");
        auditLog.setDetails("User deleted: id=" + id);
        rabbitTemplate.convertAndSend("appExchange", "user.delete", auditLog);
        if (userService.softDeleteUser(id) == 1) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Soft delete user successfully."));
        }
        else if (userService.softDeleteUser(id) == 0) {
            return new ResponseEntity<>(Collections.singletonMap("error", "Access denied: Insufficient privileges."), HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody UpdateStatusUser request) {
        if (request.isLock()) {
            if (userService.unlockUser(id) == 1) return ResponseEntity.ok(Collections.singletonMap("message", "User unlocked successfully."));
            else if (userService.unlockUser(id) == 0) return new ResponseEntity<>(Collections.singletonMap("error", "Access denied: Insufficient privileges."), HttpStatus.FORBIDDEN);
        } else {
            if (userService.lockUser(id) == 1) return ResponseEntity.ok(Collections.singletonMap("message", "User locked successfully."));
            else if (userService.lockUser(id) == 0) return new ResponseEntity<>(Collections.singletonMap("error", "Access denied: Insufficient privileges."), HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/lock")
    public ResponseEntity<?> lockUser(@PathVariable Long id) {
        System.out.println("=== LOCK ENDPOINT CALLED for user ID: " + id + " ===");
        int result = userService.lockUser(id);
        System.out.println("Lock user result for ID " + id + ": " + result);
        if (result == 1) {
            // UserService already sends the RabbitMQ message, no need to duplicate
            System.out.println("User locked successfully, notification sent by UserService");
            return ResponseEntity.ok(Collections.singletonMap("message", "User locked successfully."));
        } else if (result == 0) {
            System.out.println("Lock failed: Access denied (insufficient privileges) for user ID: " + id);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Access denied: Insufficient privileges."));
        } else if (result == 2) {
            System.out.println("Lock failed: User not found for ID: " + id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found."));
        }
        System.out.println("Lock failed: Unknown error for user ID: " + id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Unknown error occurred."));
    }
    @PatchMapping("/{id}/unlock")
    public ResponseEntity<?> unlockUser(@PathVariable Long id) {
        System.out.println("=== UNLOCK ENDPOINT CALLED for user ID: " + id + " ===");
        int result = userService.unlockUser(id);
        System.out.println("Unlock user result for ID " + id + ": " + result);
        if (result == 1) {
            // UserService already sends the RabbitMQ message, no need to duplicate
            System.out.println("User unlocked successfully, notification sent by UserService");
            return ResponseEntity.ok(Collections.singletonMap("message", "User unlocked successfully."));
        } else if (result == 0) {
            System.out.println("Unlock failed: Access denied (insufficient privileges) for user ID: " + id);
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Collections.singletonMap("error", "Access denied: Insufficient privileges."));
        } else if (result == 2) {
            System.out.println("Unlock failed: User not found for ID: " + id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "User not found."));
        }
        System.out.println("Unlock failed: Unknown error for user ID: " + id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Unknown error occurred."));
    }


//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadProfilePic(@RequestParam("file") MultipartFile file) throws IOException {
//        String uploadDir = "/upload/images/";
//        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
//        Path path = Paths.get(uploadDir + filename);
//        Files.copy(file.getInputStream(), path);
//
//        String publicUrl = "http://localhost:8080/upload/images/" + filename;
//        
//        auditLog.setDetails("Profile picture uploaded: " + publicUrl);
//        rabbitTemplate.convertAndSend("appExchange", "user.uploadProfilePic", auditLog);
//        return ResponseEntity.ok(Collections.singletonMap("url", publicUrl));
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
    public ResponseEntity<FetchUserResponse> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest dto) {
        auditLog.setUserId(id);
        auditLog.setFullName(dto.getFullName());
        auditLog.setEmail(dto.getEmail());
        auditLog.setIdentifyNum(dto.getIdentifyNum());

        auditLog.setDetails("User updated: " + dto.getFullName());
        rabbitTemplate.convertAndSend("appExchange", "user.update", auditLog);
        return userService.FetchUserDetails(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long userId,
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(userId, request);
        auditLog.setUserId(userId);
        auditLog.setDetails("Password changed for user ID: " + userId);
        rabbitTemplate.convertAndSend("appExchange", "user.changePassword", auditLog);
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
