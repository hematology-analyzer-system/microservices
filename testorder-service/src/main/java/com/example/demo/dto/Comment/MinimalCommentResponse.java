package com.example.demo.dto.Comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinimalCommentResponse {
    String content;
    String createdBy;
    String updateBy;
}
