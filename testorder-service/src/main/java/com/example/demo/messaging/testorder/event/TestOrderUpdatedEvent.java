package com.example.demo.messaging.testorder.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event payload for test order updates
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestOrderUpdatedEvent {
    private Long testOrderId;
    private Integer patientId;
    private String doctorId;
    private String status;
    private String previousStatus;
    private LocalDateTime updatedAt;
    private String notes;
}