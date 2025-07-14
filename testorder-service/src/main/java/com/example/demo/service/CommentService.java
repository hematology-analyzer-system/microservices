package com.example.demo.service;

import com.example.demo.dto.Comment.AddCommentRequest;
import com.example.demo.dto.Comment.CommentResponse;
import com.example.demo.dto.Comment.UpdateCommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Result;
import com.example.demo.entity.TestOrder;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ForbiddenActionException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.ResultRepository;
import com.example.demo.repository.TestOrderRepository;
import com.example.demo.security.CurrentUser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {
    private CommentRepository commentRepository;

    private TestOrderRepository testOrderRepository;

    private ResultRepository resultRepository;

    private String formatlizeCreatedBy(Long id, String name, String email, String identifyNum){
        return String.format(
                "ID: %d | Name: %s | Email: %s | IdNum: %s",
                id, name, email, identifyNum
        );
    }

    public CommentResponse addComment(Long userId, AddCommentRequest addCommentRequest) {
        if(addCommentRequest.getTestOrderId() == null && addCommentRequest.getResultId() == null){
            throw new RuntimeException("Comment must belong to TestOrder or Result");
        }

        TestOrder testOrder = null;
        Result result = null;

        if(addCommentRequest.getTestOrderId() != null){
            testOrder = testOrderRepository.findById(addCommentRequest.getTestOrderId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Test Order Not Found"));
        }

        if(addCommentRequest.getResultId() != null){
            result = resultRepository.findById(addCommentRequest.getResultId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Result Not Found"));
        }



        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        String createdByinString = formatlizeCreatedBy(currentUser.getUserId(), currentUser.getEmail()
                , currentUser.getEmail(), currentUser.getIdentifyNum());

        Comment comment = Comment.builder()
                .userId(userId)
                .content(addCommentRequest.getContent())
                .createBy(createdByinString)
                .result(result)
                .testOrder(testOrder)
                .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public CommentResponse modifiedComment(
            Long commentId,
            Long userId,
            UpdateCommentRequest updateCommentRequest) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment Not Found"));

        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenActionException("User can't change this comment");
        }

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        String createdByinString = formatlizeCreatedBy(currentUser.getUserId(), currentUser.getEmail()
                , currentUser.getEmail(), currentUser.getIdentifyNum());

        comment.setContent(updateCommentRequest.getContent());
        comment.setUpdateBy(createdByinString);

        commentRepository.save(comment);

        return CommentResponse.builder()
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();

    }

    public String deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment Not Found"));

        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenActionException("User can't change this comment");
        }

        commentRepository.deleteById(commentId);

        return "Delete Successfully";
    }
}
