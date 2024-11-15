package be.pxl.services.service;

import be.pxl.services.domain.Dto.PostRequest;
import be.pxl.services.domain.Dto.PostResponse;
import be.pxl.services.domain.Post;
import be.pxl.services.repository.IPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final IPostRepository postRepository;

    public Post mapToPost(PostRequest postRequest) {
        return Post.builder()
                .author(postRequest.getAuthor())
                .content(postRequest.getContent())
                .title(postRequest.getTitle())
                .build();
    }

    public PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .content(post.getContent())
                .title(post.getTitle())
                .build();
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {
        postRepository.save(mapToPost(postRequest));
        return mapToPostResponse(mapToPost(postRequest));
    }

    @Override
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }
}
