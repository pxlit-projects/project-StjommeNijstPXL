package be.pxl.services.controller;

import be.pxl.services.domain.dto.PostReviewResponse;
import be.pxl.services.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    // Haal alle posts in review op
    @GetMapping("/pending")
    public ResponseEntity<List<PostReviewResponse>> getAllPendingPosts() {
        logger.info("Request received to fetch all pending posts for review");
        List<PostReviewResponse> responses = reviewService.getAllPendingPosts();
        logger.info("Fetched {} pending posts", responses.size());
        return ResponseEntity.ok(responses);
    }

    // Keur een post goed
    @PostMapping("/{postId}/approve")
    public ResponseEntity<Map<String, Object>> approvePost(@PathVariable Long postId) {
        logger.info("Request received to approve post with ID: {}", postId);
        try {
            ResponseEntity<Map<String, Object>> response = reviewService.approvePost(postId);
            logger.info("Post with ID {} approved successfully", postId);
            return ResponseEntity.ok(response.getBody());
        } catch (IllegalArgumentException e) {
            logger.error("Post with ID {} not found or cannot be approved", postId, e);
            return ResponseEntity.notFound().build();
        }
    }

    // Wijs een post af
    @PostMapping("/{postId}/reject")
    public ResponseEntity<Map<String, Object>> rejectPost(
            @PathVariable Long postId,
            @RequestBody Map<String, String> payload) {
        logger.info("Request received to reject post with ID: {}", postId);
        String commentary = payload.get("comment");
        logger.debug("Commentary provided for rejection: {}", commentary);
        try {
            ResponseEntity<Map<String, Object>> serviceResponse = reviewService.rejectPost(postId, commentary);
            logger.info("Post with ID {} rejected successfully", postId);
            return ResponseEntity.ok(serviceResponse.getBody());
        } catch (IllegalArgumentException e) {
            logger.error("Post with ID {} not found or cannot be rejected", postId, e);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public void deleteReviews() {
        logger.warn("Request received to delete all reviews");
        reviewService.deleteAllReviews();
        logger.info("All reviews have been deleted");
    }

}
