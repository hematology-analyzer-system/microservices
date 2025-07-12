package com.example.user.controller;

import com.example.user.model.Privilege;
import com.example.user.security.JwtService;
import com.example.user.service.PrivilegeService;
import com.example.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = PrivilegeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class PrivilegeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrivilegeService privilegeService;
    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void PrivilegeController_create_ShouldReturnCreatedPrivilege() throws Exception {
        // Arrange
        Privilege privilege = new Privilege();
        privilege.setCode("READ");
        privilege.setDescription("Read permission");

        given(privilegeService.createPrivilege(Mockito.any(Privilege.class))).willReturn(privilege);

        // Act & Assert
        ResultActions response = mockMvc.perform(post("/privileges")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(privilege)));

        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(privilege.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(privilege.getDescription()));
    }

    @Test
    public void PrivilegeController_list_ShouldReturnAllPrivileges() throws Exception {
        // Arrange
        Privilege privilege1 = new Privilege();
        privilege1.setCode("READ");
        privilege1.setDescription("Read privilege");

        Privilege privilege2 = new Privilege();
        privilege2.setCode("WRITE");
        privilege2.setDescription("Write privilege");

        List<Privilege> privileges = List.of(privilege1, privilege2);

        given(privilegeService.getAllPrivileges()).willReturn(privileges);

        // Act & Assert
        mockMvc.perform(get("/privileges")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(privileges.size()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].code").value(privilege1.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(privilege1.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].code").value(privilege2.getCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description").value(privilege2.getDescription()));
    }

    @Test
    public void PrivilegeController_get_ShouldReturnPrivilegeById() throws Exception {
        // Arrange
        Long privilegeId = 1L;
        Privilege privilege = new Privilege();
        privilege.setCode("READ");
        privilege.setDescription("Read privilege");

        given(privilegeService.getPrivilegeById(privilegeId)).willReturn(Optional.of(privilege));

        // Act & Assert
        mockMvc.perform(get("/privileges/{id}", privilegeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("READ"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Read privilege"));
    }

    @Test
    public void PrivilegeController_delete_ShouldReturnNoContent() throws Exception {
        // Arrange
        Long privilegeId = 1L;

        // Giả sử service không ném exception
        willDoNothing().given(privilegeService).deletePrivilege(privilegeId);

        // Act & Assert
        mockMvc.perform(delete("/privileges/{id}", privilegeId))
                .andExpect(status().isNoContent());

        // Xác minh service được gọi đúng
        then(privilegeService).should(times(1)).deletePrivilege(privilegeId);
    }

}
