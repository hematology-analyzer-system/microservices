package com.example.user.dto.role;
import com.example.user.model.Privilege;
import lombok.Data;

import java.util.Set;

@Data
public class RoleResponse {
    private String name;
    private String code;
    private String description;
    private Set<String> privileges;
    public RoleResponse( String name,String code, String description, Set<Privilege> privileges) {

        this.name = name;
        this.code = code;
        this.description = description;
        this.privileges = privileges.stream()
                .map(Privilege::getCode)
                .collect(java.util.stream.Collectors.toSet());
    }
}
