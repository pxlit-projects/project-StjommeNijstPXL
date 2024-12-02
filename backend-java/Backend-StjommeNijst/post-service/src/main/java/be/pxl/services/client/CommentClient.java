package be.pxl.services.client;


import be.pxl.services.domain.dto.UserCommentRequest;
import be.pxl.services.domain.dto.UserCommentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "comment-service", url = "http://localhost:8087/api/comments/")
public interface CommentClient {

    @PostMapping("/{postId}")
    void addCommentToPost(@PathVariable Long postId, @RequestBody UserCommentRequest request);

    @GetMapping("/{postId}")
    List<UserCommentResponse> getCommentsByPost(@PathVariable Long postId);

    @PutMapping("/{commentId}/update")
    UserCommentResponse updateComment(@PathVariable Long commentId, @RequestBody UserCommentRequest request);

    @DeleteMapping("/{commentId}/delete")
    String deleteComment(@PathVariable Long commentId);
}
