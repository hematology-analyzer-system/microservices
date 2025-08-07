package com.example.demo.controller;

import com.example.demo.dto.Comment.AddCommentRequest;
import com.example.demo.dto.Comment.CommentResponse;
import com.example.demo.dto.Comment.UpdateCommentRequest;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

//    @Autowired
//    private CommentRepository commentRepository;

    @PostMapping("addTO/{id}")
    public ResponseEntity<CommentResponse> addCommentTO(
            @PathVariable Long id,
            @RequestBody AddCommentRequest addCommentRequest
    ){

        CommentResponse commentResponse = commentService.addCommentTO(id, addCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @PostMapping("addResult/{id}")
    public ResponseEntity<CommentResponse> addCommentResult(
            @PathVariable Long id,
            @RequestBody AddCommentRequest addCommentRequest
    ){

        CommentResponse commentResponse = commentService.addCommentResult(id, addCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @PutMapping("/result/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody UpdateCommentRequest updateCommentRequest
    ){

        CommentResponse commentResponse = commentService.modifiedComment(commentId, updateCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @PutMapping("/testorder/{commentId}")
    public ResponseEntity<CommentResponse> updateCommentTO(
            @PathVariable("commentId") Long commentId,
            @RequestBody UpdateCommentRequest updateCommentRequest
    ){

        CommentResponse commentResponse = commentService.modifiedCommentTO(commentId, updateCommentRequest);

        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/result/{commentId}")
    public String deleteComment(
            @PathVariable("commentId") Long commentId
    ){
        return  commentService.deleteComment(commentId);
    }

    @DeleteMapping("/testorder/{commentId}")
    public String deleteCommentTO(
            @PathVariable("commentId") Long commentId
    ){
        return  commentService.deleteCommentTO(commentId);
    }
}
