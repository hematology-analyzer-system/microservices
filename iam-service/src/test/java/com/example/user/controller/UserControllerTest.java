package com.example.user.controller;

import com.example.user.config.AuditTestConfig;
import com.example.user.dto.userdto.*;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.User;
import com.example.user.model.UserAuditInfo;
import com.example.user.security.JwtService;
import com.example.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.ReplaceWith;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Import(AuditTestConfig.class)
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private JwtService  jwtService;
    @MockitoBean(name = "auditorProvider")
    private AuditorAware<UserAuditInfo> auditorAware;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private CreateUserRequest  createUserRequest;
    private UpdateUserRequest  updateUserRequest;
    private ChangePasswordRequest changePasswordRequest;

    @BeforeEach
    public void setup() {


        createUserRequest = new CreateUserRequest(
                "John Doe",
                "john.doe@example.com",
                "0123456789",
                "ID123456",
                "123 Main Street",
                "Male",
                "123456",
                "1990-01-01",
                34
        );

        user = new User();
        user.setId(1L);
        user.setFullName(createUserRequest.getFullName());
        user.setEmail(createUserRequest.getEmail());
        user.setPhone(createUserRequest.getPhone());
        user.setIdentifyNum(createUserRequest.getIdentify_num());
        user.setAddress(createUserRequest.getAddress());
        user.setGender(createUserRequest.getGender());
        user.setPassword(createUserRequest.getPassword());
        user.setDate_of_Birth(createUserRequest.getDate_of_Birth());
        user.setAge(createUserRequest.getAge());

        updateUserRequest = new UpdateUserRequest(
                "John Updated",
                "1990-01-01",
                "updated.email@example.com",
                "Updated Address",
                "Male",
                30
        );

        changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword("oldPassword");
        changePasswordRequest.setNewPassword("newPassword123");

        given(auditorAware.getCurrentAuditor()).willReturn(Optional.of(
                new UserAuditInfo(999L, "Test User", "test@example.com", "999999999")
        ));
    }

    @Test
    public void UserController_create_ShouldReturnCreatedUser() throws Exception {
        // Arrange
        given(userService.createUser(ArgumentMatchers.any(CreateUserRequest.class))).willReturn(user);

        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)));

        response.andExpect(status().isOk());
    }

    @Test
    public void UserController_list_ShouldReturnUserList() throws Exception {
        // Arrange
        List<User> userList = List.of(user);
        given(userService.getAllUsers()).willReturn(userList);

        // Act
        ResultActions response = mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", CoreMatchers.is(userList.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullName").value(user.getFullName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].phone").value(user.getPhone()));
    }

    @Test
    public void UserController_get_ShouldReturnUserById() throws Exception {
        // Arrange
        Long userId = user.getId();
        given(userService.getUserById(userId)).willReturn(Optional.of(user));

        // Act
        ResultActions response = mockMvc.perform(get("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value(user.getFullName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.phone").value(user.getPhone()));
    }


    @Test
    public void UserController_get_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        Long userId = 999L;
        given(userService.getUserById(userId)).willReturn(Optional.empty());

        // Act
        ResultActions response = mockMvc.perform(get("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isNotFound());
    }

    @Test
    public void UserController_delete_ShouldReturnNoContent() throws Exception {
        // Arrange
        Long userId = user.getId();
        doNothing().when(userService).deleteUser(userId);

        // Act
        ResultActions response = mockMvc.perform(delete("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isNoContent());

        // Verify interaction
        then(userService).should(times(1)).deleteUser(userId);
    }

    @Test
    public void UserController_delete_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        // Arrange
        Long userId = 999L;
        willThrow(new ResourceNotFoundException("User", "id", userId))
                .given(userService).deleteUser(userId);

        // Act
        ResultActions response = mockMvc.perform(delete("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isNotFound());

        // Verify interaction
        then(userService).should(times(1)).deleteUser(userId);
    }

    @Test
    public void UserController_assignRoleToUser_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        Long userId = user.getId();
        Long roleId = 1L;

        willDoNothing().given(userService).assignRoleToUser(userId, roleId);

        // Act
        ResultActions response = mockMvc.perform(post("/users/{userId}/roles/{roleId}", userId, roleId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Role assigned to user successfully."));

        then(userService).should(times(1)).assignRoleToUser(userId, roleId);
    }

    @Test
    public void UserController_assignRoleToUser_ShouldReturnNotFound_WhenUserOrRoleNotExist() throws Exception {
        // Arrange
        Long userId = 999L;
        Long roleId = 888L;

        willThrow(new ResourceNotFoundException("User", "id", userId))
                .given(userService).assignRoleToUser(userId, roleId);

        // Act
        ResultActions response = mockMvc.perform(post("/users/{userId}/roles/{roleId}", userId, roleId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isNotFound());

        then(userService).should(times(1)).assignRoleToUser(userId, roleId);
    }


    @Test
    public void UserController_searchUsers_ShouldReturnPagedResult() throws Exception {
        // Arrange
        int page = 0;
        int size = 5;
        String keyword = "john";
        String sortBy = "fullName";
        String direction = "asc";

        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users);

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


        PageUserResponse response = new PageUserResponse(
                userResponses,
                userPage.getTotalElements(),
                page,
                size,
                sortBy,
                direction
        );


        given(userService.searchUsers(page, size, keyword, sortBy, direction))
                .willReturn(response);

        // Act
        ResultActions result = mockMvc.perform(get("/users/search")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .param("keyword", keyword)
                .param("sortBy", sortBy)
                .param("direction", direction)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles.length()").value(users.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].fullName").value(user.getFullName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].phone").value(user.getPhone()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].identifyNum").value(user.getIdentifyNum()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].gender").value(user.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].age").value(user.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].address").value(user.getAddress()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles[0].dateOfBirth").value(user.getDate_of_Birth()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value((int) userPage.getTotalElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sortBy").value(sortBy))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sortDirection").value(direction))
                .andExpect(MockMvcResultMatchers.jsonPath("$.empty").value(false));

        then(userService).should(times(1)).searchUsers(page, size, keyword, sortBy, direction);
    }

    @Test
    public void UserController_updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Arrange
        Long userId = 1L;

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFullName(updateUserRequest.getFullName());
        updatedUser.setEmail(updateUserRequest.getEmail());
        updatedUser.setAddress(updateUserRequest.getAddress());
        updatedUser.setGender(updateUserRequest.getGender());
        updatedUser.setAge(updateUserRequest.getAge());
        updatedUser.setDate_of_Birth(updateUserRequest.getDate_of_Birth());

        given(userService.updateUser(eq(userId), any(UpdateUserRequest.class)))
                .willReturn(updatedUser);

        // Act
        ResultActions response = mockMvc.perform(put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUserRequest)));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(updatedUser.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value(updatedUser.getFullName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updatedUser.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value(updatedUser.getGender()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(updatedUser.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value(updatedUser.getAddress()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.date_of_Birth").value(updatedUser.getDate_of_Birth()));
    }

    @Test
    public void UserController_changePassword_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        Long userId = 1L;

        willDoNothing().given(userService).changePassword(eq(userId), any(ChangePasswordRequest.class));

        // Act
        ResultActions response = mockMvc.perform(put("/users/{userId}/change-password", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(MockMvcResultMatchers.content().string("Password changed successfully."));

        // Verify service was called
        then(userService).should(times(1)).changePassword(eq(userId), any(ChangePasswordRequest.class));
    }


}
