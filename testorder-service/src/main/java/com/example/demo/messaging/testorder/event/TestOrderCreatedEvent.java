package com.example.demo.messaging.testorder.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event payload for test order creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestOrderCreatedEvent {
    private Long testOrderId;
    private Integer patientId;
    private String doctorId;
    private String status;
    private LocalDateTime createdAt;
    private String notes;
}