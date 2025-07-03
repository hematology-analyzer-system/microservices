package com.example.user.Model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "phone"),
        @UniqueConstraint(columnNames = "identifyNum")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String Gender;
    private String Date_of_Birth;
    private Integer Age;
    private String Address;
    private String password;
    private String status;
    @Column(name = "identifyNum")
    private String identifyNum;
    private LocalDateTime update_at;
    private LocalDateTime create_at;
    @PrePersist
    protected void onCreate() {
        this.create_at = LocalDateTime.now();
        this.update_at = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.update_at = LocalDateTime.now();
    }
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
