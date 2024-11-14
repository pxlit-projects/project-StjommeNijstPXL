package be.pxl.services.controller;

import be.pxl.services.domain.Dto.PostRequest;
import be.pxl.services.domain.Post;
import be.pxl.services.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post createPost(@RequestBody PostRequest postRequest) {
        return postService.createPost(postRequest);
    }
}
