import { TestBed } from '@angular/core/testing';
import { HttpTestingController, HttpClientTestingModule } from '@angular/common/http/testing';
import { PostService } from './posts.service';
import { Status } from '../models/post-status.enum';
import { Post } from '../models/post.model';
import { UserCommentRequest } from '../models/user-comment-request.model';
import { UserCommentResponse } from '../models/user-comment-response.model';
import { AuthService } from './auth.service';
import { PostWithComment } from '../models/postWithComment.model';
import { HttpErrorResponse } from '@angular/common/http';

describe('PostService', () => {
  let service: PostService;
  let httpMock: HttpTestingController;
  let authService: jasmine.SpyObj<AuthService>;

  const mockApiUrl = 'http://localhost:8083/api/posts';
  const mockApiUrlReview = 'http://localhost:8083/api/review';

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getUserRole']);
    authServiceSpy.getUserRole.and.returnValue('USER');

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PostService,
        { provide: AuthService, useValue: authServiceSpy }
      ]
    });

    service = TestBed.inject(PostService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('Comment Operations', () => {
    it('should update a comment and handle success', () => {
      const commentId = 1;
      const commentRequest: UserCommentRequest = {
        content: 'Updated comment',
        author: 'Author',
        createdAt: '2025-01-12',
        postId: 1
      };
      const commentResponse: UserCommentResponse = { ...commentRequest, id: commentId };

      service.updateComment(commentId, commentRequest).subscribe(response => {
        expect(response).toEqual(commentResponse);
      });

      const req = httpMock.expectOne(`${mockApiUrl}/comments/${commentId}`);
      expect(req.request.method).toBe('PUT');
      req.flush(commentResponse);
    });

    it('should handle error when updating comment fails', () => {
      const commentId = 1;
      const commentRequest: UserCommentRequest = {
        content: 'Updated comment',
        author: 'Author',
        createdAt: '2025-01-12',
        postId: 1
      };
      const errorMessage = 'Comment update failed';

      service.updateComment(commentId, commentRequest).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(400);
          expect(error.error).toBe(errorMessage);
        }
      });

      const req = httpMock.expectOne(`${mockApiUrl}/comments/${commentId}`);
      req.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });

    it('should delete a comment and handle success', () => {
      const postId = 1;
      const commentId = 1;
    
      service.deleteComment(postId, commentId).subscribe(response => {
        expect(response).toBeNull(); // Change to expect null
      });
    
      const req = httpMock.expectOne(`${mockApiUrl}/${postId}/comments/${commentId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null); // Sending null as the response body
    });
    

    it('should handle error when deleting comment fails', () => {
      const postId = 1;
      const commentId = 1;
      const errorMessage = 'Delete failed';

      service.deleteComment(postId, commentId).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(404);
          expect(error.error).toBe(errorMessage);
        }
      });

      const req = httpMock.expectOne(`${mockApiUrl}/${postId}/comments/${commentId}`);
      req.flush(errorMessage, { status: 404, statusText: 'Not Found' });
    });
  });

  describe('Post Review Operations', () => {
    it('should get not approved posts with empty result', () => {
      service.getNotApprovedPosts().subscribe(posts => {
        expect(posts).toEqual([]);
      });

      const req = httpMock.expectOne(`${mockApiUrlReview}/pending`);
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });

    it('should handle error when getting not approved posts', () => {
      const errorMessage = 'Server error';

      service.getNotApprovedPosts().subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(500);
          expect(error.error).toBe(errorMessage);
        }
      });

      const req = httpMock.expectOne(`${mockApiUrlReview}/pending`);
      req.flush(errorMessage, { status: 500, statusText: 'Server Error' });
    });

    it('should reject post with empty comment', () => {
      const postId = 1;
      const comment = '';

      service.rejectPostWithComment(postId, comment).subscribe(response => {
        expect(response).toBe('Post rejected successfully');
      });

      const req = httpMock.expectOne(`${mockApiUrlReview}/${postId}/reject`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ comment });
      req.flush('Post rejected successfully');
    });
  });

  describe('Post Operations', () => {
    it('should create post with empty content', () => {
      const newPost = {
        title: 'New Post',
        content: '',
        author: 'Author'
      };

      service.createPost(newPost).subscribe();

      const req = httpMock.expectOne(mockApiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('X-User-Role')).toBe('USER');
      expect(req.request.body).toEqual(newPost);
      req.flush({});
    });

    it('should handle error when creating post', () => {
      const newPost = {
        title: 'New Post',
        content: 'Content',
        author: 'Author'
      };
      const errorMessage = 'Creation failed';

      service.createPost(newPost).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(400);
          expect(error.error).toBe(errorMessage);
        }
      });

      const req = httpMock.expectOne(mockApiUrl);
      req.flush(errorMessage, { status: 400, statusText: 'Bad Request' });
    });
  });

  describe('Post Filtering', () => {
    it('should handle empty filter parameters', () => {
      const filter = {};

      service.getFilteredPosts(filter).subscribe(posts => {
        expect(posts).toEqual([]);
      });

      const req = httpMock.expectOne(request => 
        request.url === `${mockApiUrl}/filter` && 
        request.params.get('status') === Status.GOEDGEKEURD
      );
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });


    it('should handle malformed date in filter', () => {
      const filter = {
        startDate: 'invalid-date',
        endDate: '2023-12-31'
      };

      service.getFilteredPosts(filter).subscribe();

      const req = httpMock.expectOne(request => 
        request.url === `${mockApiUrl}/filter`
      );
      expect(req.request.method).toBe('GET');
      req.flush([]);
    });
  });

  describe('Edge Cases', () => {
    it('should handle non-existent post ID', () => {
      const nonExistentId = 99999;
      const errorMessage = 'Post not found';

      service.getPostById(nonExistentId).subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(404);
          expect(error.error).toBe(errorMessage);
        }
      });

      const req = httpMock.expectOne(`${mockApiUrl}/${nonExistentId}`);
      req.flush(errorMessage, { status: 404, statusText: 'Not Found' });
    });

    it('should handle server timeout', () => {
      service.getAllPosts().subscribe({
        error: (error: HttpErrorResponse) => {
          expect(error.status).toBe(504);
        }
      });

      const req = httpMock.expectOne(mockApiUrl);
      req.error(new ErrorEvent('timeout'), { status: 504, statusText: 'Gateway Timeout' });
    });
  });
});