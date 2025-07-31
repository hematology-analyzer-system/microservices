package com.example.user.model;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "privileges")
public class Privilege {

    @Id
    private Long privilegeId;

    

    private String code;
    private String description;

//    @ManyToMany(mappedBy = "privileges")
//    private Set<Role> roles = new HashSet<>();
    public Long getPrivilegeId() {
        return privilegeId;
    }
}
