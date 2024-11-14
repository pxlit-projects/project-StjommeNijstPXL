package be.pxl.services.service;

import be.pxl.services.domain.Dto.PostRequest;
import be.pxl.services.domain.Post;

public interface IPostService {
    Post createPost(PostRequest postRequest);
}
