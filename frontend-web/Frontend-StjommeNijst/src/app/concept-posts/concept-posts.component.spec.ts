import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ConceptPostsComponent } from './concept-posts.component';
import { PostService } from '../services/posts.service';
import { RouterModule } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Post } from '../models/post.model';
import { Status } from '../models/post-status.enum';
import { ActivatedRoute } from '@angular/router';


describe('ConceptPostsComponent', () => {
  let component: ConceptPostsComponent;
  let fixture: ComponentFixture<ConceptPostsComponent>;
  let postService: jasmine.SpyObj<PostService>;

  const mockConceptPosts: Post[] = [
    {
      id: 1,
      title: 'Draft Post 1',
      content: 'Content 1',
      author: 'Author 1',
      createdAt: '2024-01-01',
      status: Status.CONCEPT,
      comments: [],
      showComments: false
    },
    {
      id: 2,
      title: 'Draft Post 2',
      content: 'Content 2',
      author: 'Author 2',
      createdAt: '2024-01-02',
      status: Status.CONCEPT,
      comments: [],
      showComments: false
    }
  ];

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', ['getSavedConcepts']);
    
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
    fixture = TestBed.createComponent(ConceptPostsComponent);
    component = fixture.componentInstance;
  });

  describe('Component Initialization', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with empty concept posts array', () => {
      expect(component.conceptPosts).toEqual([]);
    });
  });

  describe('Loading Concept Posts', () => {
    it('should load concept posts on init', fakeAsync(() => {
      postService.getSavedConcepts.and.returnValue(of(mockConceptPosts));
      
      fixture.detectChanges(); // Triggers ngOnInit
      tick();

      expect(component.conceptPosts).toEqual(mockConceptPosts);
      expect(postService.getSavedConcepts).toHaveBeenCalled();
    }));

    it('should handle empty response', fakeAsync(() => {
      postService.getSavedConcepts.and.returnValue(of([]));
      
      fixture.detectChanges();
      tick();

      expect(component.conceptPosts).toEqual([]);
      expect(postService.getSavedConcepts).toHaveBeenCalled();
    }));

    it('should handle error when loading concept posts', fakeAsync(() => {
      const errorMessage = 'Error loading concepts';
      spyOn(console, 'error');
      postService.getSavedConcepts.and.returnValue(throwError(() => new Error(errorMessage)));
      
      fixture.detectChanges();
      tick();

      expect(component.conceptPosts).toEqual([]);
      expect(console.error).toHaveBeenCalled();
      expect(postService.getSavedConcepts).toHaveBeenCalled();
    }));
  });

  describe('Manual Load', () => {
    it('should allow manual loading of concept posts', fakeAsync(() => {
      postService.getSavedConcepts.and.returnValue(of(mockConceptPosts));
      
      component.loadSavedConcepts();
      tick();

      expect(component.conceptPosts).toEqual(mockConceptPosts);
      expect(postService.getSavedConcepts).toHaveBeenCalled();
    }));

    it('should handle error during manual load', fakeAsync(() => {
      const errorMessage = 'Error loading concepts';
      spyOn(console, 'error');
      postService.getSavedConcepts.and.returnValue(throwError(() => new Error(errorMessage)));
      
      component.loadSavedConcepts();
      tick();

      expect(component.conceptPosts).toEqual([]);
      expect(console.error).toHaveBeenCalled();
      expect(postService.getSavedConcepts).toHaveBeenCalled();
    }));
  });

  describe('Error Handling', () => {
    it('should log specific error message', fakeAsync(() => {
      const specificError = new Error('Network timeout');
      spyOn(console, 'error');
      postService.getSavedConcepts.and.returnValue(throwError(() => specificError));
      
      component.loadSavedConcepts();
      tick();

      expect(console.error).toHaveBeenCalledWith(
        'Er is een fout opgetreden bij het ophalen van concepten',
        specificError
      );
    }));
  });
});