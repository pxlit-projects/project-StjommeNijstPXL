package be.pxl.services.controller;

import be.pxl.services.domain.UserComment;
import be.pxl.services.domain.dto.UserCommentRequest;
import be.pxl.services.domain.dto.UserCommentResponse;
import be.pxl.services.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<UserCommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody UserCommentRequest request) {
        UserCommentResponse userComment = commentService.addComment(request);
        return ResponseEntity.ok(userComment);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<UserCommentResponse>> getCommentsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @PutMapping("/{commentId}/update")
    public ResponseEntity<UserCommentResponse> updateComment(@PathVariable Long commentId, @RequestBody UserCommentRequest request) {
        UserCommentResponse userComment = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(userComment);
    }

    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        String deleteComment = commentService.deleteComment(commentId);
        return ResponseEntity.ok(deleteComment);
    }
}

