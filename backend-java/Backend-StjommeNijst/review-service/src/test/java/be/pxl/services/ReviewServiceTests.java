package be.pxl.services;

import be.pxl.services.domain.PostReview;
import be.pxl.services.domain.ReviewStatus;
import be.pxl.services.domain.dto.PostReviewMessage;
import be.pxl.services.domain.dto.PostReviewResponse;
import be.pxl.services.repository.IPostReviewRepository;
import be.pxl.services.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTests {

    @Mock
    private IPostReviewRepository reviewRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reviewService = new ReviewService(reviewRepository, rabbitTemplate);
    }

    // Test for handleIncomingPost
    @Test
    void testHandleIncomingPost() {
        PostReviewMessage message = new PostReviewMessage(1L, "Title", "Content", "Author", "2024-12-07 14:00:00", "WACHTEND", null);

        reviewService.handleIncomingPost(message);

        // Verify that the repository save method is called
        verify(reviewRepository, times(1)).save(any(PostReview.class));
    }

    // Test for fromStringToLocalDateTime
    @Test
    void testFromStringToLocalDateTime() {
        String date = "2024-12-07 14:00:00";
        LocalDateTime localDateTime = reviewService.fromStringToLocalDateTime(date);

        assertNotNull(localDateTime);
        assertEquals(LocalDateTime.of(2024, 12, 7, 14, 0, 0, 0), localDateTime);
    }

    // Test for fromLocalDateTimeToString
    @Test
    void testFromLocalDateTimeToString() {
        LocalDateTime localDateTime = LocalDateTime.of(2024, 12, 7, 14, 0, 0, 0);
        String dateString = reviewService.fromLocalDateTimeToString(localDateTime);

        assertEquals("2024-12-07 14:00:00", dateString);
    }

    // Test for getAllPendingPosts
    @Test
    void testGetAllPendingPosts() {
        PostReview postReview = new PostReview(1L, 123L, "Title", "Content", "Author", LocalDateTime.now(), ReviewStatus.WACHTEND);
        when(reviewRepository.findAllByStatus(ReviewStatus.WACHTEND)).thenReturn(List.of(postReview));

        List<PostReviewResponse> responses = reviewService.getAllPendingPosts();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Title", responses.get(0).getTitle());
    }

    // Test for approvePost
    @Test
    void testApprovePost() {
        when(reviewRepository.findById(1L)).thenReturn(java.util.Optional.of(new PostReview(1L, 123L, "Title", "Content", "Author", LocalDateTime.now(), ReviewStatus.WACHTEND)));

        ResponseEntity<Map<String, Object>> response = reviewService.approvePost(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("message"));
    }

    // Test for rejectPost
    @Test
    void testRejectPost() {
        String commentary = "Not good";
        when(reviewRepository.findById(1L)).thenReturn(java.util.Optional.of(new PostReview(1L, 123L, "Title", "Content", "Author", LocalDateTime.now(), ReviewStatus.WACHTEND)));

        ResponseEntity<Map<String, Object>> response = reviewService.rejectPost(1L, commentary);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().containsKey("message"));
        assertTrue(response.getBody().containsKey("commentary"));
    }

    @Test
    void testUpdatePostStatus() {
        // Given: Create a post review with ID 1
        PostReview post = new PostReview(1L, 123L, "Title", "Content", "Author", LocalDateTime.now(), ReviewStatus.WACHTEND);

        // Mock the repository to return the post when findById is called
        when(reviewRepository.findById(1L)).thenReturn(java.util.Optional.of(post));

        // Act: Call the updatePostStatus method
        String message = reviewService.updatePostStatus(1L, ReviewStatus.GOEDGEKEURD, "Approved");

        // Assert: Verify that the result message is as expected
        assertEquals("Post is goedgekeurd!", message);

        // Verify that convertAndSend was called with the expected arguments
        verify(rabbitTemplate, times(1)).convertAndSend(eq("update-queue"), any(PostReviewMessage.class));
    }


    // Test for sendEmailNotification
    @Test
    void testSendEmailNotification() {
        // Given: Create a post review with ID 1
        PostReview post = new PostReview(1L, 123L, "Title", "Content", "Author", LocalDateTime.now(), ReviewStatus.GOEDGEKEURD);

        // Mock the repository to return the post when findById is called
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(post));

        // Since sendEmailNotification is private, we can't test it directly. But you can check if it was called indirectly.
        doNothing().when(rabbitTemplate).convertAndSend(eq("update-queue"), any(PostReviewMessage.class));

        // Act: Call the updatePostStatus method which should trigger the email and message sending
        String result = reviewService.updatePostStatus(1L, ReviewStatus.GOEDGEKEURD, "Approved");

        // Verify: Check that convertAndSend was called with the correct arguments
        verify(rabbitTemplate, times(1)).convertAndSend(eq("update-queue"), any(PostReviewMessage.class));

        // Optionally, you can also verify the result message
        assertEquals("Post is goedgekeurd!", result);
    }


}
