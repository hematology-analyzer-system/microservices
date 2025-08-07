package com.example.user.dto.role;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRoleRequest {
    private Long roleId;
    private String code;
    private String name;
    private String description;
    private List<Long> privilegesIds;

    public UpdateRoleRequest() {}

    public UpdateRoleRequest(Long roleId, String code, String name, String description, List<Long> privilegesIds) {
        this.roleId = roleId;
        this.name = name;
        this.code = code;
        this.privilegesIds = privilegesIds;
        this.description = description;
    }
}
