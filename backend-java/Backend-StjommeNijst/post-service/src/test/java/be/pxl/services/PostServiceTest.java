package be.pxl.services;

import be.pxl.services.domain.dto.*;
import be.pxl.services.domain.Post;
import be.pxl.services.domain.Status;
import be.pxl.services.repository.IPostRepository;
import be.pxl.services.client.CommentClient;
import be.pxl.services.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private IPostRepository postRepository;

    @Mock
    private CommentClient commentClient;

    private PostRequest postRequest;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Prepare test data using Builder pattern
        postRequest = PostRequest.builder()
                .author("Author")
                .content("Content")
                .title("Title")
                .createdAt("2024-12-07 10:00:00")
                .status(Status.GOEDGEKEURD)
                .build();

        post = Post.builder()
                .id(1L)
                .author("Author")
                .content("Content")
                .title("Title")
                .createdAt(LocalDateTime.now())
                .status(Status.GOEDGEKEURD)
                .build();
    }

    @Test
    void testCreatePost_Approved() {
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse response = postService.createPost(postRequest);

        assertNotNull(response);
        assertEquals(postRequest.getAuthor(), response.getAuthor());
        assertEquals(postRequest.getTitle(), response.getTitle());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(rabbitTemplate, times(0)).convertAndSend(anyString(), Optional.ofNullable(any()));  // Status is approved, so no RabbitMQ call
    }

    @Test
    void testCreatePost_WaitingStatus() {
        // Change status to WACHTEND for testing RabbitMQ sending logic
        PostRequest waitingRequest = PostRequest.builder()
                .author("Author")
                .content("Content")
                .title("Title")
                .createdAt("2024-12-07 10:00:00")
                .status(Status.WACHTEND)
                .build();

        Post postWaiting = Post.builder()
                .id(1L)
                .author("Author")
                .content("Content")
                .title("Title")
                .createdAt(LocalDateTime.now())
                .status(Status.WACHTEND)
                .build();

        when(postRepository.save(any(Post.class))).thenReturn(postWaiting);

        PostResponse response = postService.createPost(waitingRequest);

        assertNotNull(response);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("review-queue"), any(PostReviewMessage.class));  // RabbitMQ should be called
    }

    @Test
    void testGetAllPosts() {
        List<Post> posts = List.of(post);
        when(postRepository.findAll()).thenReturn(posts);

        List<PostResponse> response = postService.getAllPosts();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(post.getTitle(), response.get(0).getTitle());
    }

    @Test
    void testUpdatePost_Exists() {
        PostRequest updatedRequest = PostRequest.builder()
                .author("Updated Author")
                .content("Updated Content")
                .title("Updated Title")
                .createdAt("2024-12-07 12:00:00")
                .status(Status.GOEDGEKEURD)
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponse updatedPost = postService.updatePost(post.getId(), updatedRequest);

        assertNotNull(updatedPost);
        assertEquals(updatedRequest.getAuthor(), updatedPost.getAuthor());
        assertEquals(updatedRequest.getTitle(), updatedPost.getTitle());
    }

    @Test
    void testUpdatePost_NotFound() {
        PostRequest updatedRequest = PostRequest.builder()
                .author("Updated Author")
                .content("Updated Content")
                .title("Updated Title")
                .createdAt("2024-12-07 12:00:00")
                .status(Status.GOEDGEKEURD)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        PostResponse updatedPost = postService.updatePost(999L, updatedRequest);

        assertNull(updatedPost);
    }

    @Test
    void testGetPostById_NotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        PostResponse response = postService.getPostById(999L);

        assertNull(response);
    }

    @Test
    void testGetFilteredPosts() {
        String startDate = "2024-12-01 00:00:00";
        String endDate = "2024-12-31 23:59:59";
        String author = "Author";
        String keyword = "Content";

        when(postRepository.findFilteredPosts(any(), any(), any(), any(), any())).thenReturn(List.of(post));

        List<PostResponse> response = postService.getFilteredPosts(startDate, endDate, author, keyword);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(post.getTitle(), response.get(0).getTitle());
    }

    @Test
    void testDeletePosts() {
        postService.deletePosts();
        verify(postRepository, times(1)).deleteAll();
    }

    @Test
    void testAddUserComment() {
        UserCommentRequest commentRequest = UserCommentRequest.builder()
                .content("Great post!")
                .build();

        doNothing().when(commentClient).addCommentToPost(anyLong(), any(UserCommentRequest.class));

        postService.addUserComment(post.getId(), commentRequest);

        verify(commentClient, times(1)).addCommentToPost(post.getId(), commentRequest);
    }

    @Test
    void testGetUserComments() {
        UserCommentResponse commentResponse = UserCommentResponse.builder()
                .id(1L)
                .content("Great post!")
                .build();

        when(commentClient.getCommentsByPost(anyLong())).thenReturn(List.of(commentResponse));

        List<UserCommentResponse> comments = postService.getUserComments(post.getId());

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("Great post!", comments.get(0).getContent());
    }

    @Test
    void testUpdateUserComment() {
        UserCommentRequest commentRequest = UserCommentRequest.builder()
                .content("Updated comment!")
                .build();

        UserCommentResponse commentResponse = UserCommentResponse.builder()
                .id(1L)
                .content("Updated comment!")
                .build();

        when(commentClient.updateComment(anyLong(), any(UserCommentRequest.class))).thenReturn(commentResponse);

        UserCommentResponse updatedComment = postService.updateUserComment(1L, commentRequest);

        assertNotNull(updatedComment);
        assertEquals("Updated comment!", updatedComment.getContent());
    }

    @Test
    void testDeleteUserComment() {
        // Arrange
        String expectedResponse = "Comment deleted";

        // Maak een mutabele lijst van comment-IDs
        List<Long> commentIds = new ArrayList<>(List.of(1L, 2L));

        post = Post.builder()
                .id(1L)
                .title("Sample Post")
                .author("Author")
                .content("Content")
                .userCommentIds(commentIds) // Gebruik de mutabele lijst
                .build();

        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        when(commentClient.deleteComment(1L)).thenReturn(expectedResponse);

        // Act
        String response = postService.deleteUserComment(post.getId(), 1L);

        // Assert
        assertEquals(expectedResponse, response);
        verify(commentClient, times(1)).deleteComment(1L);
        verify(postRepository, times(1)).save(post);
        assertFalse(post.getUserCommentIds().contains(1L)); // Controleer of de comment-ID is verwijderd
    }

    @Test
    void testUpdatePost_StatusWaiting() {
        // Gegeven: een PostRequest met de status 'WACHTEND'
        PostRequest waitingRequest = PostRequest.builder()
                .author("Author")
                .content("Updated content")
                .title("Updated title")
                .createdAt("2024-12-07 10:00:00")
                .status(Status.WACHTEND)
                .build();

        // Simuleer de Post die we willen updaten
        Post existingPost = Post.builder()
                .id(1L)
                .author("Old Author")
                .content("Old content")
                .title("Old title")
                .createdAt(LocalDateTime.now())
                .status(Status.GOEDGEKEURD)
                .build();

        // Wanneer we de post ophalen uit de repository, moet het de bestaande post zijn
        when(postRepository.findById(existingPost.getId())).thenReturn(Optional.of(existingPost));
        // Simuleer de opslaan van de post na de update
        when(postRepository.save(any(Post.class))).thenReturn(existingPost);

        // Actie: update de post met de status 'WACHTEND'
        PostResponse response = postService.updatePost(existingPost.getId(), waitingRequest);

        // Asserties
        assertNotNull(response);
        // Verifieer dat RabbitTemplate de juiste gegevens verstuurt naar de 'review-queue'
        verify(rabbitTemplate, times(1)).convertAndSend(eq("review-queue"), any(PostReviewMessage.class));

        // Controleer of de post is bijgewerkt
        assertEquals(waitingRequest.getTitle(), existingPost.getTitle());
        assertEquals(waitingRequest.getContent(), existingPost.getContent());
        assertEquals(waitingRequest.getStatus(), existingPost.getStatus());
    }


    @Test
    void testGetConceptPosts() {
        // Given
        List<Post> conceptPosts = List.of(
                Post.builder().id(1L).title("Concept Post 1").createdAt(LocalDateTime.now()).status(Status.CONCEPT).build(),
                Post.builder().id(2L).title("Concept Post 2").createdAt(LocalDateTime.now()).status(Status.CONCEPT).build()
        );

        // Mock repository to return the concept posts
        when(postRepository.findAllByStatus(Status.CONCEPT)).thenReturn(conceptPosts);

        // Act
        List<PostResponse> response = postService.getConceptPosts();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Concept Post 1", response.get(0).getTitle());
        assertEquals("Concept Post 2", response.get(1).getTitle());
        verify(postRepository, times(1)).findAllByStatus(Status.CONCEPT);
    }

    @Test
    void testGetDeclinedPosts() {
        // Given
        List<Post> declinedPosts = List.of(
                Post.builder().id(1L).title("Declined Post 1").createdAt(LocalDateTime.now()).status(Status.NIET_GOEDGEKEURD).build(),
                Post.builder().id(2L).title("Declined Post 2").createdAt(LocalDateTime.now()).status(Status.NIET_GOEDGEKEURD).build()
        );

        // Mock repository to return the declined posts
        when(postRepository.findAllByStatus(Status.NIET_GOEDGEKEURD)).thenReturn(declinedPosts);

        // Act
        List<PostResponseWithComment> response = postService.getDeclinedPosts();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Declined Post 1", response.get(0).getTitle());
        assertEquals("Declined Post 2", response.get(1).getTitle());
        verify(postRepository, times(1)).findAllByStatus(Status.NIET_GOEDGEKEURD);
    }

    @Test
    void testGetNotApprovedPosts() {
        // Given
        List<Post> notApprovedPosts = List.of(
                Post.builder().id(1L).title("Not Approved Post 1").status(Status.WACHTEND).createdAt(LocalDateTime.now()).build(),
                Post.builder().id(2L).title("Not Approved Post 2").status(Status.WACHTEND).createdAt(LocalDateTime.now()).build()
        );

        // Mock repository to return the not approved posts
        when(postRepository.findAllByStatus(Status.WACHTEND)).thenReturn(notApprovedPosts);

        // Act
        List<PostResponse> response = postService.getNotApprovedPosts();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("Not Approved Post 1", response.get(0).getTitle());
        assertEquals("Not Approved Post 2", response.get(1).getTitle());
        verify(postRepository, times(1)).findAllByStatus(Status.WACHTEND);
    }

    @Test
    void testHandleStatusUpdate() {
        // Gegeven
        PostReviewMessageWithComment message = new PostReviewMessageWithComment(
                1L,                        // postId
                "Post Title",              // title
                "Content of the post",     // content
                "Author Name",             // author
                "2024-12-07 10:00:00",     // createdAt
                "GOEDGEKEURD",                // status
                "Good post!"               // comment
        );

        Post postToUpdate = Post.builder().id(1L).status(Status.WACHTEND).build();

        // Mock repository om een post te vinden
        when(postRepository.findById(1L)).thenReturn(Optional.of(postToUpdate));

        // Actie
        postService.handleStatusUpdate(message);

        // Asserties
        assertNotNull(postToUpdate);
        assertEquals(Status.valueOf(message.getStatus()), postToUpdate.getStatus());
        assertEquals(message.getComment(), postToUpdate.getComment());
        verify(postRepository, times(1)).save(postToUpdate); // Verifieer dat de post wordt opgeslagen na de update
    }



}
