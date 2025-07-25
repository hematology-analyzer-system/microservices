package com.example.demo.dto.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinimalCommentResponse {
    private Long id;
    private String content;
    private String createdBy;
    private String updateBy;

    private LocalDateTime createdAt;
}
