package com.example.demo.dto.Comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCommentRequest {
    @NotBlank(message = "Content is required")
    private String content;

    private Long testOrderId;

    private Long resultId;
}
