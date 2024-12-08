package be.pxl.services.controller;

import be.pxl.services.domain.dto.UserCommentRequest;
import be.pxl.services.domain.dto.UserCommentResponse;
import be.pxl.services.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<UserCommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody UserCommentRequest request) {
        logger.info("Request received to add a comment to post ID: {}", postId);
        logger.debug("Comment request details: {}", request);
        UserCommentResponse userComment = commentService.addComment(request);
        logger.info("Comment added successfully with ID: {}", userComment.getId());
        return ResponseEntity.ok(userComment);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<UserCommentResponse>> getCommentsByPost(@PathVariable Long postId) {
        logger.info("Request received to retrieve comments for post ID: {}", postId);
        List<UserCommentResponse> comments = commentService.getCommentsByPostId(postId);
        logger.debug("Retrieved {} comments for post ID: {}", comments.size(), postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}/update")
    public ResponseEntity<UserCommentResponse> updateComment(@PathVariable Long commentId, @RequestBody UserCommentRequest request) {
        logger.info("Request received to update comment with ID: {}", commentId);
        logger.debug("Update request details: {}", request);
        UserCommentResponse userComment = commentService.updateComment(commentId, request);
        logger.info("Comment with ID: {} updated successfully", commentId);
        return ResponseEntity.ok(userComment);
    }

    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
        logger.info("Request received to delete comment with ID: {}", commentId);
        String deleteComment = commentService.deleteComment(commentId);
        logger.info("Comment with ID: {} deleted successfully", commentId);
        return ResponseEntity.ok(deleteComment);
    }
}
