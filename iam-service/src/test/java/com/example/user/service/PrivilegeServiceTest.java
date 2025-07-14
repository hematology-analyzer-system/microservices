package com.example.user.service;

import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Privilege;
import com.example.user.repository.PrivilegeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrivilegeServiceTest {
    @Mock
    private PrivilegeRepository privilegeRepository;
    @InjectMocks
    private PrivilegeService privilegeService;

    @Test
    public void PrivilegeServiceTest_getAllPrivileges_ShouldReturnListOfPrivileges() {
        // Arrange
        Privilege privilege1 = new Privilege();
        privilege1.setCode("READ_ONLY");
        privilege1.setDescription("Read only access");

        Privilege privilege2 = new Privilege();
        privilege2.setCode("WRITE");
        privilege2.setDescription("Write access");

        List<Privilege> mockPrivileges = List.of(privilege1, privilege2);

        when(privilegeRepository.findAll()).thenReturn(mockPrivileges);

        // Act
        List<Privilege> result = privilegeService.getAllPrivileges();

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result).extracting(Privilege::getCode)
                .containsExactlyInAnyOrder("READ_ONLY", "WRITE");

        Mockito.verify(privilegeRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void PrivilegeServiceTest_createPrivilege_ShouldSaveAndReturnPrivilege() {
        // Arrange
        Privilege inputPrivilege = new Privilege();
        inputPrivilege.setCode("MANAGE_USERS");
        inputPrivilege.setDescription("Permission to manage users");

        when(privilegeRepository.save(Mockito.any(Privilege.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Privilege result = privilegeService.createPrivilege(inputPrivilege);

        // Assert
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getCode()).isEqualTo("MANAGE_USERS");
        Assertions.assertThat(result.getDescription()).isEqualTo("Permission to manage users");

        Mockito.verify(privilegeRepository, Mockito.times(1)).save(Mockito.any(Privilege.class));
    }

    @Test
    public void PrivilegeServiceTest_getPrivilegeById_ShouldReturnPrivilege_WhenFound() {
        // Arrange
        Long id = 1L;
        Privilege privilege = new Privilege();
        privilege.setCode("READ");
        privilege.setDescription("Read access");

        when(privilegeRepository.findById(id)).thenReturn(Optional.of(privilege));

        // Act
        Optional<Privilege> result = privilegeService.getPrivilegeById(id);

        // Assert
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getCode()).isEqualTo("READ");
        Assertions.assertThat(result.get().getDescription()).isEqualTo("Read access");

        Mockito.verify(privilegeRepository, Mockito.times(1)).findById(id);
    }

    @Test
    public void PrivilegeServiceTest_deletePrivilege_ShouldCallRepositoryDeleteById() {
        // Arrange
        Long privilegeId = 1L;
        doThrow(ResourceNotFoundException.class)
                .when(privilegeRepository).deleteById(privilegeId);
        // Act
//        privilegeService.deletePrivilege(privilegeId);

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> privilegeService.deletePrivilege(1L));
        Mockito.verify(privilegeRepository, Mockito.times(1)).deleteById(privilegeId);
    }




}
