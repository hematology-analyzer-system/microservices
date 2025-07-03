package com.example.user.Model;
import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;
@Data
@Entity
@Table(name = "privileges")
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privilegeId;

    private String code;
    private String description;

//    @ManyToMany(mappedBy = "privileges")
//    private Set<Role> roles = new HashSet<>();
}
