package com.example.user.model;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "privileges")
public class Privilege {
    //    @ManyToMany(mappedBy = "privileges")
    //    private Set<Role> roles = new HashSet<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privilegeId;

    

    private String code;
    private String description;

}
