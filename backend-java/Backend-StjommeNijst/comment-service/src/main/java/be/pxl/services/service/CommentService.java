package be.pxl.services.service;

import be.pxl.services.domain.UserComment;
import be.pxl.services.domain.dto.UserCommentRequest;
import be.pxl.services.domain.dto.UserCommentResponse;
import be.pxl.services.repository.UserCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final UserCommentRepository userCommentRepository;

    // Convert a String to LocalDateTime
    private LocalDateTime fromStringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    // Convert a LocalDateTime to String
    private String fromLocalDateTimeToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }



    // Map UserComment to UserCommentResponse DTO
    private UserCommentResponse mapToUserCommentResponse(UserComment userComment) {
        return UserCommentResponse.builder()
                .id(userComment.getId())
                .author(userComment.getAuthor())
                .content(userComment.getContent())
                .createdAt(fromLocalDateTimeToString(userComment.getCreatedAt()))
                .postId(userComment.getPostId())
                .build();
    }

    private UserComment mapToUserComment(UserCommentRequest userCommentRequest)
    {
        return UserComment.builder()
                .createdAt(fromStringToLocalDateTime(userCommentRequest.getCreatedAt()))
                .author(userCommentRequest.getAuthor())
                .postId(userCommentRequest.getPostId())
                .content(userCommentRequest.getContent())
                .build();
    }

    // Save a comment and return as DTO
    public UserCommentResponse addComment(UserCommentRequest userCommentRequest) {
        UserComment savedComment = userCommentRepository.save(mapToUserComment(userCommentRequest));
        return mapToUserCommentResponse(savedComment);
    }

    // Get comments by Post ID and return as DTOs
    public List<UserCommentResponse> getCommentsByPostId(Long postId) {
        List<UserComment> comments = userCommentRepository.findByPostId(postId);
        return comments.stream()
                .map(this::mapToUserCommentResponse)
                .collect(Collectors.toList());
    }

    public String deleteComment(Long commentId) {
        userCommentRepository.deleteById(commentId);
        return "Comment deleted";
    }

    public UserCommentResponse updateComment(Long commentId, UserCommentRequest userCommentRequest) {

        UserComment existingComment = userCommentRepository.findById(commentId).orElse(null);
        if (existingComment != null) {
            existingComment.setContent(userCommentRequest.getContent());
            existingComment.setCreatedAt(fromStringToLocalDateTime(userCommentRequest.getCreatedAt()));
            existingComment.setPostId(userCommentRequest.getPostId());
            existingComment.setAuthor(userCommentRequest.getAuthor());
            userCommentRepository.save(existingComment);
            return mapToUserCommentResponse(existingComment);
        }
        return null;
    }
}
