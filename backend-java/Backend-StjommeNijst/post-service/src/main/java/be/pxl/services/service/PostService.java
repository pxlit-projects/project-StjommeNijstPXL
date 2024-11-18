package be.pxl.services.service;

import be.pxl.services.domain.Dto.PostRequest;
import be.pxl.services.domain.Dto.PostResponse;
import be.pxl.services.domain.Post;
import be.pxl.services.repository.IPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final IPostRepository postRepository;

    public LocalDateTime fromStringToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    public String fromLocalDateTimeToString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

    public Post mapToPost(PostRequest postRequest) {
        return Post.builder()
                .author(postRequest.getAuthor())
                .content(postRequest.getContent())
                .title(postRequest.getTitle())
                .createdAt(fromStringToLocalDateTime(postRequest.getCreatedAt()))
                .build();
    }

    public PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .author(post.getAuthor())
                .content(post.getContent())
                .title(post.getTitle())
                .createdAt(fromLocalDateTimeToString(post.getCreatedAt()))
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

    @Override
    public PostResponse updatePost(Long id, PostRequest postRequest) {
        Post existingPost = postRepository.findById(id).orElse(null);
        if (existingPost != null) {
            existingPost.setTitle(postRequest.getTitle());
            existingPost.setContent(postRequest.getContent());
            existingPost.setAuthor(postRequest.getAuthor());
            existingPost.setCreatedAt(fromStringToLocalDateTime(postRequest.getCreatedAt()));

            postRepository.save(existingPost);

            return mapToPostResponse(existingPost);
        }
        return null;
    }

    @Override
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return null;
        }
        return mapToPostResponse(post);
    }

    @Override
    public List<PostResponse> getFilteredPosts(String startDate, String endDate, String author, String keyword) {
        // Haal de gefilterde posts op via de repository
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        if (startDate != null){
            startDateTime = fromStringToLocalDateTime(startDate);
        }
        if (endDate != null){
            endDateTime = fromStringToLocalDateTime(endDate);
        }
        List<Post> posts = postRepository.findFilteredPosts(startDateTime, endDateTime, author, keyword);

        // Zet de gefilterde posts om naar PostResponse objecten
        return posts.stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
    }
}
