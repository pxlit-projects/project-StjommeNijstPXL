package be.pxl.services.controller;

import be.pxl.services.domain.Dto.PostRequest;
import be.pxl.services.domain.Post;
import be.pxl.services.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody PostRequest postRequest, HttpServletRequest request) {
        String role = request.getHeader("X-User-Role");

        if ("redacteur".equalsIgnoreCase(role)) {
            Post createdPost = postService.createPost(postRequest);
            return ResponseEntity.ok("Post created with ID: " + createdPost);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User does not have permission to create posts.");
        }
    }

}
