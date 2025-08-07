package com.example.user.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "modified_history")
public class ModifiedHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime updatedAt;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "updated_by_email")),
            @AttributeOverride(name = "fullName", column = @Column(name = "updated_by_fullname")),
            @AttributeOverride(name = "userId", column = @Column(name = "updated_by_userid")),
            @AttributeOverride(name = "identifyNum", column = @Column(name = "updated_by_identify_num"))
    })
    private UserAuditInfo updatedBy;

}
