package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "results")
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    private Boolean reviewed;

    private String value;

    private String unit;

    private String rangeMin;

    private String rangeMax;

    @Column(name = "updateBy")
    private String updateBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_order_id")
    private TestOrder testOrder;

    @OneToOne(mappedBy = "result", cascade = CascadeType.ALL)
    private Comment comment;
}
