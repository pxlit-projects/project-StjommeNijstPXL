package be.pxl.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PostReviewMessageWithComment implements Serializable {
    private Long postId;
    private String title;
    private String content;
    private String author;
    private String createdAt;
    private String status;
    private String comment;
}

