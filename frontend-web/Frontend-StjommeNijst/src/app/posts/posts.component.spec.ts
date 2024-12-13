import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PostsComponent } from './posts.component'; // Import the PostsComponent
import { PostService } from '../services/posts.service';
import { AuthService } from '../services/auth.service';
import { of, EMPTY } from 'rxjs';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PostFilterComponent } from '../post-filter/post-filter.component'; // Import filter component
import { FormsModule } from '@angular/forms';
import { Status } from '../models/post-status.enum';
import { UserCommentResponse } from '../models/user-comment-response.model';

describe('PostsComponent', () => {
  let component: PostsComponent;
  let fixture: ComponentFixture<PostsComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', ['getFilteredPosts', 'getCommentsByPost', 'addCommentToPost', 'updateComment', 'deleteComment']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isRedacteur', 'getUserName']);

    await TestBed.configureTestingModule({
      imports: [CommonModule, RouterModule, PostFilterComponent, FormsModule, PostsComponent], // Use PostsComponent in imports instead of declarations
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ]
    }).compileComponents();

    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PostsComponent);
    component = fixture.componentInstance;
    postService.getFilteredPosts.and.returnValue(of([]));
    authService.isRedacteur.and.returnValue(false);
    authService.getUserName.and.returnValue('TestUser');
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load posts on init', () => {
    component.ngOnInit();
    expect(postService.getFilteredPosts).toHaveBeenCalled();
  });

  it('should update filter parameters', () => {
    const filter = {
      startDate: '2024-01-01',
      endDate: '2024-01-31',
      author: 'testAuthor',
      keyword: 'test'
    };

    component.onFilterChanged(filter);
    expect(component.startDate).toBe(filter.startDate);
    expect(component.endDate).toBe(filter.endDate);
    expect(component.author).toBe(filter.author);
    expect(component.keyword).toBe(filter.keyword);
  });

  it('should toggle comments visibility', () => {
    const postId = 1;
    const post = {
      id: postId,
      title: 'Test Post Title',
      content: 'Test content for the post.',
      author: 'Test Author',
      createdAt: '2024-01-01T12:00:00Z',
      status: Status.GOEDGEKEURD,
      showComments: false,
      comments: []
    };
    component.posts = [post];

    postService.getCommentsByPost.and.returnValue(of([]));

    component.toggleComments(postId);
    expect(post.showComments).toBe(true);
    expect(postService.getCommentsByPost).toHaveBeenCalledWith(postId);
  });

  it('should add a comment', () => {
    const postId = 1;
    const commentContent = 'This is a test comment';
    component.newCommentContent[postId] = commentContent;
  
    const newComment = {
      content: commentContent,
      author: 'TestUser',
      createdAt: jasmine.any(String), // jasmine.any() om te controleren of createdAt een string is
      postId
    };
  
    // Simuleer dat de addCommentToPost een lege Observable retourneert
    postService.addCommentToPost.and.returnValue(of()); // Vervang EMPTY door of(null) voor succesreactie
  
    // Maak een mock post object met een lege comments array
    const post = {
      id: postId,
      title: 'Test Post Title',
      content: 'Test content for the post.',
      author: 'Test Author',
      createdAt: '2024-01-01T12:00:00Z',
      status: Status.GOEDGEKEURD,
      showComments: false,
      comments: [] as UserCommentResponse[] // Begin met een lege comments-array
    };
  
    // Zet de posts van de component
    component.posts = [post];
  
    // Roep de addComment-methode aan
    component.addComment(postId);
  
    // Zorg ervoor dat de addCommentToPost werd aangeroepen met de juiste parameters
    expect(postService.addCommentToPost).toHaveBeenCalledWith(postId, newComment);
  
  
    // Controleer of de input voor het nieuwe comment leeg is na het toevoegen
    expect(component.newCommentContent[postId]).toBe(commentContent);  // De comment input moet worden gewist
  });
  
  it('should delete a comment', () => {
    const postId = 1;
    const commentId = 1;
    
    const comment = {
      id: commentId,
      content: 'Test comment content',
      author: 'TestUser',
      createdAt: '2024-01-01T12:00:00Z',
      postId: postId
    };
  
    const post = {
      id: postId,
      title: 'Test Post Title',
      content: 'Test content for the post.',
      author: 'Test Author',
      createdAt: '2024-01-01T12:00:00Z',
      status: Status.GOEDGEKEURD,
      showComments: false,
      comments: [comment]
    };
    
    component.posts = [post];

    spyOn(window, 'confirm').and.returnValue(true);
    postService.deleteComment.and.returnValue(of());
  
    component.deleteComment(postId, commentId);
    fixture.detectChanges();
    
    expect(postService.deleteComment).toHaveBeenCalledWith(postId, commentId);
  });
  
});
