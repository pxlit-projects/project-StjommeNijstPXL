package be.pxl.services.service;

import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostResponseWithComment;

import java.util.List;

public interface IPostService {
    PostResponse createPost(PostRequest postRequest);
    List<PostResponse> getAllPosts();
    PostResponse updatePost(Long id, PostRequest postRequest);
    PostResponse getPostById(Long id);
    List<PostResponse> getFilteredPosts(String startDate, String endDate, String author, String keyword);
    List<PostResponse> getConceptPosts();
    List<PostResponse> getNotApprovedPosts();
    List<PostResponseWithComment> getDeclinedPosts();
    void deletePosts();

    }
