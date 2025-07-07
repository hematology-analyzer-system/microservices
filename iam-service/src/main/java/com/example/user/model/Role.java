package com.example.user.model;
import jakarta.persistence.*;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;
@Data
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    private String name;
    private String description;
    private String code;

//    @ManyToMany(mappedBy = "roles")
//    @JsonIgnore
//    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "role_privileges",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    private Set<Privilege> privileges = new HashSet<>();
}

