package be.pxl.services;

import be.pxl.services.domain.UserComment;
import be.pxl.services.domain.dto.UserCommentRequest;
import be.pxl.services.domain.dto.UserCommentResponse;
import be.pxl.services.repository.UserCommentRepository;
import be.pxl.services.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private UserCommentRepository userCommentRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void testAddComment() {
        // Arrange
        UserCommentRequest request = UserCommentRequest.builder()
                .content("Test content")
                .author("Test author")
                .createdAt("2024-12-08 10:00:00")
                .postId(1L)
                .build();

        UserComment savedComment = UserComment.builder()
                .id(1L)
                .content(request.getContent())
                .author(request.getAuthor())
                .createdAt(LocalDateTime.of(2024, 12, 8, 10, 0))
                .postId(request.getPostId())
                .build();

        when(userCommentRepository.save(any(UserComment.class))).thenReturn(savedComment);

        // Act
        UserCommentResponse response = commentService.addComment(request);

        // Assert
        assertNotNull(response);
        assertEquals(savedComment.getId(), response.getId());
        assertEquals(savedComment.getContent(), response.getContent());
        verify(userCommentRepository, times(1)).save(any(UserComment.class));
    }

    @Test
    void testGetCommentsByPostId() {
        // Arrange
        Long postId = 1L;
        List<UserComment> comments = List.of(
                UserComment.builder()
                        .id(1L)
                        .content("Comment 1")
                        .author("Author 1")
                        .createdAt(LocalDateTime.of(2024, 12, 8, 10, 0))
                        .postId(postId)
                        .build(),
                UserComment.builder()
                        .id(2L)
                        .content("Comment 2")
                        .author("Author 2")
                        .createdAt(LocalDateTime.of(2024, 12, 8, 11, 0))
                        .postId(postId)
                        .build()
        );

        when(userCommentRepository.findByPostId(postId)).thenReturn(comments);

        // Act
        List<UserCommentResponse> responses = commentService.getCommentsByPostId(postId);

        // Assert
        assertEquals(2, responses.size());
        assertEquals("Comment 1", responses.get(0).getContent());
        assertEquals("Comment 2", responses.get(1).getContent());
        verify(userCommentRepository, times(1)).findByPostId(postId);
    }

    @Test
    void testDeleteComment() {
        // Arrange
        Long commentId = 1L;
        doNothing().when(userCommentRepository).deleteById(commentId);

        // Act
        String result = commentService.deleteComment(commentId);

        // Assert
        assertEquals("Comment deleted", result);
        verify(userCommentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void testUpdateComment_Found() {
        // Arrange
        Long commentId = 1L;
        UserComment existingComment = UserComment.builder()
                .id(commentId)
                .content("Old content")
                .author("Old author")
                .createdAt(LocalDateTime.of(2024, 12, 8, 10, 0))
                .postId(1L)
                .build();

        UserCommentRequest request = UserCommentRequest.builder()
                .content("Updated content")
                .author("Updated author")
                .createdAt("2024-12-08 11:00:00")
                .postId(2L)
                .build();

        when(userCommentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(userCommentRepository.save(any(UserComment.class))).thenReturn(existingComment);

        // Act
        UserCommentResponse response = commentService.updateComment(commentId, request);

        // Assert
        assertNotNull(response);
        assertEquals(request.getContent(), response.getContent());
        verify(userCommentRepository, times(1)).findById(commentId);
        verify(userCommentRepository, times(1)).save(existingComment);
    }

    @Test
    void testUpdateComment_NotFound() {
        // Arrange
        Long commentId = 1L;
        UserCommentRequest request = UserCommentRequest.builder()
                .content("Updated content")
                .author("Updated author")
                .createdAt("2024-12-08 11:00:00")
                .postId(2L)
                .build();

        when(userCommentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act
        UserCommentResponse response = commentService.updateComment(commentId, request);

        // Assert
        assertNull(response);
        verify(userCommentRepository, times(1)).findById(commentId);
        verify(userCommentRepository, never()).save(any(UserComment.class));
    }
}
