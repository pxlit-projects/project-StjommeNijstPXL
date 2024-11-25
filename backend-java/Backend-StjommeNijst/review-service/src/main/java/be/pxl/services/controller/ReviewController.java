package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostReviewResponse;
import be.pxl.services.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Haal alle posts in review op
    @GetMapping("/pending")
    public ResponseEntity<List<PostReviewResponse>> getAllPendingPosts() {
        return ResponseEntity.ok(reviewService.getAllPendingPosts());
    }

    // Keur een post goed
    @PostMapping("/{postId}/approve")
    public ResponseEntity<String> approvePost(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(reviewService.approvePost(postId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Wijs een post af
    @PostMapping("/{postId}/reject")
    public ResponseEntity<String> rejectPost(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(reviewService.rejectPost(postId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

