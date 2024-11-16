package be.pxl.services.service;

import be.pxl.services.domain.Dto.PostRequest;
import be.pxl.services.domain.Dto.PostResponse;

import java.util.List;

public interface IPostService {
    PostResponse createPost(PostRequest postRequest);
    List<PostResponse> getAllPosts();
    PostResponse updatePost(Long id, PostRequest postRequest);
    PostResponse getPostById(Long id);

    //List<PostResponse> getFilteredPosts(String startDate, String endDate, String author, String keyword);
}
