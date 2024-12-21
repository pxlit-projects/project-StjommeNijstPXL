import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing'; // Importeer HttpClientTestingModule
import { ActivatedRoute } from '@angular/router'; // Import ActivatedRoute for mocking
import { of } from 'rxjs'; // Gebruik 'of' om een mock observable te maken
import { NotApprovedPostsComponent } from './not-approved-posts.component'; // Je standalone component
import { PostService } from '../services/posts.service'; // PostService importeren

describe('NotApprovedPostsComponent', () => {
  let component: NotApprovedPostsComponent;
  let fixture: ComponentFixture<NotApprovedPostsComponent>;
  let postService: PostService;

  beforeEach(async () => {
    // Mock ActivatedRoute
    const mockActivatedRoute = {
      snapshot: { params: {} }, // Mock eventuele routeparameters
    };

    // Configureren van TestBed
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule, // Zorg ervoor dat HttpClientTestingModule wordt geïmporteerd
        NotApprovedPostsComponent // Standalone component toevoegen aan imports
      ],
      providers: [
        PostService,
        { provide: ActivatedRoute, useValue: mockActivatedRoute }, // Mock de ActivatedRoute
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(NotApprovedPostsComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService); // Injecteer PostService
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
