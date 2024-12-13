import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DeclinedPostsComponent } from './declined-posts.component';
import { PostService } from '../services/posts.service';
import { RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { PostWithComment } from '../models/postWithComment.model';
import { Status } from '../models/post-status.enum';
import { ActivatedRoute } from '@angular/router';


describe('DeclinedPostsComponent', () => {
  let component: DeclinedPostsComponent;
  let fixture: ComponentFixture<DeclinedPostsComponent>;
  let postService: jasmine.SpyObj<PostService>;

  const mockDeclinedPosts: PostWithComment[] = [
    {
      id: 1,
      title: 'Declined Post 1',
      content: 'Content 1',
      author: 'Author 1',
      createdAt: '2024-01-01',
      status: Status.NIET_GOEDGEKEURD,
      comment: 'Rejection reason 1'
    },
    {
      id: 2,
      title: 'Declined Post 2',
      content: 'Content 2',
      author: 'Author 2',
      createdAt: '2024-01-02',
      status: Status.NIET_GOEDGEKEURD,
      comment: 'Rejection reason 2'
    }
  ];

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', ['getDeclinedPosts']);
    
    await TestBed.configureTestingModule({
      imports: [RouterModule],
      declarations: [],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { params: {}, queryParams: {} }, // Mock any properties you need
            paramMap: of({ get: (key: string) => null }), // Mock paramMap if required
        }
      }
      ]
    }).compileComponents();

    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeclinedPostsComponent);
    component = fixture.componentInstance;
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with empty declined posts array', () => {
      expect(component.declinedPosts).toEqual([]);
    });
  });

  describe('Loading Declined Posts', () => {
    it('should load declined posts on init', fakeAsync(() => {
      postService.getDeclinedPosts.and.returnValue(of(mockDeclinedPosts));
      
      fixture.detectChanges(); // Triggers ngOnInit
      tick();

      expect(component.declinedPosts).toEqual(mockDeclinedPosts);
      expect(postService.getDeclinedPosts).toHaveBeenCalled();
    }));

    it('should handle empty response', fakeAsync(() => {
      postService.getDeclinedPosts.and.returnValue(of([]));
      
      fixture.detectChanges();
      tick();

      expect(component.declinedPosts).toEqual([]);
      expect(postService.getDeclinedPosts).toHaveBeenCalled();
    }));

    it('should handle error when loading declined posts', fakeAsync(() => {
      const errorMessage = 'Error loading declined posts';
      spyOn(console, 'error');
      postService.getDeclinedPosts.and.returnValue(throwError(() => new Error(errorMessage)));
      
      fixture.detectChanges();
      tick();

      expect(component.declinedPosts).toEqual([]);
      expect(console.error).toHaveBeenCalled();
      expect(postService.getDeclinedPosts).toHaveBeenCalled();
    }));
  });

  describe('Manual Load', () => {
    it('should allow manual loading of declined posts', fakeAsync(() => {
      postService.getDeclinedPosts.and.returnValue(of(mockDeclinedPosts));
      
      component.loadSavedConcepts();
      tick();

      expect(component.declinedPosts).toEqual(mockDeclinedPosts);
      expect(postService.getDeclinedPosts).toHaveBeenCalled();
    }));

    it('should handle error during manual load', fakeAsync(() => {
      const errorMessage = 'Error loading declined posts';
      spyOn(console, 'error');
      postService.getDeclinedPosts.and.returnValue(throwError(() => new Error(errorMessage)));
      
      component.loadSavedConcepts();
      tick();

      expect(component.declinedPosts).toEqual([]);
      expect(console.error).toHaveBeenCalled();
      expect(postService.getDeclinedPosts).toHaveBeenCalled();
    }));
  });
});