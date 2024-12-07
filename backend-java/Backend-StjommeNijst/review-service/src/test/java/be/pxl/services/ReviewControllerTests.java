package be.pxl.services;

import be.pxl.services.domain.ReviewStatus;
import be.pxl.services.domain.dto.PostReviewResponse;
import be.pxl.services.service.ReviewService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ReviewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Container
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0");
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    void testGetAllPendingPosts_Success() throws Exception {
        // Arrange
        PostReviewResponse response = new PostReviewResponse(1L, 123L, "Post Title", "Author",
                "Content", "2023-12-06 14:30:00", ReviewStatus.WACHTEND);
        Mockito.when(reviewService.getAllPendingPosts()).thenReturn(List.of(response));

        // Act & Assert
        mockMvc.perform(get("/api/review/pending")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].postid", is(123)))
                .andExpect(jsonPath("$[0].title", is("Post Title")))
                .andExpect(jsonPath("$[0].status", is("WACHTEND")));
    }

    @BeforeEach
    public void setup() {
        objectMapper.findAndRegisterModules();
        objectMapper.configOverride(LocalDateTime.class)
                .setFormat(JsonFormat.Value.forPattern("yyyy-MM-dd HH:mm:ss"));
    }
    @Test
    void testApprovePost_Success() throws Exception {
        // Arrange
        Map<String, Object> response = Map.of("message", "Post is goedgekeurd!");
        Mockito.when(reviewService.approvePost(eq(1L))).thenReturn(ResponseEntity.ok(response));

        // Act & Assert
        mockMvc.perform(post("/api/review/1/approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Post is goedgekeurd!")));
    }

    @Test
    void testApprovePost_PostNotFound() throws Exception {
        // Arrange
        Mockito.when(reviewService.approvePost(eq(1L))).thenThrow(new IllegalArgumentException("Post met ID 1 niet gevonden."));

        // Act & Assert
        mockMvc.perform(post("/api/review/1/approve")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRejectPost_Success() throws Exception {
        // Arrange
        Map<String, Object> mockResponse = Map.of(
                "message", "Post is niet goedgekeurd!",
                "commentary", "Rejected due to spam"
        );
        Mockito.when(reviewService.rejectPost(eq(1L), eq("Rejected due to spam")))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // Act & Assert
        mockMvc.perform(post("/api/review/1/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\": \"Rejected due to spam\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentary", is("Rejected due to spam")))
                .andExpect(jsonPath("$.message", is("Post is niet goedgekeurd!")));
    }


    @Test
    void testRejectPost_PostNotFound() throws Exception {
        // Arrange
        Mockito.doThrow(new IllegalArgumentException("Post met ID 1 niet gevonden."))
                .when(reviewService).rejectPost(eq(1L), any());

        // Act & Assert
        mockMvc.perform(post("/api/review/1/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\": \"Rejected due to spam\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteReviews_Success() throws Exception {
        // Arrange
        Mockito.doNothing().when(reviewService).deleteAllReviews();

        // Act & Assert
        mockMvc.perform(delete("/api/review")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
