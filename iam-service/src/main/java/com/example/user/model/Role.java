package com.example.user.model;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;
@Data
@Entity
@Table(name = "roles")
@EntityListeners(AuditingEntityListener.class)
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
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "created_by_email")),
            @AttributeOverride(name = "fullName", column = @Column(name = "created_by_full_name")),
            @AttributeOverride(name = "userId", column = @Column(name = "created_by_user_id")),
            @AttributeOverride(name = "identifyNum", column = @Column(name = "created_by_identify_num"))
    })
    private UserAuditInfo createdBy;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "updated_by_email")),
            @AttributeOverride(name = "fullName", column = @Column(name = "updated_by_full_name")),
            @AttributeOverride(name = "userId", column = @Column(name = "updated_by_user_id")),
            @AttributeOverride(name = "identifyNum", column = @Column(name = "updated_by_identify_num"))
    })
    private UserAuditInfo updatedBy;
}

