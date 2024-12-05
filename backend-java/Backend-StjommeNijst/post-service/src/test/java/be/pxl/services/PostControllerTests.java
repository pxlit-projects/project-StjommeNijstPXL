package be.pxl.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.Status;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.UserCommentRequest;
import be.pxl.services.domain.dto.UserCommentResponse;
import be.pxl.services.repository.IPostRepository;
import be.pxl.services.service.PostService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class PostControllerTests
{
    @Mock
    PostService postService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Container
    private static MySQLContainer sqlContainer = new MySQLContainer("mysql:8.0");
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }
    @BeforeEach
    public void setup() {
        objectMapper.findAndRegisterModules();
        objectMapper.configOverride(LocalDateTime.class)
                .setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd HH:mm:ss"));
    }
    @Test
    public void testCreatePostShouldReturn200() throws Exception {
        PostRequest postRequest
                = PostRequest.builder()
                .title("Test Post")
                .author("Test author")
                .content("Test content")
                .createdAt("2024-12-05 14:30:45")
                .status(Status.WACHTEND)
                .build();

        String postString = objectMapper.writeValueAsString(postRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postString))
                .andExpect(status().isOk());

    }
    @Test
    public void testGetAllPostsShouldReturn200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());

    }
    @Test
    public void testGetPostByIdShouldReturn200() throws Exception {
        // Een bestaande post opslaan
        Post post = Post.builder()
                .id(2L)
                .title("Test Post")
                .author("Test author")
                .content("Test content")
                .createdAt(LocalDateTime.now())
                .status(Status.WACHTEND)
                .build();
        postRepository.save(post);

        // Perform GET request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/" + 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expecting HTTP 200
                .andReturn();

        // Extract JSON response
        String jsonResponse = result.getResponse().getContentAsString();
        PostResponse responsePost;
        responsePost = objectMapper.readValue(jsonResponse, PostResponse.class);

        // Validate the response
        Assertions.assertNotNull(responsePost);
        Assertions.assertEquals(post.getId(), responsePost.getId());
        Assertions.assertEquals(post.getTitle(), responsePost.getTitle());
        Assertions.assertEquals(post.getAuthor(), responsePost.getAuthor());
    }
    @Test
    public void testGetConceptPostsShouldReturn200() throws Exception {
        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .author("Test author")
                .content("Test content")
                .createdAt(LocalDateTime.now())
                .status(Status.CONCEPT)
                .build();
        postRepository.save(post);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/concepts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<PostResponse> responsePosts = objectMapper.readValue(jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponse.class));

        Assertions.assertNotNull(responsePosts);
        Assertions.assertFalse(responsePosts.isEmpty());
        Assertions.assertEquals(Status.CONCEPT, responsePosts.get(0).getStatus());
    }
    @Test
    public void testGetDeclinedPostsShouldReturn200() throws Exception {
        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .author("Test author")
                .content("Test content")
                .createdAt(LocalDateTime.now())
                .status(Status.NIET_GOEDGEKEURD)
                .build();
        postRepository.save(post);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/declined")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<PostResponse> responsePosts = objectMapper.readValue(jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponse.class));

        Assertions.assertNotNull(responsePosts);
        Assertions.assertFalse(responsePosts.isEmpty());
        Assertions.assertEquals(Status.NIET_GOEDGEKEURD, responsePosts.get(0).getStatus());
    }
    @Test
    public void testGetNotApprovedPostsShouldReturn200() throws Exception {
        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .author("Test author")
                .content("Test content")
                .createdAt(LocalDateTime.now())
                .status(Status.WACHTEND)
                .build();
        postRepository.save(post);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/notapproved")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<PostResponse> responsePosts = objectMapper.readValue(jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponse.class));

        Assertions.assertNotNull(responsePosts);
        Assertions.assertFalse(responsePosts.isEmpty());
        Assertions.assertEquals(Status.WACHTEND, responsePosts.get(0).getStatus());
    }
    @Test
    public void testUpdateUserCommentShouldReturn200() throws Exception {
        // Create a request object for the updated comment
        UserCommentRequest updatedCommentRequest = UserCommentRequest.builder()
                .content("Updated comment!")
                .author("John Doe")
                .createdAt("2024-12-05 12:00:00")
                .postId(1L)
                .build();

        // Convert the updated comment object to JSON
        String requestBody = objectMapper.writeValueAsString(updatedCommentRequest);

        // Create a mock response for the updated comment
        UserCommentResponse updatedCommentResponse = new UserCommentResponse(1L, "Updated comment!", "John Doe", "2024-12-05 12:00:00", 1L);
        System.out.println("Mock Response: " + objectMapper.writeValueAsString(updatedCommentResponse));

        // Mock the service method
        when(postService.updateUserComment(eq(1L), any(UserCommentRequest.class))).thenReturn(updatedCommentResponse);

        // Perform the PUT request
        String responseBody = mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn().getResponse().getContentAsString();

        System.out.println("Response Body: " + responseBody);

        // Perform assertions
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/comments/{commentId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetFilteredPostsShouldReturnFilteredResults() throws Exception {
        // Testdata voorbereiden
        Post post1 = Post.builder()
                .id(1L)
                .title("First Post")
                .author("Author1")
                .content("Content with keyword test")
                .createdAt(LocalDateTime.of(2024, 12, 1, 10, 0))
                .status(Status.GOEDGEKEURD)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("Second Post")
                .author("Author2")
                .content("Other content")
                .createdAt(LocalDateTime.of(2024, 12, 2, 15, 0))
                .status(Status.GOEDGEKEURD)
                .build();

        postRepository.save(post1);
        postRepository.save(post2);

        String startDate = "2024-12-01 00:00:00";
        String endDate = "2024-12-03 23:59:59";
        String author = "Author1";
        String keyword = "keyword";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/filter")
                        .param("startDate", startDate)
                        .param("endDate", endDate)
                        .param("author", author)
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<PostResponse> responsePosts = objectMapper.readValue(jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, PostResponse.class));

        Assertions.assertNotNull(responsePosts);
        Assertions.assertEquals(1, responsePosts.size());
        Assertions.assertEquals("First Post", responsePosts.get(0).getTitle());
        Assertions.assertEquals("Author1", responsePosts.get(0).getAuthor());
    }
    @Test
    public void testDeleteAllPostsShouldClearDatabase() throws Exception {
        Post post1 = Post.builder()
                .id(1L)
                .title("First Post")
                .author("Author1")
                .content("Content of first post")
                .createdAt(LocalDateTime.now())
                .status(Status.GOEDGEKEURD)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("Second Post")
                .author("Author2")
                .content("Content of second post")
                .createdAt(LocalDateTime.now())
                .status(Status.CONCEPT)
                .build();

        postRepository.save(post1);
        postRepository.save(post2);

        Assertions.assertFalse(postRepository.findAll().isEmpty());
        Assertions.assertEquals(2, postRepository.findAll().size());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertTrue(postRepository.findAll().isEmpty());
    }
    @Test
    public void testUpdatePostShouldReturn200WithGoedgekeurdStatus() throws Exception {
        // Een bestaande post opslaan
        Post post = Post.builder()
                .id(1L)
                .title("Original Title")
                .author("Original Author")
                .content("Original Content")
                .createdAt(LocalDateTime.now())
                .status(Status.WACHTEND)  // Status die we willen testen
                .build();
        postRepository.save(post);

        PostRequest postRequest = PostRequest.builder()
                .title("Updated Title")
                .author("Updated Author")
                .content("Updated Content")
                .createdAt("2024-12-05 14:30:45")
                .status(Status.GOEDGEKEURD)  // Status na update
                .build();

        String postRequestJson = objectMapper.writeValueAsString(postRequest);

        // Perform the PUT request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequestJson))
                .andExpect(status().isOk()) // Verwachte status is OK (200)
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        PostResponse responsePost = objectMapper.readValue(jsonResponse, PostResponse.class);

        // Asserties
        Assertions.assertNotNull(responsePost);
        Assertions.assertEquals(post.getId(), responsePost.getId());
        Assertions.assertEquals("Updated Title", responsePost.getTitle());
        Assertions.assertEquals("Updated Author", responsePost.getAuthor());
        Assertions.assertEquals("Updated Content", responsePost.getContent());
        Assertions.assertEquals(Status.GOEDGEKEURD, responsePost.getStatus());
    }
    @Test
    public void testUpdatePostShouldReturn404WhenPostNotFound() throws Exception {
        // Post met id 999 bestaat niet
        Long nonExistingPostId = 999L;

        PostRequest postRequest = PostRequest.builder()
                .title("Updated Title")
                .author("Updated Author")
                .content("Updated Content")
                .createdAt("2024-12-05 14:30:45")
                .status(Status.GOEDGEKEURD)
                .build();

        String postRequestJson = objectMapper.writeValueAsString(postRequest);

        // Perform the PUT request
        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/" + nonExistingPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postRequestJson))
                .andExpect(status().isNotFound()) // Verwachte status is 404 (Not Found)
                .andReturn();
    }
    @Test
    public void testAddUserCommentShouldReturn200() throws Exception {
        // Maak een request object
        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .author("Test author")
                .content("Test content")
                .createdAt(LocalDateTime.now())
                .status(Status.GOEDGEKEURD)
                .build();
        postRepository.save(post);
        UserCommentRequest userCommentRequest = UserCommentRequest.builder()
                .content("Great post!")
                .author("John Doe")
                .createdAt("2024-12-05 12:00:00")
                .postId(1L)
                .build();

        // Zet het request object om naar JSON
        String requestBody = objectMapper.writeValueAsString(userCommentRequest);

        // Voer de POST request uit naar de juiste URL
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts/{postId}/comments", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());  // Verwacht een 200 statuscode
    }
    @Test
    public void testGetUserCommentsShouldReturn200() throws Exception {
        // Create a mock list of comments (you could mock your service here)
        UserCommentResponse commentResponse = new UserCommentResponse(1L, "Great post!", "John Doe", "2024-12-05 12:00:00", 1L);
        List<UserCommentResponse> comments = Collections.singletonList(commentResponse);

        // Mock the service method
        when(postService.getUserComments(1L)).thenReturn(comments);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/{postId}/comments", 1L))
                .andExpect(status().isOk())  // Expecting a 200 status code
                .andExpect(jsonPath("$[0].content").value("Great post!"))  // Check the content of the first comment
                .andExpect(jsonPath("$[0].author").value("John Doe"));  // Check the author
    }
    @Test
    public void testDeleteUserCommentShouldReturn200() throws Exception {
        // Mock the service method
        when(postService.deleteUserComment(1L, 1L)).thenReturn("Comment deleted");

        Post post = Post.builder()
                .id(1L)
                .title("Test Post")
                .author("Test author")
                .content("Test content")
                .createdAt(LocalDateTime.now())
                .status(Status.WACHTEND)
                .build();
        postRepository.save(post);

        // Perform the DELETE request
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/{postId}/comments/{commentId}", 1L, 1L))
                .andExpect(status().isOk());  // Expecting a 200 status code
    }


}
