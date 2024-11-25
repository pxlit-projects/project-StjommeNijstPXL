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
public class PostRequest {

    private String title;
    private String content;
    private String author;
    private String createdAt;
    private Status status;
}
