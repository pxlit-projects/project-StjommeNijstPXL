package be.pxl.services.domain.dto;

import be.pxl.services.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseWithComment {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String createdAt;
    private Status status;
    private String comment;
}