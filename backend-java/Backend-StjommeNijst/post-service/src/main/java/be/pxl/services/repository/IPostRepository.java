package be.pxl.services.repository;

import be.pxl.services.domain.Dto.PostResponse;
import be.pxl.services.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IPostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE " +
            "(p.createdAt >= :startDate OR :startDate IS NULL) AND " +
            "(p.createdAt <= :endDate OR :endDate IS NULL) AND " +
            "(p.author = :author OR :author IS NULL) AND " +
            "(p.content LIKE %:keyword% OR :keyword IS NULL)")
    List<Post> findFilteredPosts(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("author") String author,
            @Param("keyword") String keyword);
}
