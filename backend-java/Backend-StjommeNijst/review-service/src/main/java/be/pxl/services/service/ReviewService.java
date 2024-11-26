package be.pxl.services.service;

import be.pxl.services.domain.PostReview;
import be.pxl.services.domain.ReviewStatus;
import be.pxl.services.domain.dto.PostReviewMessage;
import be.pxl.services.domain.dto.PostReviewResponse;
import be.pxl.services.repository.IPostReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final IPostReviewRepository reviewRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "review-queue")
    public void handleIncomingPost(PostReviewMessage message) {
        reviewRepository.save(fromPostReviewMessageToPostReview(message)); // Opslaan in de database
    }

    public LocalDateTime fromStringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    public String fromLocalDateTimeToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    private PostReview fromPostReviewMessageToPostReview(PostReviewMessage message){
        return PostReview.builder()
                .postId(message.getPostId())
                .title(message.getTitle())
                .content(message.getContent())
                .author(message.getAuthor())
                .status(ReviewStatus.valueOf(message.getStatus()))
                .createdAt(fromStringToLocalDateTime(message.getCreatedAt()))
                .build();
    }

    private PostReviewMessage fromPostReviewToPostReviewMessage(PostReview postReview, String commentary){
         return PostReviewMessage.builder()
                 .postId(postReview.getPostId())
                 .title(postReview.getTitle())
                 .status(postReview.getStatus().name())
                 .createdAt(fromLocalDateTimeToString(postReview.getCreatedAt()))
                 .content(postReview.getContent())
                 .author(postReview.getAuthor())
                 .comment(commentary)
                 .build();
    }

    private PostReviewResponse toPostReviewResponse(PostReview postReview){
        return PostReviewResponse.builder()
                .id(postReview.getId())
                .postid(postReview.getPostId())
                .title(postReview.getTitle())
                .status(postReview.getStatus())
                .createdAt(fromLocalDateTimeToString(postReview.getCreatedAt()))
                .content(postReview.getContent())
                .author(postReview.getAuthor())
                .build();
    }


    // Haal alle posts op die in review staan
    public List<PostReviewResponse> getAllPendingPosts() {
        return reviewRepository.findAllByStatus(ReviewStatus.WACHTEND).stream()
                .map(this::toPostReviewResponse) // Gebruik de converter methode
                .collect(Collectors.toList());
    }

    public ResponseEntity<Map<String, Object>> approvePost(Long postId) {
        String message = updatePostStatus(postId, ReviewStatus.GOEDGEKEURD, "is goedgekeurd");
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> rejectPost(Long postId, String commentary) {
        String message = updatePostStatus(postId, ReviewStatus.NIET_GOEDGEKEURD, "is afgewezen: " + commentary);
        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        response.put("commentary", commentary);
        return ResponseEntity.ok(response);
    }


    // Algemene methode om de status van een post te wijzigen
    private String updatePostStatus(Long postId, ReviewStatus newStatus, String commentary) {
        PostReview post = reviewRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post met ID " + postId + " niet gevonden."));
        post.setStatus(newStatus);
        reviewRepository.save(post);

        PostReviewMessage message = fromPostReviewToPostReviewMessage(post, commentary);

        rabbitTemplate.convertAndSend("update-queue", message); // Stuur terug naar de Post-service
        return "Post is " + newStatus.toString().toLowerCase() + "!";
    }
}
