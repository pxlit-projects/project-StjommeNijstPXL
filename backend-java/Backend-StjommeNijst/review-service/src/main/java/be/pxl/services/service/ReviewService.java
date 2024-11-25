package be.pxl.services.service;

import be.pxl.services.domain.PostReview;
import be.pxl.services.domain.ReviewStatus;
import be.pxl.services.domain.dto.PostReviewMessage;
import be.pxl.services.domain.dto.PostReviewResponse;
import be.pxl.services.repository.IPostReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
                .id(message.getPostId())
                .title(message.getTitle())
                .content(message.getContent())
                .author(message.getAuthor())
                .status(ReviewStatus.valueOf(message.getStatus()))
                .createdAt(fromStringToLocalDateTime(message.getCreatedAt()))
                .build();
    }

    private PostReviewMessage fromPostReviewToPostReviewMessage(PostReview postReview){
         return PostReviewMessage.builder()
                 .postId(postReview.getPostId())
                 .title(postReview.getTitle())
                 .status(postReview.getStatus().name())
                 .createdAt(fromLocalDateTimeToString(postReview.getCreatedAt()))
                 .content(postReview.getContent())
                 .author(postReview.getAuthor())
                 .build();
    }

    private PostReviewResponse toPostReviewResponse(PostReview postReview){
        return PostReviewResponse.builder()
                .id(postReview.getPostId())
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

    // Keur een post goed
    public String approvePost(Long postId) {
        return updatePostStatus(postId, ReviewStatus.GOEDGEKEURD);
    }

    // Wijs een post af
    public String rejectPost(Long postId) {
        return updatePostStatus(postId, ReviewStatus.NIET_GOEDGEKEURD);
    }

    // Algemene methode om de status van een post te wijzigen
    private String updatePostStatus(Long postId, ReviewStatus newStatus) {
        PostReview post = reviewRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post met ID " + postId + " niet gevonden."));
        post.setStatus(newStatus);
        reviewRepository.save(post);

        PostReviewMessage message = fromPostReviewToPostReviewMessage(post);

        rabbitTemplate.convertAndSend("update-queue", message); // Stuur terug naar de Post-service
        return "Post is " + newStatus.toString().toLowerCase() + "!";
    }
}
