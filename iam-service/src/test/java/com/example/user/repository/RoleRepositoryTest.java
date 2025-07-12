package com.example.user.repository;

import com.example.user.DataInitializer;
import com.example.user.exception.ResourceNotFoundException;
import com.example.user.model.Privilege;
import com.example.user.model.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;

    private List<Privilege> allPrivileges;


    @BeforeEach
    public void setup() {
        allPrivileges = new ArrayList<>();

        Privilege p1 = new Privilege();
        p1.setCode("READ_ONLY");
        p1.setDescription("Only have right to view patient test orders and patient test order results.");
        allPrivileges.add(p1);

        Privilege p2 = new Privilege();
        p2.setCode("CREATE_TEST_ORDER");
        p2.setDescription("Have right to create a new patient test order");
        allPrivileges.add(p2);

        Privilege p3 = new Privilege();
        p3.setCode("MODIFY_TEST_ORDER");
        p3.setDescription("Have right to modify information a patient test order.");
        allPrivileges.add(p3);

        Privilege p4 = new Privilege();
        p4.setCode("DELETE_TEST_ORDER");
        p4.setDescription("Have right to delete an exist test order.");
        allPrivileges.add(p4);

        Privilege p5 = new Privilege();
        p5.setCode("REVIEW_TEST_ORDER");
        p5.setDescription("Have right to review, modify test result of test order");
        allPrivileges.add(p5);

        Privilege p6 = new Privilege();
        p6.setCode("ADD_COMMENT");
        p6.setDescription("Have right to add a new comment for test result");
        allPrivileges.add(p6);

        Privilege p7 = new Privilege();
        p7.setCode("MODIFY_COMMENT");
        p7.setDescription("Have right to modify a comment.");
        allPrivileges.add(p7);

        Privilege p8 = new Privilege();
        p8.setCode("DELETE_COMMENT");
        p8.setDescription("Have right to delete a comment.");
        allPrivileges.add(p8);

        Privilege p9 = new Privilege();
        p9.setCode("VIEW_CONFIGURATION");
        p9.setDescription("Have right to view, add, modify and delete configurations.");
        allPrivileges.add(p9);

        Privilege p10 = new Privilege();
        p10.setCode("CREATE_CONFIGURATION");
        p10.setDescription("Have right to add a new configuration.");
        allPrivileges.add(p10);

        Privilege p11 = new Privilege();
        p11.setCode("MODIFY_CONFIGURATION");
        p11.setDescription("Have right to modify a configuration.");
        allPrivileges.add(p11);

        Privilege p12 = new Privilege();
        p12.setCode("DELETE_CONFIGURATION");
        p12.setDescription("Have right to delete a configuration.");
        allPrivileges.add(p12);

        Privilege p13 = new Privilege();
        p13.setCode("VIEW_USER");
        p13.setDescription("Have right to view all user profiles");
        allPrivileges.add(p13);

        Privilege p14 = new Privilege();
        p14.setCode("CREATE_USER");
        p14.setDescription("Have right to create a new user.");
        allPrivileges.add(p14);

        Privilege p15 = new Privilege();
        p15.setCode("MODIFY_USER");
        p15.setDescription("Have right to modify an user.");
        allPrivileges.add(p15);

        Privilege p16 = new Privilege();
        p16.setCode("DELETE_USER");
        p16.setDescription("Have right to delete an user.");
        allPrivileges.add(p16);

        Privilege p17 = new Privilege();
        p17.setCode("LOCK_UNLOCK_USER");
        p17.setDescription("Have right to lock or unlock an user.");
        allPrivileges.add(p17);

        Privilege p18 = new Privilege();
        p18.setCode("VIEW_ROLE");
        p18.setDescription("Have right to view all role privileges.");
        allPrivileges.add(p18);

        Privilege p19 = new Privilege();
        p19.setCode("CREATE_ROLE");
        p19.setDescription("Have right to create a new custom role.");
        allPrivileges.add(p19);

        Privilege p20 = new Privilege();
        p20.setCode("UPDATE_ROLE");
        p20.setDescription("Have right to modify privileges of custom role.");
        allPrivileges.add(p20);

        Privilege p21 = new Privilege();
        p21.setCode("DELETE_ROLE");
        p21.setDescription("Have right to delete a custom role.");
        allPrivileges.add(p21);


        privilegeRepository.saveAll(allPrivileges);
        System.out.println("--- @BeforeEach Setup ---");
        System.out.println("Number of privileges saved: " + allPrivileges.size());
        allPrivileges.forEach(p -> System.out.println("  Code: " + p.getCode() + ", ID: " + p.getPrivilegeId()));
        System.out.println("-------------------------");
    }


    @Test
    public void RoleRepository_Save_ReturnSavedRole() {
        //Arrange
        Set<Privilege> rolePrivileges = new HashSet<>();
        rolePrivileges.add(privilegeRepository.findByCode("READ_ONLY").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","READ_ONLY")));
        rolePrivileges.add(privilegeRepository.findByCode("CREATE_TEST_ORDER").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","CREATE_TEST_ORDER")));

        Role role = new Role();
        role.setName("testRole");
        role.setDescription("A role for testing purposes");
        role.setCode("TEST_ROLE");
        role.setPrivileges(rolePrivileges);

        //Act
        Role savedRole = roleRepository.save(role);

        //Assert
        Assertions.assertThat(roleRepository.findAll().size()).isEqualTo(1);
        Assertions.assertThat(savedRole).isNotNull();
        Assertions.assertThat(savedRole.getName()).isEqualTo(role.getName());
        Assertions.assertThat(savedRole.getDescription()).isEqualTo(role.getDescription());
        Assertions.assertThat(savedRole.getPrivileges().size()).isEqualTo(rolePrivileges.size());
        Assertions.assertThat(savedRole.getPrivileges().containsAll(rolePrivileges)).isTrue();
    }

    @Test
    public void RoleRepository_GetAll_ReturnAllRoles() {
        //Arrange
        Set<Privilege> rolePrivileges1 = new HashSet<>();
        rolePrivileges1.add(privilegeRepository.findByCode("READ_ONLY").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","READ_ONLY")));
        rolePrivileges1.add(privilegeRepository.findByCode("CREATE_TEST_ORDER").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","CREATE_TEST_ORDER")));
        Role role1 = new Role();
        role1.setName("testRole");
        role1.setDescription("A role for testing purposes");
        role1.setCode("TEST_ROLE");
        role1.setPrivileges(rolePrivileges1);

        Set<Privilege> rolePrivileges2 = new HashSet<>();
        rolePrivileges2.add(privilegeRepository.findByCode("MODIFY_USER").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","MODIFY_USER")));
        Role role2 = new Role();
        role2.setName("testRole2");
        role2.setDescription("A role for testing purposes2");
        role2.setCode("TEST_ROLE2");
        role2.setPrivileges(rolePrivileges2);

        Role savedRole = roleRepository.save(role1);
        Role savedRole2 = roleRepository.save(role2);

        //Act
        List<Role> allRoles = roleRepository.findAll();

        //Assert
        Assertions.assertThat(allRoles.size()).isEqualTo(2);
        Assertions.assertThat(allRoles.contains(savedRole)).isTrue();
        Assertions.assertThat(allRoles.contains(savedRole2)).isTrue();
    }

    @Test
    public void RoleRepository_GetAll_ReturnEmpty() {
        //Arrange

        //Act
        List<Role> allRoles = roleRepository.findAll();

        //Assert
        Assertions.assertThat(allRoles.size()).isEqualTo(0);
        Assertions.assertThat(allRoles).isEmpty();
    }

    @Test
    public void RoleRepository_Update_ReturnUpdatedRole() {
        //Arrange
        Set<Privilege> rolePrivileges = new HashSet<>();
        rolePrivileges.add(privilegeRepository.findByCode("READ_ONLY").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","READ_ONLY")));
        rolePrivileges.add(privilegeRepository.findByCode("CREATE_TEST_ORDER").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","CREATE_TEST_ORDER")));

        Role role = new Role();
        role.setName("testRole");
        role.setDescription("A role for testing purposes");
        role.setCode("TEST_ROLE");
        role.setPrivileges(rolePrivileges);
        Role savedRole = roleRepository.save(role);

        //Act
        Role updatedRole = roleRepository.findById(savedRole.getRoleId()).orElseThrow(()-> new ResourceNotFoundException("Role","Id",savedRole.getRoleId()));
        role.setName("testRoleUpdate");
        role.setDescription("Updated role");
        role.setCode("TEST_ROLE_UPDATE");
        rolePrivileges.clear();
        updatedRole.setPrivileges(rolePrivileges);
        Role savedUpdatedRole = roleRepository.save(updatedRole);

        //Assert
        Assertions.assertThat(savedUpdatedRole.getName()).isEqualTo(updatedRole.getName());
        Assertions.assertThat(savedUpdatedRole.getDescription()).isEqualTo(updatedRole.getDescription());
        Assertions.assertThat(savedUpdatedRole.getPrivileges().size()).isEqualTo(0);
        Assertions.assertThat(roleRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void RoleRepository_Delete_ReturnDeletedRole() {
        //Arrange
        Set<Privilege> rolePrivileges = new HashSet<>();
        rolePrivileges.add(privilegeRepository.findByCode("READ_ONLY").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","READ_ONLY")));
        rolePrivileges.add(privilegeRepository.findByCode("CREATE_TEST_ORDER").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","CREATE_TEST_ORDER")));

        Role role = new Role();
        role.setName("testRole");
        role.setDescription("A role for testing purposes");
        role.setCode("TEST_ROLE");
        role.setPrivileges(rolePrivileges);
        Role savedRole = roleRepository.save(role);

        //Act
        roleRepository.deleteById(savedRole.getRoleId());

        //Assert
        Assertions.assertThat(roleRepository.findAll().size()).isEqualTo(0);
        Assertions.assertThat(roleRepository.findById(savedRole.getRoleId())).isEmpty();
        Assertions.assertThat(roleRepository.findByCode("TEST_ROLE")).isEmpty();

    }

    @Test
    public void RoleRepository_DeleteNothing_ReturnSameAsDB() {
        //Arrange
        Set<Privilege> rolePrivileges = new HashSet<>();
        rolePrivileges.add(privilegeRepository.findByCode("READ_ONLY").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","READ_ONLY")));
        rolePrivileges.add(privilegeRepository.findByCode("CREATE_TEST_ORDER").orElseThrow(()-> new ResourceNotFoundException("Role","Privilege","CREATE_TEST_ORDER")));

        Role role = new Role();
        role.setName("testRole");
        role.setDescription("A role for testing purposes");
        role.setCode("TEST_ROLE");
        role.setPrivileges(rolePrivileges);
        Role savedRole = roleRepository.save(role);

        //Act
        roleRepository.deleteById(savedRole.getRoleId()+1);

        //Assert
        Assertions.assertThat(roleRepository.findAll().size()).isEqualTo(1);
        Assertions.assertThat(roleRepository.findById(savedRole.getRoleId())).isNotNull();
        Assertions.assertThat(roleRepository.findByCode("TEST_ROLE")).isNotNull();

    }


}
