package be.pxl.services.domain.dto;

import be.pxl.services.domain.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReviewResponse {
    private Long id;
    private Long postid;
    private String title;
    private String content;
    private String author;
    private String createdAt;
    private ReviewStatus status;
}
