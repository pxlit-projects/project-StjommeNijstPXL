package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostReviewResponse;
import be.pxl.services.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, Object>> approvePost(@PathVariable Long postId) {
        try {
            return ResponseEntity.ok(reviewService.approvePost(postId).getBody());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Wijs een post af
    @PostMapping("/{postId}/reject")
    public ResponseEntity<Map<String, Object>> rejectPost(
            @PathVariable Long postId,
            @RequestBody Map<String, String> payload) {
        try {
            String commentary = payload.get("comment");
            ResponseEntity<Map<String, Object>> serviceResponse = reviewService.rejectPost(postId, commentary);

            // Voeg message en commentary toe aan de response
            return ResponseEntity.ok(serviceResponse.getBody());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping
    public void deleteReviews(){
        reviewService.deleteAllReviews();
    }

}

