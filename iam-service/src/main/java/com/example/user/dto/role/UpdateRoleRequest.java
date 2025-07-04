package com.example.user.dto.role;
import lombok.Data;
@Data
public class UpdateRoleRequest {
    private String name;
    private String description;

    public UpdateRoleRequest() {}

    public UpdateRoleRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
