package be.pxl.services;

import be.pxl.services.client.CommentClient;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Status;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostReviewMessage;
import be.pxl.services.domain.dto.PostReviewMessageWithComment;
import be.pxl.services.repository.IPostRepository;
import be.pxl.services.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@SpringJUnitConfig
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private IPostRepository postRepository;

    @Mock
    private CommentClient commentClient;

    @Mock
    private PostReviewMessageWithComment message;

    private PostRequest postRequest;

    @BeforeEach
    public void setUp() {
        postRequest = PostRequest.builder()
                .author("John Doe")
                .content("Post content")
                .title("Post title")
                .createdAt("2024-12-05 12:00:00")
                .status(Status.WACHTEND) // Ensure post is WACHTEND
                .build();
    }

    @Test
    public void testCreatePostSendsMessageToRabbitMQ() {
        Post savedPost = Post.builder()
                .id(1L)
                .author("John Doe")
                .content("Post content")
                .title("Post title")
                .createdAt(LocalDateTime.now())
                .status(Status.WACHTEND)
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        postService.createPost(postRequest);

        ArgumentCaptor<PostReviewMessage> messageCaptor = ArgumentCaptor.forClass(PostReviewMessage.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("review-queue"), messageCaptor.capture());

        PostReviewMessage sentMessage = messageCaptor.getValue();
        assertEquals(1L, sentMessage.getPostId());
        assertEquals("Post title", sentMessage.getTitle());
        assertEquals("Post content", sentMessage.getContent());
        assertEquals("John Doe", sentMessage.getAuthor());
        assertEquals(Status.WACHTEND.name(), sentMessage.getStatus());
    }

    @Test
    public void testUpdatePostSendsMessageToRabbitMQ() {
        Post existingPost = Post.builder()
                .id(1L)
                .author("John Doe")
                .content("Updated content")
                .title("Updated title")
                .createdAt(LocalDateTime.now())
                .status(Status.WACHTEND)
                .build();

        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenReturn(existingPost);

        PostRequest updateRequest = PostRequest.builder()
                .author("John Doe")
                .content("Updated content")
                .title("Updated title")
                .createdAt("2024-12-05 12:00:00")
                .status(Status.WACHTEND)
                .build();

        postService.updatePost(1L, updateRequest);

        ArgumentCaptor<PostReviewMessage> messageCaptor = ArgumentCaptor.forClass(PostReviewMessage.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("review-queue"), messageCaptor.capture());

        PostReviewMessage sentMessage = messageCaptor.getValue();
        assertEquals(1L, sentMessage.getPostId());
        assertEquals("Updated title", sentMessage.getTitle());
        assertEquals("Updated content", sentMessage.getContent());
        assertEquals("John Doe", sentMessage.getAuthor());
        assertEquals(Status.WACHTEND.name(), sentMessage.getStatus());
    }

    @Test
    public void testHandleStatusUpdateShouldUpdatePostStatus() {
        // Arrange: Create a Post object
        Post existingPost = Post.builder()
                .id(1L)
                .author("John Doe")
                .content("Post content")
                .title("Post title")
                .createdAt(LocalDateTime.now())
                .status(Status.WACHTEND)
                .build();

        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(existingPost));

        when(message.getPostId()).thenReturn(1L);
        when(message.getStatus()).thenReturn("GOEDGEKEURD");  // New status
        when(message.getComment()).thenReturn("Approved after review");

        postService.handleStatusUpdate(message);

        assertEquals(Status.GOEDGEKEURD, existingPost.getStatus());
        assertEquals("Approved after review", existingPost.getComment());

        verify(postRepository, times(1)).save(existingPost);
    }
}
