package com.example.user.controller;

import com.example.user.dto.role.PageRoleResponse;
import com.example.user.dto.role.RoleRequest;
import com.example.user.dto.role.RoleResponse;
import com.example.user.dto.role.UpdateRoleRequest;
import com.example.user.model.Privilege;
import com.example.user.model.Role;
import com.example.user.security.JwtService;
import com.example.user.service.RoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RoleController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private RoleService roleService;
    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Role role;
    private UpdateRoleRequest  updateRoleRequest;
    private RoleRequest roleRequest;
    private RoleResponse roleResponse;
    private PageRoleResponse pageRoleResponse;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setName("ADMIN");
        role.setDescription("ADMIN");

        updateRoleRequest = new UpdateRoleRequest();
        updateRoleRequest.setName("MANAGER");
        updateRoleRequest.setDescription("MANAGER");


        roleRequest = new RoleRequest();
        roleRequest.setSort("name");
        roleRequest.setFilter("admin");
        roleRequest.setPage_num(0);
        roleRequest.setPage_size(5);

        Privilege privilege1 = new Privilege();
        privilege1.setCode("READ");
        privilege1.setDescription("Read privilege");

        Privilege privilege2 = new Privilege();
        privilege2.setCode("WRITE");
        privilege2.setDescription("Write privilege");

        Set<Privilege> privilegeSet = new HashSet<>();
        privilegeSet.add(privilege1);
        privilegeSet.add(privilege2);

        roleResponse = new RoleResponse(
                "Admin",
                "ADMIN",
                "Administrator role with full permissions",
                privilegeSet
        );

        List<RoleResponse> roles = List.of(roleResponse);
        pageRoleResponse = new PageRoleResponse(
                roles, 1, 0, 5, "name", "asc"
        );
    }

    @Test
    public void RoleController_create_ShouldReturnCreatedRole() throws Exception {
        // Arrang

        given(roleService.createRole(Mockito.any(Role.class))).willReturn(role);

        // Act
        ResultActions response = mockMvc.perform(post("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(role.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(role.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(role.getDescription()));

        then(roleService).should(Mockito.times(1)).createRole(Mockito.any(Role.class));
    }

    @Test
    public void RoleController_list_ShouldReturnRoleList() throws Exception {
        // Arrange
        List<Role> roles = List.of(role); // `role` được khởi tạo trong @BeforeEach
        given(roleService.getAllRoles()).willReturn(roles);

        // Act
        ResultActions response = mockMvc.perform(get("/roles")
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(roles.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(role.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(role.getDescription()));
    }

    @Test
    public void RoleController_get_ShouldReturnRoleById() throws Exception {
        // Arrange
        Long roleId = 1L;
        given(roleService.getRoleById(roleId)).willReturn(Optional.of(role));

        // Act
        ResultActions response = mockMvc.perform(get("/roles/{id}", roleId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(role.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(role.getDescription()));
    }

    @Test
    public void RoleController_get_ShouldReturnNotFound_WhenRoleDoesNotExist() throws Exception {
        // Arrange
        Long nonExistingId = 99L;
        given(roleService.getRoleById(nonExistingId)).willReturn(Optional.empty());

        // Act
        ResultActions response = mockMvc.perform(get("/roles/{id}", nonExistingId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isNotFound());
    }

    @Test
    public void RoleController_delete_ShouldReturnNoContent() throws Exception {
        // Arrange
        Long roleId = 1L;
        willDoNothing().given(roleService).deleteRole(roleId);

        // Act
        ResultActions response = mockMvc.perform(delete("/roles/{id}", roleId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isNoContent());

        // Verify
        then(roleService).should(times(1)).deleteRole(roleId);
    }

    @Test
    public void RoleController_assignPrivilegeToRole_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        Long roleId = 1L;
        Long privilegeId = 100L;
        willDoNothing().given(roleService).assignPrivilegeToRole(roleId, privilegeId);

        // Act
        ResultActions response = mockMvc.perform(post("/roles/{roleId}/privileges/{privilegeId}", roleId, privilegeId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().string("Privilege assigned to role successfully."));

        // Verify
        then(roleService).should(times(1)).assignPrivilegeToRole(roleId, privilegeId);
    }

    @Test
    public void RoleController_removePrivilegeFromRole_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        Long roleId = 1L;
        Long privilegeId = 100L;
        willDoNothing().given(roleService).removePrivilegeFromRole(roleId, privilegeId);

        // Act
        ResultActions response = mockMvc.perform(delete("/roles/{roleId}/privileges/{privilegeId}", roleId, privilegeId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(content().string("Privilege removed from role successfully."));

        // Verify
        then(roleService).should(times(1)).removePrivilegeFromRole(roleId, privilegeId);
    }

    @Test
    public void RoleController_updateRole_ShouldReturnUpdatedRole() throws Exception {
        Long roleId = 1L;
        role.setName(updateRoleRequest.getName());
        role.setDescription(updateRoleRequest.getDescription());

        given(roleService.updateRole(eq(roleId), any(UpdateRoleRequest.class)))
                .willReturn(role);

        mockMvc.perform(put("/roles/{id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRoleRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updateRoleRequest.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(updateRoleRequest.getDescription()));
    }

    @Test
    public void RoleController_getAllPaged_ShouldReturnPagedRoles() throws Exception {
        // Arrange
        int page = 0;
        int size = 5;
        String sortBy = "name";
        String sortDirection = "asc";

        given(roleService.getAllRoles(page, size, sortBy, sortDirection))
                .willReturn(pageRoleResponse);

        // Act & Assert
        mockMvc.perform(get("/roles/paging")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sortBy", sortBy)
                        .param("sortDirection", sortDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles.length()").value(pageRoleResponse.getRoles().size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(pageRoleResponse.getTotalElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sortBy").value(sortBy))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sortDirection").value(sortDirection))
                .andExpect(MockMvcResultMatchers.jsonPath("$.empty").value(false));
    }

    @Test
    public void RoleController_searchRoles_ShouldReturnPagedResult() throws Exception {
        // Arrange
        given(roleService.searchRoles(roleRequest)).willReturn(pageRoleResponse);

        // Act & Assert
        mockMvc.perform(post("/roles/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles.length()").value(pageRoleResponse.getRoles().size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(pageRoleResponse.getTotalElements()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sortBy").value(pageRoleResponse.getSortBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.sortDirection").value(pageRoleResponse.getSortDirection()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.empty").value(false));
    }

}
