package be.pxl.services;

import be.pxl.services.controller.PostController;
import be.pxl.services.domain.Status;
import be.pxl.services.domain.dto.*;
import be.pxl.services.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
        objectMapper = new ObjectMapper();
    }

    private void performGetWithRole(String url, String role) throws Exception {
        mockMvc.perform(get(url)
                        .header("X-User-Role", role))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPosts() throws Exception {
        // Arrange: maak een lijst van posts aan
        PostResponse post1 = PostResponse.builder()
                .id(1L)
                .author("Author 1")
                .title("Title 1")
                .content("Content 1")
                .createdAt("2023-12-01 12:00:00")
                .status(Status.GOEDGEKEURD)
                .build();

        List<PostResponse> postList = Arrays.asList(post1);

        // Mock de service om een lijst van posts te retourneren
        when(postService.getAllPosts()).thenReturn(postList);

        // Act: voer de GET-aanroep uit naar het endpoint /api/posts
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].author").value("Author 1"));

        // Assert: controleer of de service is aangeroepen
        verify(postService, times(1)).getAllPosts();
    }

    @Test
    void testGetPostById() throws Exception {
        // Arrange: maak een post aan
        PostResponse post = PostResponse.builder()
                .id(1L)
                .author("Author 1")
                .title("Title 1")
                .content("Content 1")
                .createdAt("2023-12-01 12:00:00")
                .status(Status.GOEDGEKEURD)
                .build();

        // Mock de service om de post met ID 1 te retourneren
        when(postService.getPostById(1L)).thenReturn(post);

        // Act: voer de GET-aanroep uit naar het endpoint /api/posts/1
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author").value("Author 1"));

        // Assert: controleer of de service is aangeroepen met ID 1
        verify(postService, times(1)).getPostById(1L);
    }

    @Test
    void testGetPostByIdNotFound() throws Exception {
        // Mock de service om null te retourneren voor een niet-bestaand post-ID
        when(postService.getPostById(1L)).thenReturn(null);

        // Act: voer de GET-aanroep uit naar het endpoint /api/posts/1
        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isNotFound());

        // Assert: controleer of de service is aangeroepen met ID 1
        verify(postService, times(1)).getPostById(1L);
    }

    @Test
    void testGetConceptPosts() throws Exception {
        // Arrange: maak een lijst van concept posts aan
        PostResponse post1 = PostResponse.builder()
                .id(1L)
                .author("Author 1")
                .title("Concept Post 1")
                .content("Content 1")
                .createdAt("2023-12-01 12:00:00")
                .status(Status.CONCEPT)
                .build();

        List<PostResponse> postList = Arrays.asList(post1);

        // Mock de service om concept posts te retourneren
        when(postService.getConceptPosts()).thenReturn(postList);

        // Act: voer de GET-aanroep uit naar het endpoint /api/posts/concepts met de juiste role header
        mockMvc.perform(get("/api/posts/concepts")
                        .header("X-User-Role", "redacteur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Concept Post 1"));

        // Assert: controleer of de service is aangeroepen
        verify(postService, times(1)).getConceptPosts();
    }

    @Test
    void testGetConceptPostsUnauthorized() throws Exception {
        // Act: voer de GET-aanroep uit naar het endpoint /api/posts/concepts zonder de juiste role header
        mockMvc.perform(get("/api/posts/concepts"))
                .andExpect(status().isUnauthorized());

        // Assert: controleer of de service niet is aangeroepen
        verify(postService, times(0)).getConceptPosts();
    }


    @Test
    void testGetNotApprovedPosts() throws Exception {
        // Given: a list of PostResponse objects for not approved posts
        PostResponse post1 = PostResponse.builder()
                .id(1L)
                .author("Author 1")
                .title("Not Approved Post 1")
                .content("Content 1")
                .createdAt("2023-12-01 12:00:00")
                .status(Status.WACHTEND)
                .build();

        PostResponse post2 = PostResponse.builder()
                .id(2L)
                .author("Author 2")
                .title("Not Approved Post 2")
                .content("Content 2")
                .createdAt("2023-12-02 12:00:00")
                .status(Status.WACHTEND)
                .build();

        // Mock the service layer to return these not approved posts
        when(postService.getNotApprovedPosts()).thenReturn(List.of(post1, post2));

        // Perform the GET request to the /notapproved endpoint
        mockMvc.perform(get("/api/posts/notapproved")
                        .header("X-User-Role", "redacteur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Not Approved Post 1"))
                .andExpect(jsonPath("$[1].title").value("Not Approved Post 2"));

        // Verify the service method was called
        verify(postService, times(1)).getNotApprovedPosts();
    }

    @Test
    void testGetDeclinedPosts() throws Exception {
        // Given: a list of PostResponseWithComment objects for declined posts
        PostResponseWithComment post1 = PostResponseWithComment.builder()
                .id(1L)
                .author("Author 1")
                .title("Declined Post 1")
                .content("Content 1")
                .createdAt("2023-12-01 12:00:00")
                .status(Status.NIET_GOEDGEKEURD)
                .comment("Declined reason 1")
                .build();

        PostResponseWithComment post2 = PostResponseWithComment.builder()
                .id(2L)
                .author("Author 2")
                .title("Declined Post 2")
                .content("Content 2")
                .createdAt("2023-12-02 12:00:00")
                .status(Status.NIET_GOEDGEKEURD)
                .comment("Declined reason 2")
                .build();

        // Mock the service layer to return these declined posts
        when(postService.getDeclinedPosts()).thenReturn(List.of(post1, post2));

        // Perform the GET request to the /declined endpoint
        mockMvc.perform(get("/api/posts/declined")
                        .header("X-User-Role", "redacteur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Declined Post 1"))
                .andExpect(jsonPath("$[0].comment").value("Declined reason 1"))
                .andExpect(jsonPath("$[1].title").value("Declined Post 2"))
                .andExpect(jsonPath("$[1].comment").value("Declined reason 2"));

        // Verify the service method was called
        verify(postService, times(1)).getDeclinedPosts();
    }

    @Test
    void testGetDeclinedPostsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/posts/declined")
                        .header("X-User-Role", "non-redacteur"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreatePost() throws Exception {
        PostRequest request = new PostRequest("Author", "Title", "Content", "2023-12-01 12:00:00", Status.CONCEPT);
        PostResponse response = PostResponse.builder()
                .id(1L)
                .author("Author")
                .title("Title")
                .content("Content")
                .createdAt("2023-12-01 12:00:00")
                .status(Status.CONCEPT)
                .build();

        when(postService.createPost(any(PostRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Role", "redacteur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.status").value("CONCEPT"));

        verify(postService, times(1)).createPost(any(PostRequest.class));
    }

    @Test
    void testCreatePostUnauthorized() throws Exception {
        PostRequest request = new PostRequest("Author", "Title", "Content", "2023-12-01 12:00:00", Status.CONCEPT);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Role", "non-redacteur"))
                .andExpect(status().isUnauthorized());

        verify(postService, times(0)).createPost(any(PostRequest.class));
    }


    @Test
    void testUpdatePost() throws Exception {
        PostRequest request = new PostRequest("Author", "Updated Title", "Updated Content", "2023-12-01 12:00:00", Status.CONCEPT);
        PostResponse response = PostResponse.builder()
                .id(1L)
                .author("Author")
                .title("Updated Title")
                .content("Updated Content")
                .createdAt("2023-12-01 12:00:00")
                .status(Status.CONCEPT)
                .build();

        when(postService.updatePost(eq(1L), any(PostRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-User-Role", "redacteur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"));

        verify(postService, times(1)).updatePost(eq(1L), any(PostRequest.class));
    }

    @Test
    void testDeleteAllPosts() throws Exception {
        mockMvc.perform(delete("/api/posts")
                        .header("X-User-Role", "redacteur"))
                .andExpect(status().isOk());

        verify(postService, times(1)).deletePosts();
    }

    @Test
    void testGetUserComments() throws Exception {
        Long postId = 1L;

        UserCommentResponse comment1 = UserCommentResponse.builder()
                .id(1L)
                .content("Test comment 1")
                .author("User1")
                .createdAt("2023-12-01T10:00:00")
                .postId(postId)
                .build();

        UserCommentResponse comment2 = UserCommentResponse.builder()
                .id(2L)
                .content("Test comment 2")
                .author("User2")
                .createdAt("2023-12-02T11:00:00")
                .postId(postId)
                .build();

        when(postService.getUserComments(postId)).thenReturn(Arrays.asList(comment1, comment2));

        ResponseEntity<List<UserCommentResponse>> response = postController.getUserComments(postId);

        verify(postService, times(1)).getUserComments(postId);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(2, response.getBody().size());
        Assertions.assertEquals("Test comment 1", response.getBody().get(0).getContent());
        Assertions.assertEquals("User1", response.getBody().get(0).getAuthor());
        Assertions.assertEquals("2023-12-01T10:00:00", response.getBody().get(0).getCreatedAt());
        Assertions.assertEquals(postId, response.getBody().get(0).getPostId());
    }

    @Test
    void deleteUserComment_ShouldCallServiceAndReturnOk() {
        Long postId = 1L;
        Long commentId = 1L;
        String expectedResponse = "Comment deleted successfully";  // Assuming the response message

        // Mock the behavior of deleteUserComment to return a response message
        when(postService.deleteUserComment(postId, commentId)).thenReturn(expectedResponse);

        ResponseEntity<Void> response = postController.deleteUserComment(postId, commentId);

        verify(postService, times(1)).deleteUserComment(postId, commentId);
        Assertions.assertEquals(200, response.getStatusCodeValue());  // Check if response is OK
    }

    @Test
    void updateUserComment_ShouldReturnUpdatedComment() {
        Long commentId = 1L;
        UserCommentRequest request = new UserCommentRequest();
        request.setContent("Updated comment");

        UserCommentResponse updatedComment = UserCommentResponse.builder()
                .id(commentId)
                .content("Updated comment")
                .author("User1")
                .createdAt("2023-12-03T15:00:00")
                .postId(1L)
                .build();

        when(postService.updateUserComment(commentId, request)).thenReturn(updatedComment);

        ResponseEntity<UserCommentResponse> response = postController.updateUserComment(commentId, request);

        verify(postService, times(1)).updateUserComment(commentId, request);
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("Updated comment", response.getBody().getContent());
        Assertions.assertEquals("User1", response.getBody().getAuthor());
        Assertions.assertEquals("2023-12-03T15:00:00", response.getBody().getCreatedAt());
    }
}
