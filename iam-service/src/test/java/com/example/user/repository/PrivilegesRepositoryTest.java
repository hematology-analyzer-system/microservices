package com.example.user.repository;

import com.example.user.DataInitializer;
import com.example.user.config.AuditTestConfig;
import com.example.user.model.Privilege;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Import(AuditTestConfig.class)
public class PrivilegesRepositoryTest {

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
    public void PriviledgeRepository_Save_ReturnSavePrivilege(){
        //Arrange

        Privilege p1 = new Privilege();
        p1.setCode("ABC");
        p1.setDescription("ABC");

        Optional<Privilege> expected =  Optional.of(p1);

        //Act
        Privilege savedp1 = privilegeRepository.save(p1);

        //Assert
        Assertions.assertThat(p1).isNotNull();
        Assertions.assertThat(savedp1).isNotNull();
        Assertions.assertThat(savedp1.getCode()).isEqualTo("ABC");
        Assertions.assertThat(savedp1.getDescription()).isEqualTo("ABC");
        Assertions.assertThat(privilegeRepository.findAll()).hasSize(22);
        Assertions.assertThat(privilegeRepository.findByCode(savedp1.getCode())).isNotNull();
        Assertions.assertThat(privilegeRepository.findByCode(savedp1.getCode())).isEqualTo(expected);

    }

    @Test
    public void PrivilegeRepository_FindAll_ReturnAllPrivileges() {
        //Arrange

        //Act
        List<Privilege> allPrivileges = privilegeRepository.findAll();

        //Assert
        Assertions.assertThat(allPrivileges).isNotNull();
        Assertions.assertThat(allPrivileges).isNotEmpty();
        Assertions.assertThat(allPrivileges.size()).isGreaterThan(0);
        Assertions.assertThat(allPrivileges.size()).isEqualTo(21);

    }

    @Test
    public void PrivilegeRepository_FindByCode_Existing_ReturnsPrivilege() {
        // Arrange
        String code = "READ_ONLY";

        // Act
        Optional<Privilege> result = privilegeRepository.findByCode(code);

        // Assert
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getCode()).isEqualTo(code);
    }

    @Test
    public void PrivilegeRepository_FindByCode_NotExisting_ReturnsEmpty() {
        // Act
        Optional<Privilege> result = privilegeRepository.findByCode("NOT_EXIST");

        // Assert
        Assertions.assertThat(result).isNotPresent();
    }

    @Test
    public void PrivilegeRepository_DeleteById_RemovesPrivilege() {
        // Arrange
        Privilege privilege = new Privilege();
        privilege.setCode("TO_DELETE");
        privilege.setDescription("Will be deleted");
        Privilege saved = privilegeRepository.save(privilege);

        // Act
        privilegeRepository.deleteById(saved.getPrivilegeId());

        // Assert
        Optional<Privilege> result = privilegeRepository.findById(saved.getPrivilegeId());
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    public void PrivilegeRepository_UpdatePrivilege_ChangesArePersisted() {
        // Arrange
        Privilege privilege = new Privilege();
        privilege.setCode("TO_UPDATE");
        privilege.setDescription("Old description");
        Privilege saved = privilegeRepository.save(privilege);

        // Act
        saved.setDescription("Updated description");
        Privilege updated = privilegeRepository.save(saved);

        // Assert
        Assertions.assertThat(updated.getDescription()).isEqualTo("Updated description");
    }

    @Test
    public void PrivilegeRepository_FindById_ReturnsCorrectPrivilege() {
        // Arrange
        Privilege privilege = new Privilege();
        privilege.setCode("FIND_ID");
        privilege.setDescription("Find by ID test");
        Privilege saved = privilegeRepository.save(privilege);

        // Act
        Optional<Privilege> result = privilegeRepository.findById(saved.getPrivilegeId());

        // Assert
        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getCode()).isEqualTo("FIND_ID");
    }

}
