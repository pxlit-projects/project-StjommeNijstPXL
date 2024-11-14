package be.pxl.services.service;

import be.pxl.services.domain.Dto.PostRequest;
import be.pxl.services.domain.Post;
import be.pxl.services.repository.IPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public Post createPost(PostRequest postRequest) {
        return postRepository.save(mapToPost(postRequest));
    }
}
