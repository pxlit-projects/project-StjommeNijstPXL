package be.pxl.services.repository;

import be.pxl.services.domain.PostReview;
import be.pxl.services.domain.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IPostReviewRepository extends JpaRepository<PostReview, Long>
{
    List<PostReview> findAllByStatus(ReviewStatus status);

}
