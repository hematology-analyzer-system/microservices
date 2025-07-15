package com.example.user.service;

import com.example.user.dto.role.PageRoleResponse;
import com.example.user.dto.role.RoleRequest;
import com.example.user.dto.role.UpdateRoleRequest;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Privilege;
import com.example.user.model.Role;
import com.example.user.model.UserAuditInfo;
import com.example.user.repository.PrivilegeRepository;
import com.example.user.repository.RoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private PrivilegeRepository privilegeRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuditorAware<UserAuditInfo> auditorAware;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        Mockito.lenient().when(auditorAware.getCurrentAuditor())
                .thenReturn(Optional.of(new UserAuditInfo(
                        99L, "Test User", "test@example.com", "999999999"
                )));
    }


    @Test
    public void RoleServiceTest_createRole_ShouldAssignReadOnlyPrivilege_WhenNoPrivilegeProvided() {
        // Arrange
        Role role = new Role();
        role.setName("User");
        role.setCode("USER");

        Privilege readOnlyPrivilege = new Privilege();
        readOnlyPrivilege.setCode("READ_ONLY");
        readOnlyPrivilege.setDescription("Default read-only privilege");

        when(privilegeRepository.findByCode("READ_ONLY")).thenReturn(Optional.of(readOnlyPrivilege));
        when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Role result = roleService.createRole(role);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPrivileges())
                .isNotEmpty()
                .extracting(Privilege::getCode)
                .containsExactly("READ_ONLY");

        Mockito.verify(privilegeRepository).findByCode("READ_ONLY");
        Mockito.verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    public void cRoleServiceTest_createRole_ShouldUseExistingPrivileges_WhenProvided() {
        // Arrange
        Privilege customPrivilege = new Privilege();
        customPrivilege.setCode("CUSTOM");
        customPrivilege.setDescription("Default custom privilege");

        Role role = new Role();
        role.setName("Admin");
        role.setCode("ADMIN");
        role.setPrivileges(Set.of(customPrivilege));

        when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Role result = roleService.createRole(role);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getPrivileges())
                .hasSize(1)
                .extracting(Privilege::getCode)
                .containsExactly("CUSTOM");

        Mockito.verify(privilegeRepository, Mockito.never()).findByCode("READ_ONLY");
        Mockito.verify(roleRepository).save(role);
    }

    @Test
    public void RoleServiceTest_getAllRoles_ReturnsListOfRoles() {
        // Arrange
        Role role1 = new Role();
        role1.setName("Admin");
        role1.setCode("ADMIN");

        Role role2 = new Role();
        role2.setName("User");
        role2.setCode("USER");

        List<Role> roles = List.of(role1, role2);

        Mockito.when(roleRepository.findAll()).thenReturn(roles);

        // Act
        List<Role> result = roleService.getAllRoles();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result)
                .extracting(Role::getCode)
                .containsExactly("ADMIN", "USER");

        Mockito.verify(roleRepository).findAll();
    }

    @Test
    public void RoleServiceTest_getRoleById_ReturnsRole() {
        // Arrange
        Long roleId = 1L;
        Role role = new Role();
        role.setRoleId(roleId);
        role.setName("Admin");
        role.setCode("ADMIN");

        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        // Act
        Optional<Role> result = roleService.getRoleById(roleId);

        // Assert
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getCode()).isEqualTo("ADMIN");
        Mockito.verify(roleRepository).findById(roleId);
    }

    @Test
    public void RoleServiceTest_getRoleById_ReturnsEmpty_WhenNotFound() {
        // Arrange
        Long roleId = 99L;
        Mockito.when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act
        Optional<Role> result = roleService.getRoleById(roleId);

        // Assert
        Assertions.assertThat(result).isNotPresent();
        Mockito.verify(roleRepository).findById(roleId);
    }

    @Test
    public void RoleServiceTest_deleteRole_Success() {
        // Arrange
        Long roleId = 1L;

        // Act
        roleService.deleteRole(roleId);

        // Assert
        Mockito.verify(roleRepository, Mockito.times(1)).deleteById(roleId);
    }

    @Test
    void RoleServiceTest_deleteRole_ThrowResourceNotFoundException() {
        // Arrange
        Long roleId = 1L;
        Mockito.doThrow(new ResourceNotFoundException("Role", "id", roleId))
                .when(roleRepository).deleteById(roleId);

        // Act & Assert
        Assertions.assertThatThrownBy(() -> roleService.deleteRole(roleId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role not found with id = '1'");

        // Verify
        Mockito.verify(roleRepository, Mockito.times(1)).deleteById(roleId);
    }

    @Test
    public void RoleServiceTest_assignPrivilegeToRole_ShouldAddPrivilegeSuccessfully() {
        // Arrange

        Role role = new Role();
        role.setName("ADMIN");
        role.setCode("ADMIN");

        Privilege privilege = new Privilege();
        privilege.setCode("WRITE");
        privilege.setDescription("Write access");

        when(roleRepository.findById(role.getRoleId())).thenReturn(Optional.of(role));
        when(privilegeRepository.findById(privilege.getPrivilegeId())).thenReturn(Optional.of(privilege));
        when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        roleService.assignPrivilegeToRole(role.getRoleId(), privilege.getPrivilegeId());

        // Assert
        Assertions.assertThat(role.getPrivileges())
                .isNotEmpty()
                .extracting(Privilege::getCode)
                .containsExactly("WRITE");

        Mockito.verify(roleRepository).findById(role.getRoleId());
        Mockito.verify(privilegeRepository).findById(privilege.getPrivilegeId());
        Mockito.verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    public void RoleServiceTest_assignPrivilegeToRole_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        Long roleId = 1L;
        Long privilegeId = 2L;

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> roleService.assignPrivilegeToRole(roleId, privilegeId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role not found with id = '1'");

        Mockito.verify(roleRepository).findById(roleId);
        Mockito.verify(privilegeRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void RoleServiceTest_updateRole_ShouldUpdateSuccessfully() {
        // Arrange
        Long roleId = 1L;

        Role existingRole = new Role();
        existingRole.setRoleId(roleId);
        existingRole.setName("Old Name");
        existingRole.setDescription("Old Description");

        UpdateRoleRequest updateDto = new UpdateRoleRequest();
        updateDto.setName("New Name");
        updateDto.setDescription("New Description");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Role updatedRole = roleService.updateRole(roleId, updateDto);

        // Assert
        Assertions.assertThat(updatedRole).isNotNull();
        Assertions.assertThat(updatedRole.getName()).isEqualTo("New Name");
        Assertions.assertThat(updatedRole.getDescription()).isEqualTo("New Description");

        Mockito.verify(roleRepository).findById(roleId);
        Mockito.verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    public void RoleServiceTest_updateRole_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        Long roleId = 1L;
        UpdateRoleRequest updateDto = new UpdateRoleRequest();
        updateDto.setName("New Name");
        updateDto.setDescription("New Description");

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> roleService.updateRole(roleId, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found");

        Mockito.verify(roleRepository).findById(roleId);
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
    }

    @Test
    public void RoleServiceTest_removePrivilegeFromRole_ShouldRemovePrivilegeSuccessfully() {
        // Arrange
        Long roleId = 1L;
        Long privilegeId = 100L;

        Role role = new Role();
        role.setRoleId(roleId);

        Privilege privilege = new Privilege();
        privilege.setPrivilegeId(privilegeId);
        privilege.setCode("TEST_PRIVILEGE");

        role.setPrivileges(new HashSet<>(Set.of(privilege)));

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(privilegeRepository.findById(privilegeId)).thenReturn(Optional.of(privilege));
        when(roleRepository.save(Mockito.any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        roleService.removePrivilegeFromRole(roleId, privilegeId);

        // Assert
        Assertions.assertThat(role.getPrivileges()).doesNotContain(privilege);

        Mockito.verify(roleRepository).findById(roleId);
        Mockito.verify(privilegeRepository).findById(privilegeId);
        Mockito.verify(roleRepository).save(Mockito.any(Role.class));
    }

    @Test
    public void RoleServiceTest_removePrivilegeFromRole_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        Long roleId = 1L;
        Long privilegeId = 100L;

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> roleService.removePrivilegeFromRole(roleId, privilegeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Role not found");

        Mockito.verify(roleRepository).findById(roleId);
        Mockito.verify(privilegeRepository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
    }

    @Test
    public void RoleServiceTest_removePrivilegeFromRole_ShouldThrowException_WhenPrivilegeNotFound() {
        // Arrange
        Long roleId = 1L;
        Long privilegeId = 100L;

        Role role = new Role();
        role.setRoleId(roleId);
        role.setPrivileges(new HashSet<>());

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(privilegeRepository.findById(privilegeId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> roleService.removePrivilegeFromRole(roleId, privilegeId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Privilege not found");

        Mockito.verify(roleRepository).findById(roleId);
        Mockito.verify(privilegeRepository).findById(privilegeId);
        Mockito.verify(roleRepository, Mockito.never()).save(Mockito.any(Role.class));
    }

    @Test
    public void RoleServiceTest_getAllRoles_ShouldReturnPagedRoles() {
        // Arrange
        int page = 0;
        int size = 2;
        String sortBy = "name";
        String direction = "asc";

        Role role1 = new Role();
        role1.setName("Admin");
        role1.setCode("ADMIN");
        role1.setDescription("Admin role");

        Role role2 = new Role();
        role2.setName("User");
        role2.setCode("USER");
        role2.setDescription("User role");

        Page<Role> rolePage = new PageImpl<>(List.of(role1, role2));
        when(roleRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).ascending())))
                .thenReturn(rolePage);

        // Act
        PageRoleResponse result = roleService.getAllRoles(page, size, sortBy, direction);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRoles()).hasSize(2);
        Assertions.assertThat(result.getCurrentPage()).isEqualTo(page);
        Assertions.assertThat(result.getPageSize()).isEqualTo(size);
        Assertions.assertThat(result.getSortBy()).isEqualTo(sortBy);
        Assertions.assertThat(result.getSortDirection()).isEqualTo(direction);

        Mockito.verify(roleRepository).findAll(PageRequest.of(page, size, Sort.by(sortBy).ascending()));
    }

    @Test
    public void RoleServiceTest_getAllRoles_ShouldReturnEmptyResponse_WhenNoRolesFound() {
        // Arrange
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String direction = "desc";

        Page<Role> emptyPage = new PageImpl<>(Collections.emptyList());

        when(roleRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy).descending())))
                .thenReturn(emptyPage);

        // Act
        PageRoleResponse result = roleService.getAllRoles(page, size, sortBy, direction);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRoles()).isEmpty();
        Assertions.assertThat(result.getCurrentPage()).isEqualTo(page);
        Assertions.assertThat(result.getPageSize()).isEqualTo(size);
        Assertions.assertThat(result.getSortBy()).isEqualTo(sortBy);
        Assertions.assertThat(result.getSortDirection()).isEqualTo(direction);

        Mockito.verify(roleRepository).findAll(PageRequest.of(page, size, Sort.by(sortBy).descending()));
    }

    @Test
    public void RoleServiceTest_searchRoles_ShouldReturnMatchingRolesByName() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setFilter("Admin");
        request.setSort("name");
        request.setPage_num(1);      // page = 0 (vì -1)
        request.setPage_size(2);

        Role role1 = new Role();
        role1.setName("Admin");
        role1.setCode("ADMIN");
        role1.setDescription("Admin role");

        Page<Role> rolePage = new PageImpl<>(List.of(role1));

        Pageable pageable = PageRequest.of(0, 2, Sort.by("name").ascending());

        when(roleRepository.findByNameContainingIgnoreCase("Admin", pageable))
                .thenReturn(rolePage);

        // Act
        PageRoleResponse result = roleService.searchRoles(request);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRoles()).hasSize(1);
        Assertions.assertThat(result.getRoles().getFirst().getName()).isEqualTo("Admin");
        Assertions.assertThat(result.getCurrentPage()).isEqualTo(0);
        Assertions.assertThat(result.getPageSize()).isEqualTo(2);
        Assertions.assertThat(result.getSortBy()).isEqualTo("name");
        Assertions.assertThat(result.getSortDirection()).isEqualTo("asc");

        Mockito.verify(roleRepository).findByNameContainingIgnoreCase("Admin", pageable);
    }

    @Test
    public void RoleServiceTest_searchRoles_ShouldSearchByCode_WhenSortByCode() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setFilter("ADM");
        request.setSort("code");
        request.setPage_num(1);
        request.setPage_size(5);

        Role role = new Role();
        role.setName("Admin");
        role.setCode("ADMIN");
        role.setDescription("Admin role");

        Pageable pageable = PageRequest.of(0, 5, Sort.by("code").ascending());
        Page<Role> rolePage = new PageImpl<>(List.of(role));

        when(roleRepository.findByCodeContainingIgnoreCase("ADM", pageable))
                .thenReturn(rolePage);

        // Act
        PageRoleResponse result = roleService.searchRoles(request);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRoles()).hasSize(1);
        Assertions.assertThat(result.getRoles().getFirst().getCode()).isEqualTo("ADMIN");

        Mockito.verify(roleRepository).findByCodeContainingIgnoreCase("ADM", pageable);
    }

    @Test
    public void RoleServiceTest_searchRoles_ShouldHandleEmptyResult() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setFilter("nonexistent");
        request.setSort("name");
        request.setPage_num(1);
        request.setPage_size(3);

        Pageable pageable = PageRequest.of(0, 3, Sort.by("name").ascending());
        Page<Role> emptyPage = new PageImpl<>(Collections.emptyList());

        when(roleRepository.findByNameContainingIgnoreCase("nonexistent", pageable))
                .thenReturn(emptyPage);

        // Act
        PageRoleResponse result = roleService.searchRoles(request);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getRoles()).isEmpty();
        Assertions.assertThat(result.getCurrentPage()).isEqualTo(0);
        Assertions.assertThat(result.getPageSize()).isEqualTo(3);

        Mockito.verify(roleRepository).findByNameContainingIgnoreCase("nonexistent", pageable);
    }


}
