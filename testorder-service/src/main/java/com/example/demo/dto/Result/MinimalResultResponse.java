package com.example.demo.dto.Result;

import com.example.demo.dto.Comment.MinimalCommentResponse;
import com.example.demo.dto.DetailResult.DetailResultResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MinimalResultResponse {
    private Long id;

    private Boolean reviewed;

    private String updateBy;

    private LocalDateTime createdAt;

    private List<MinimalCommentResponse> comment_result;

    private List<DetailResultResponse> detailResults;
}
