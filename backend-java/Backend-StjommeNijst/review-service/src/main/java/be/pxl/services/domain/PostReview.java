package be.pxl.services.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId; // ID van de post uit de Post-service
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status; // WACHTEND, GOEDGEKEURD, AFGEKEURD
}
