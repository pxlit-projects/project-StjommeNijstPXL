package be.pxl.services.repository;

import be.pxl.services.domain.UserComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCommentRepository extends JpaRepository<UserComment, Long> {
    List<UserComment> findByPostId(Long postId);
}

