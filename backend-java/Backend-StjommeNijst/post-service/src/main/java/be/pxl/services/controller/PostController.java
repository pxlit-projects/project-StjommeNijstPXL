package be.pxl.services.controller;

import be.pxl.services.domain.Dto.PostRequest;
import be.pxl.services.domain.Dto.PostResponse;
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Post niet gevonden
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();  // Niet toegestaan voor niet-redacteuren
        }
    }
}
