import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CreatePostsComponent } from './create-posts.component';
import { PostService } from '../services/posts.service';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { Status } from '../models/post-status.enum';

describe('CreatePostsComponent', () => {
  let component: CreatePostsComponent;
  let fixture: ComponentFixture<CreatePostsComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', ['createPost']);
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['getUserName']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: AuthService, useValue: authServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CreatePostsComponent);
    component = fixture.componentInstance;
    authService.getUserName.and.returnValue('Test User');
    fixture.detectChanges();
  });

  describe('Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with empty fields', () => {
      expect(component.title).toBe('');
      expect(component.content).toBe('');
      expect(component.author).toBe('Test User');
    });

    it('should set author from AuthService on init', () => {
      component.ngOnInit();
      expect(component.author).toBe('Test User');
      expect(authService.getUserName).toHaveBeenCalled();
    });
  });

  describe('Navigation', () => {
    it('should navigate to declined posts', () => {
      component.goToDeclinedPosts();
      expect(router.navigate).toHaveBeenCalledWith(['/declined-posts']);
    });

    it('should navigate to saved posts', () => {
      component.goToSavedPosts();
      expect(router.navigate).toHaveBeenCalledWith(['/concept-posts']);
    });
  });

  describe('Save as Draft', () => {
    it('should save post as draft when all fields are filled', fakeAsync(() => {
      const now = new Date('2024-01-01T12:00:00');
      jasmine.clock().mockDate(now);
      
      component.title = 'Test Title';
      component.content = 'Test Content';
      component.author = 'Test Author';
      component.now = now;

      postService.createPost.and.returnValue(of({}));

      component.saveAsDraft();
      tick();

      expect(postService.createPost).toHaveBeenCalledWith({
        title: 'Test Title',
        content: 'Test Content',
        author: 'Test Author',
        createdAt: '2024-01-01 12:00:00',
        status: Status.CONCEPT
      });
      expect(router.navigate).toHaveBeenCalledWith(['/posts']);
    }));

    it('should show alert when fields are empty', () => {
      spyOn(window, 'alert');
      component.title = '';
      component.content = 'Test Content';
      component.author = 'Test Author';

      component.saveAsDraft();

      expect(window.alert).toHaveBeenCalledWith('Vul alle velden in!');
      expect(postService.createPost).not.toHaveBeenCalled();
    });

    it('should handle error when saving draft', fakeAsync(() => {
      spyOn(console, 'error');
      component.title = 'Test Title';
      component.content = 'Test Content';
      component.author = 'Test Author';

      postService.createPost.and.returnValue(throwError(() => new Error('Save failed')));

      component.saveAsDraft();
      tick();

      expect(console.error).toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    }));
  });

  describe('Create Post', () => {
    it('should create post when all fields are filled', fakeAsync(() => {
      const now = new Date('2024-01-01T12:00:00');
      jasmine.clock().mockDate(now);
      
      component.title = 'Test Title';
      component.content = 'Test Content';
      component.author = 'Test Author';
      component.now = now;

      postService.createPost.and.returnValue(of({}));

      component.createPost();
      tick();

      expect(postService.createPost).toHaveBeenCalledWith({
        title: 'Test Title',
        content: 'Test Content',
        author: 'Test Author',
        createdAt: '2024-01-01 12:00:00',
        status: Status.WACHTEND
      });
      expect(router.navigate).toHaveBeenCalledWith(['/posts']);
    }));

    it('should show alert when fields are empty', () => {
      spyOn(window, 'alert');
      component.title = 'Test Title';
      component.content = '';
      component.author = 'Test Author';

      component.createPost();

      expect(window.alert).toHaveBeenCalledWith('Vul alle velden in!');
      expect(postService.createPost).not.toHaveBeenCalled();
    });

    it('should handle error when creating post', fakeAsync(() => {
      spyOn(console, 'error');
      component.title = 'Test Title';
      component.content = 'Test Content';
      component.author = 'Test Author';

      postService.createPost.and.returnValue(throwError(() => new Error('Creation failed')));

      component.createPost();
      tick();

      expect(console.error).toHaveBeenCalled();
      expect(router.navigate).not.toHaveBeenCalled();
    }));
  });

  describe('Date Formatting', () => {
    it('should format date correctly with single digits', () => {
      const now = new Date('2024-01-01T01:02:03');
      component.now = now;
      
      component.title = 'Test';
      component.content = 'Test';
      component.author = 'Test';
      
      postService.createPost.and.returnValue(of({}));
      
      component.createPost();
      
      const expectedDate = '2024-01-01 01:02:03';
      expect(postService.createPost).toHaveBeenCalledWith(jasmine.objectContaining({
        createdAt: expectedDate
      }));
    });

    it('should format date correctly with double digits', () => {
      const now = new Date('2024-12-31T23:59:59');
      component.now = now;
      
      component.title = 'Test';
      component.content = 'Test';
      component.author = 'Test';
      
      postService.createPost.and.returnValue(of({}));
      
      component.createPost();
      
      const expectedDate = '2024-12-31 23:59:59';
      expect(postService.createPost).toHaveBeenCalledWith(jasmine.objectContaining({
        createdAt: expectedDate
      }));
    });
  });
});