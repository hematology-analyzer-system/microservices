package com.example.demo.controller;

import com.example.demo.dto.Comment.AddCommentRequest;
import com.example.demo.dto.Comment.CommentResponse;
import com.example.demo.dto.Comment.UpdateCommentRequest;
//import com.example.demo.repository.CommentRepository;
import com.example.demo.security.CurrentUser;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

//    @Autowired
//    private CommentRepository commentRepository;

    @PostMapping("add")
    public ResponseEntity<CommentResponse> addComment(
            @RequestBody AddCommentRequest addCommentRequest
    ){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        CommentResponse commentResponse = commentService.addComment(currentUser.getUserId(), addCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody UpdateCommentRequest updateCommentRequest
    ){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        CommentResponse commentResponse = commentService.modifiedComment(commentId, currentUser.getUserId(), updateCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment(
            @PathVariable("commentId") Long commentId
    ){
        CurrentUser currentUser = (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return  commentService.deleteComment(currentUser.getUserId(), commentId);
    }
}
