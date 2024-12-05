package be.pxl.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PostDomainTests {

    @Test
    public void testPostBuilder() {
        // Builder wordt gebruikt om een nieuw Post-object te creëren
        Post post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author("Test Author")
                .createdAt(LocalDateTime.now())
                .status(Status.GOEDGEKEURD)
                .comment("Test Comment")
                .userCommentIds(List.of(1L, 2L))
                .build();

        // Controleer of de waarden correct zijn ingesteld
        assertThat(post.getId()).isEqualTo(1L);
        assertThat(post.getTitle()).isEqualTo("Test Title");
        assertThat(post.getContent()).isEqualTo("Test Content");
        assertThat(post.getAuthor()).isEqualTo("Test Author");
        assertThat(post.getStatus()).isEqualTo(Status.GOEDGEKEURD);
        assertThat(post.getUserCommentIds()).containsExactly(1L, 2L);
    }
}
