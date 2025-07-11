package com.example.demo.controller;

import com.example.demo.dto.Comment.AddCommentRequest;
import com.example.demo.dto.Comment.CommentResponse;
import com.example.demo.dto.Comment.UpdateCommentRequest;
import com.example.demo.repository.CommentRepository;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/{userId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable("userId") Long userId,
            @RequestBody AddCommentRequest addCommentRequest
    ){
        CommentResponse commentResponse = commentService.addComment(userId, addCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @PutMapping("/{userId}/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("userId") Long userId,
            @PathVariable("commentId") Long commentId,
            @RequestBody UpdateCommentRequest updateCommentRequest
    ){
        CommentResponse commentResponse = commentService.modifiedComment(commentId, userId, updateCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/{userId}/{commentId}")
    public String deleteComment(
            @PathVariable("userId") Long userId,
            @PathVariable("commentId") Long commentId
    ){
        return  commentService.deleteComment(userId, commentId);
    }
}
