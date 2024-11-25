package be.pxl.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class PostReviewMessage implements Serializable {
    private Long postId;
    private String title;
    private String content;
    private String author;
    private String createdAt;
    private String status; // Status als String (CONCEPT, NIET_GOEDGEKEURD, enz.)
}

