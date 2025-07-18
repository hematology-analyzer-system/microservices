package com.example.user.service;

import com.example.user.dto.userdto.ChangePasswordRequest;
import com.example.user.dto.userdto.CreateUserRequest;
import com.example.user.dto.userdto.PageUserResponse;
import com.example.user.dto.userdto.UpdateUserRequest;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Role;
import com.example.user.model.User;
import com.example.user.model.UserAuditInfo;
import com.example.user.repository.RoleRepository;
import com.example.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuditorAware<UserAuditInfo> auditorAware;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(auditorAware.getCurrentAuditor())
                .thenReturn(Optional.of(new UserAuditInfo(
                        99L, "Test User", "test@example.com", "999999999"
                )));
    }

    @Test
    public void UserServiceTest_CreateUser_ReturnNewUser(){
        //Arrange
        CreateUserRequest request = new CreateUserRequest(
                "Nguyễn Văn A",
                "nguyenvana@gmail.com",
                "00000000",
                "00000000",
                "12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh",
                "Nam",
                "123123123",
                "1/1/1992",
                32
        );
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
//        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        when(userRepository.save(Mockito.any(User.class))).thenReturn(User1);
        //Act

        User savedUser = userService.createUser(request);

        //Assert
        Assertions.assertThat(savedUser).isNotNull();
    }

    @Test
    public void UserServiceTest_getAllUsers_ReturnAllUsers(){
        @SuppressWarnings("unchecked")
        List<User> users = (List<User>) Mockito.mock(List.class);
        when(userRepository.findAll()).thenReturn(users);
        List<User> allUsers = userService.getAllUsers();
        Assertions.assertThat(allUsers).isNotNull();

    }

    @Test
    public void UserServiceTest_getUserById_ReturnUser(){
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
//        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        Optional<User> user = Optional.of(User1);

        when(userRepository.findById(Mockito.any(Long.class))).thenReturn(user);

        Optional<User> result = userService.getUserById(1L);

        Assertions.assertThat(result).isNotNull();
    }

    @Test
    public void UserServiceTest_deleteUserById_Success(){
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
//        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        Optional<User> user = Optional.of(User1);

        when(userRepository.findById(Mockito.any(Long.class))).thenReturn(user);

        userService.getUserById(1L);

        assertAll(() -> userService.deleteUser(1L));
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(Mockito.any(Long.class));
    }

    @Test
    public void UserServiceTest_deleteUserById_Fail(){
        // Arrange
        doThrow(ResourceNotFoundException.class)
                .when(userRepository).deleteById(1L);

        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void UserServiceTest_assignRoleToUser_Success(){
        // Arrange
        Long userId = 1L;
        Long roleId = 2L;

        User user = new User();
        user.setId(userId);
        user.setRoles(new HashSet<>());

        Role role = new Role();
        role.setRoleId(roleId);
        role.setName("ADMIN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        // Act
        userService.assignRoleToUser(userId, roleId);

        // Assert
        Assertions.assertThat(user.getRoles()).contains(role);
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    public void UserServiceTest_assignRoleToUser_Fail_EmptyUser() {
        Long userId = 1L;
        Long roleId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            userService.assignRoleToUser(userId, roleId)
        );

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void UserServiceTest_assignRoleToUser_Fail_EmptyRole() {
        Long userId = 1L;
        Long roleId = 2L;

        User user = new User();
        user.setId(userId);
        user.setRoles(new HashSet<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            userService.assignRoleToUser(userId, roleId)
        );

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());

    }

    @Test
    public void UserServiceTest_updateUser_Success() {
        UpdateUserRequest dto = new UpdateUserRequest(
                "Nguyễn Văn C",
                "1/2/1992",
                "nguyenvanc@gmail.com",
                "122 Lý Thường Kiệt",
                "Nữ",
                31
        );
        Long userId = 1L;

        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
//        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        Optional<User> user = Optional.of(User1);



        when(userRepository.findById(userId)).thenReturn(user);
        when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.updateUser(userId, dto);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getFullName()).isEqualTo(dto.getFullName());
        Assertions.assertThat(result.getEmail()).isEqualTo(dto.getEmail());
//        Assertions.assertThat(result.getAge()).isEqualTo(dto.getAge());
        Assertions.assertThat(result.getGender()).isEqualTo(dto.getGender());
        Assertions.assertThat(result.getAddress()).isEqualTo(dto.getAddress());


    }


    @Test
    public void UserServiceTest_updateUser_Fail_EmptyUser() {
        UpdateUserRequest dto = new UpdateUserRequest(
                "Nguyễn Văn C",
                "1/2/1992",
                "nguyenvanc@gmail.com",
                "122 Lý Thường Kiệt",
                "Nữ",
                31
        );
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

//        userService.updateUser(userId, dto);

        assertThrows(RuntimeException.class, () -> userService.updateUser(userId, dto));
    }

    @Test
    public void UserServiceTest_updateUser_Fail_ExistEmail() {
        UpdateUserRequest dto = new UpdateUserRequest(
                "Nguyễn Văn C",
                "1/2/1992",
                "nguyenvanc@gmail.com",
                "122 Lý Thường Kiệt",
                "Nữ",
                31
        );
        Long userId = 1L;
        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
//        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        Optional<User> user = Optional.of(User1);

        when(userRepository.findById(userId)).thenReturn(user);
        when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, dto));

    }

    @Test
    public void UserServiceTest_searchUsers_ReturnPageUser(){
        // Arrange
        String keyword = "Nguyen";
        int page = 0;
        int size = 10;
        String sortBy = "fullName";
        String direction = "asc";

        User User1 = new User();
        User1.setFullName("Nguyễn Văn A");
        User1.setEmail("nguyenvana@gmail.com");
        User1.setPhone("00000000");
        User1.setIdentifyNum("00000000");
        User1.setAddress("12A Lý Thường Kiệt, quận 10, thành phố Hồ Chí Minh");
        User1.setGender("Nam");
        User1.setPassword("123123123");
//        User1.setAge(32);
        User1.setDate_of_Birth("1/1/1992");
        User1.setCreate_at(LocalDateTime.now());
        User1.setUpdate_at(LocalDateTime.now());

        List<User> users = List.of(User1);
        Page<User> userPage = new PageImpl<>(users);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Mockito.when(userRepository.findByFullNameContainingIgnoreCase(keyword, pageable)).thenReturn(userPage);

        // Act
        PageUserResponse result = userService.searchUsers(page, size, keyword, sortBy, direction);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRoles().size()).isEqualTo(1);
        Assertions.assertThat(result.getCurrentPage()).isEqualTo(page);
        Assertions.assertThat(result.getPageSize()).isEqualTo(size);
        Assertions.assertThat(result.getSortBy()).isEqualTo(sortBy);
        Assertions.assertThat(result.getSortDirection()).isEqualTo(direction);
        Assertions.assertThat(result.isEmpty()).isFalse();

    }

    @Test
    public void UserServiceTest_searchUsers_ReturnEmptyPage() {
        // Arrange
        int page = 1, size = 5;
        String keyword = "nonexistent", sortBy = "fullName", direction = "asc";

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Mockito.when(userRepository.findByFullNameContainingIgnoreCase(keyword, pageable))
                .thenReturn(Page.empty());

        // Act
        PageUserResponse result = userService.searchUsers(page, size, keyword, sortBy, direction);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRoles()).isEmpty();
        Assertions.assertThat(result.getCurrentPage()).isEqualTo(page);
        Assertions.assertThat(result.getPageSize()).isEqualTo(size);
    }

    @Test
    public void UserServiceTest_changePassword_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        user.setPassword("oldPass123");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPass123");
        request.setNewPassword("newPass456");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.changePassword(userId, request);

        // Assert
        Mockito.verify(userRepository).save(user);
        Assertions.assertThat(user.getPassword()).isEqualTo("newPass456");
    }

    @Test
    public void UserServiceTest_changePassword_Fail_OldPasswordIncorrect() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPassword("correctOldPass");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrongOldPass");
        request.setNewPassword("newPass");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                        userService.changePassword(userId, request),
                "Old password is incorrect."
        );

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void UserServiceTest_changePassword_Fail_NewPasswordSameAsOld() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setPassword("samePassword");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("samePassword");
        request.setNewPassword("samePassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () ->
                        userService.changePassword(userId, request),
                "New password must be different from the old password."
        );

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }
}
