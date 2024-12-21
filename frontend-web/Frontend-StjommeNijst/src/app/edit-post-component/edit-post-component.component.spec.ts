import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing'; // Voeg HttpClientTestingModule toe
import { ActivatedRoute } from '@angular/router'; // Mock ActivatedRoute indien nodig
import { EditPostComponent } from './edit-post-component.component'; // Je standalone component
import { PostService } from '../services/posts.service'; // Importeer je PostService

describe('EditPostComponent', () => {
  let component: EditPostComponent;
  let fixture: ComponentFixture<EditPostComponent>;
  let postService: PostService;

  beforeEach(async () => {
    // Mock ActivatedRoute om snapshot parameters te simuleren
    const mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jasmine.createSpy().and.returnValue('123'), // Mock de 'get' methode om een waarde te retourneren
        },
      },
    };

    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule, // Zorg ervoor dat HttpClientTestingModule wordt geïmporteerd
        EditPostComponent, // Standalone component in imports
      ],
      providers: [
        PostService,
        { provide: ActivatedRoute, useValue: mockActivatedRoute }, // Mock de ActivatedRoute
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(EditPostComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService); // Injecteer PostService
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
