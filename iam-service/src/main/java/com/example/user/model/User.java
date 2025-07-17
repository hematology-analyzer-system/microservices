package com.example.user.model;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "phone"),
        @UniqueConstraint(columnNames = "identifyNum")
})
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String phone;
    private String gender;
    private String Date_of_Birth;
//    private Integer Age;
    private String address;
    private String password;
    private String status;
    @Column(name = "identifyNum", unique = true)
    private String identifyNum;
    private LocalDateTime update_at;
    private LocalDateTime create_at;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "created_by_email")),
            @AttributeOverride(name = "fullName", column = @Column(name = "created_by_fullname")),
            @AttributeOverride(name = "userId", column = @Column(name = "created_by_userid")),
            @AttributeOverride(name = "identifyNum", column = @Column(name = "created_by_identify_num"))
    })
    private UserAuditInfo createdBy;

    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "updated_by_email")),
            @AttributeOverride(name = "fullName", column = @Column(name = "updated_by_fullname")),
            @AttributeOverride(name = "userId", column = @Column(name = "updated_by_userid")),
            @AttributeOverride(name = "identifyNum", column = @Column(name = "updated_by_identify_num"))
    })
    @Embedded
    private UserAuditInfo updatedBy;
    @PrePersist
    protected void onCreate() {
        this.create_at = LocalDateTime.now();
        this.update_at = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.update_at = LocalDateTime.now();
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email; // Using email as username
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equals(status);
    }
}