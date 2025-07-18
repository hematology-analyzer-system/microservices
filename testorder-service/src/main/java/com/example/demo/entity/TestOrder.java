package com.example.demo.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder

@EqualsAndHashCode(exclude = "commentTO")
@ToString(exclude = "commentTO")

@Table(name = "test_orders")
public class TestOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testId;

    @Column(name = "createdBy", updatable = false)
    private String createdBy;

    @Column(name = "updateBy")
    private String updateBy;

    private String runBy;

    @CreationTimestamp
    private LocalDateTime runAt;

    private String status;

    @OneToMany(mappedBy = "testOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Result> results = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToMany(mappedBy = "testOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentTO> commentTO = new ArrayList<>();
}
