package com.example.demo.service;

import com.example.demo.dto.Comment.AddCommentRequest;
import com.example.demo.dto.Comment.CommentResponse;
import com.example.demo.dto.Comment.UpdateCommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.entity.CommentTO;
import com.example.demo.entity.Result;
import com.example.demo.entity.TestOrder;
import com.example.demo.exception.ApiException;
import com.example.demo.exception.ForbiddenActionException;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.CommentTORepository;
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

    private CommentTORepository commentTORepository;

    private TestOrderRepository testOrderRepository;

    private ResultRepository resultRepository;

    private String formatlizeCreatedBy(Long id, String name, String email, String identifyNum){
        return String.format(
                "ID: %d | Name: %s | Email: %s | IdNum: %s",
                id, name, email, identifyNum
        );
    }

    public CommentResponse addCommentTO(Long toId, AddCommentRequest addCommentRequest) {

        TestOrder testOrder = testOrderRepository.findById(toId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Test Order Not Found"));

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        String createdByinString = formatlizeCreatedBy(currentUser.getUserId(), currentUser.getFullname()
                , currentUser.getEmail(), currentUser.getIdentifyNum());

        CommentTO comment = CommentTO.builder()
                .userId(currentUser.getUserId())
                .content(addCommentRequest.getContent())
                .createBy(createdByinString)
                .testOrder(testOrder)
                .build();

        commentTORepository.save(comment);

        return CommentResponse.builder()
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public CommentResponse addCommentResult(Long resultId, AddCommentRequest addCommentRequest) {

        Result result = resultRepository.findById(resultId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Result Not Found"));

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        String createdByinString = formatlizeCreatedBy(currentUser.getUserId(), currentUser.getFullname()
                , currentUser.getEmail(), currentUser.getIdentifyNum());

        Comment comment = Comment.builder()
                .userId(currentUser.getUserId())
                .content(addCommentRequest.getContent())
                .createBy(createdByinString)
                .result(result)
                .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public CommentResponse modifiedComment(
            Long commentId,
            UpdateCommentRequest updateCommentRequest) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment Not Found"));

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        String createdByinString = formatlizeCreatedBy(currentUser.getUserId(), currentUser.getFullname()
                , currentUser.getEmail(), currentUser.getIdentifyNum());

        if (!comment.getUserId().equals(currentUser.getUserId())) {
            throw new ForbiddenActionException("User can't change this comment");
        }

        comment.setContent(updateCommentRequest.getContent());
        comment.setUpdateBy(createdByinString);

        commentRepository.save(comment);

        return CommentResponse.builder()
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public CommentResponse modifiedCommentTO(
            Long commentId,
            UpdateCommentRequest updateCommentRequest) {

        CommentTO comment = commentTORepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment Not Found"));

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        String createdByinString = formatlizeCreatedBy(currentUser.getUserId(), currentUser.getFullname()
                , currentUser.getEmail(), currentUser.getIdentifyNum());

        if (!comment.getUserId().equals(currentUser.getUserId())) {
            throw new ForbiddenActionException("User can't change this comment");
        }

        comment.setContent(updateCommentRequest.getContent());
        comment.setUpdateBy(createdByinString);

        commentTORepository.save(comment);

        return CommentResponse.builder()
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public String deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment Not Found"));

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        if (!comment.getUserId().equals(currentUser.getUserId())) {
            throw new ForbiddenActionException("User can't change this comment");
        }

        commentRepository.deleteById(commentId);

        return "Delete Successfully";
    }

    public String deleteCommentTO(Long commentId) {
        CommentTO comment = commentTORepository.findById(commentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Comment Not Found"));

        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        if (!comment.getUserId().equals(currentUser.getUserId())) {
            throw new ForbiddenActionException("User can't change this comment");
        }

        commentTORepository.deleteById(commentId);

        return "Delete Successfully";
    }
}
