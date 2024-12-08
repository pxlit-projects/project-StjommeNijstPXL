package be.pxl.services;

import be.pxl.services.controller.CommentController;
import be.pxl.services.domain.dto.UserCommentRequest;
import be.pxl.services.domain.dto.UserCommentResponse;
import be.pxl.services.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddComment() throws Exception {
        // Arrange
        UserCommentRequest request = UserCommentRequest.builder()
                .content("Test content")
                .author("Test author")
                .createdAt("2024-12-08 10:00:00")
                .postId(1L)
                .build();

        UserCommentResponse response = UserCommentResponse.builder()
                .id(1L)
                .content("Test content")
                .author("Test author")
                .createdAt("2024-12-08 10:00:00")
                .postId(1L)
                .build();

        when(commentService.addComment(any(UserCommentRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.content").value(response.getContent()))
                .andExpect(jsonPath("$.author").value(response.getAuthor()))
                .andExpect(jsonPath("$.createdAt").value(response.getCreatedAt()))
                .andExpect(jsonPath("$.postId").value(response.getPostId()));

        verify(commentService, times(1)).addComment(any(UserCommentRequest.class));
    }

    @Test
    void testGetCommentsByPost() throws Exception {
        // Arrange
        List<UserCommentResponse> responses = List.of(
                UserCommentResponse.builder().id(1L).content("Comment 1").build(),
                UserCommentResponse.builder().id(2L).content("Comment 2").build()
        );

        when(commentService.getCommentsByPostId(1L)).thenReturn(responses);

        // Act & Assert
        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(responses.size()))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value("Comment 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].content").value("Comment 2"));

        verify(commentService, times(1)).getCommentsByPostId(1L);
    }

    @Test
    void testUpdateComment() throws Exception {
        // Arrange
        UserCommentRequest request = UserCommentRequest.builder()
                .content("Updated content")
                .author("Updated author")
                .createdAt("2024-12-08 10:00:00")
                .postId(1L)
                .build();

        UserCommentResponse response = UserCommentResponse.builder()
                .id(1L)
                .content("Updated content")
                .author("Updated author")
                .createdAt("2024-12-08 10:00:00")
                .postId(1L)
                .build();

        when(commentService.updateComment(eq(1L), any(UserCommentRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/comments/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.content").value(response.getContent()))
                .andExpect(jsonPath("$.author").value(response.getAuthor()))
                .andExpect(jsonPath("$.createdAt").value(response.getCreatedAt()))
                .andExpect(jsonPath("$.postId").value(response.getPostId()));

        verify(commentService, times(1)).updateComment(eq(1L), any(UserCommentRequest.class));
    }

    @Test
    void testDeleteComment() throws Exception {
        // Arrange
        when(commentService.deleteComment(1L)).thenReturn("Comment deleted");

        // Act & Assert
        mockMvc.perform(delete("/api/comments/1/delete"))
                .andExpect(status().isOk())
                .andExpect(content().string("Comment deleted"));

        verify(commentService, times(1)).deleteComment(1L);
    }
}
