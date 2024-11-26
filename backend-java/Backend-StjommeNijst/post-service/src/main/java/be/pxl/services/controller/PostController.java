package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostResponseWithComment;
import be.pxl.services.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest postRequest, HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if ("Redacteur".equalsIgnoreCase(role)) {
            PostResponse createdPost = postService.createPost(postRequest);
            return ResponseEntity.ok(createdPost);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }


    @GetMapping("/{id}")
    ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/concepts")
    public ResponseEntity<List<PostResponse>> getConceptPosts() {
        List<PostResponse> posts = postService.getConceptPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/declined")
    public ResponseEntity<List<PostResponseWithComment>> getDeclinedPosts() {
        List<PostResponseWithComment> posts = postService.getDeclinedPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/notapproved")
    public ResponseEntity<List<PostResponse>> getNotApprovedPosts() {
        List<PostResponse> posts = postService.getNotApprovedPosts();
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable("id") Long id,
                                                   @RequestBody PostRequest postRequest,
                                                   HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");
        if ("Redacteur".equalsIgnoreCase(role)) {
            PostResponse updatedPost = postService.updatePost(id, postRequest);
            if (updatedPost != null) {
                return ResponseEntity.ok(updatedPost);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @GetMapping("/filter")
    public ResponseEntity<List<PostResponse>> getFilteredPosts(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String keyword) {

        List<PostResponse> posts = postService.getFilteredPosts(startDate, endDate, author, keyword);
        return ResponseEntity.ok(posts);
    }
}
