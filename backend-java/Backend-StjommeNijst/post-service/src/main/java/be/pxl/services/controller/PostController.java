package be.pxl.services.controller;

import be.pxl.services.domain.dto.*;
import be.pxl.services.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest, HttpServletRequest request) {
        logger.info("Request received to create a post: {}", postRequest);
        PostResponse createdPost = postService.createPost(postRequest);
        logger.info("Post created with ID: {}", createdPost.getId());
        return ResponseEntity.ok(createdPost);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        logger.info("Request received to retrieve all posts");
        List<PostResponse> posts = postService.getAllPosts();
        logger.debug("Retrieved {} posts", posts.size());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        logger.info("Request received to retrieve post with ID: {}", id);
        PostResponse post = postService.getPostById(id);
        if (post == null) {
            logger.warn("Post with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Post retrieved: {}", post);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/concepts")
    public ResponseEntity<List<PostResponse>> getConceptPosts() {
        logger.info("Request received to retrieve concept posts");
        List<PostResponse> posts = postService.getConceptPosts();
        logger.debug("Retrieved {} concept posts", posts.size());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/declined")
    public ResponseEntity<List<PostResponseWithComment>> getDeclinedPosts() {
        logger.info("Request received to retrieve declined posts");
        List<PostResponseWithComment> posts = postService.getDeclinedPosts();
        logger.debug("Retrieved {} declined posts", posts.size());
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/notapproved")
    public ResponseEntity<List<PostResponse>> getNotApprovedPosts() {
        logger.info("Request received to retrieve not-approved posts");
        List<PostResponse> posts = postService.getNotApprovedPosts();
        logger.debug("Retrieved {} not-approved posts", posts.size());
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable("id") Long id,
                                                   @RequestBody PostRequest postRequest) {
        logger.info("Request received to update post with ID: {}", id);
        PostResponse updatedPost = postService.updatePost(id, postRequest);
        if (updatedPost != null) {
            logger.debug("Post updated: {}", updatedPost);
            return ResponseEntity.ok(updatedPost);
        } else {
            logger.warn("Post with ID {} not found for update", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PostResponse>> getFilteredPosts(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String keyword) {
        logger.info("Request received to filter posts with parameters: startDate={}, endDate={}, author={}, keyword={}",
                startDate, endDate, author, keyword);
        List<PostResponse> posts = postService.getFilteredPosts(startDate, endDate, author, keyword);
        logger.debug("Retrieved {} filtered posts", posts.size());
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping
    public void deleteAllPosts() {
        logger.warn("Request received to delete all posts");
        postService.deletePosts();
        logger.info("All posts deleted");
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Void> addUserComment(
            @PathVariable Long postId,
            @RequestBody UserCommentRequest commentRequest) {
        logger.info("Request received to add comment to post with ID: {}", postId);
        postService.addUserComment(postId, commentRequest);
        logger.info("Comment added to post with ID: {}", postId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<UserCommentResponse>> getUserComments(@PathVariable Long postId) {
        logger.info("Request received to retrieve comments for post with ID: {}", postId);
        List<UserCommentResponse> comments = postService.getUserComments(postId);
        logger.debug("Retrieved {} comments for post ID: {}", comments.size(), postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteUserComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        logger.info("Request received to delete comment with ID: {} from post ID: {}", commentId, postId);
        String result = postService.deleteUserComment(postId, commentId);
        logger.info("Delete comment result: {}", result);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<UserCommentResponse> updateUserComment(
            @PathVariable Long commentId,
            @RequestBody UserCommentRequest request) {
        logger.info("Request received to update comment with ID: {}", commentId);
        UserCommentResponse updatedComment = postService.updateUserComment(commentId, request);
        logger.debug("Comment updated: {}", updatedComment);
        return ResponseEntity.ok(updatedComment);
    }
}
