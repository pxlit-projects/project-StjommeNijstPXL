import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConceptPostsComponent } from './concept-posts.component';

describe('ConceptPostsComponent', () => {
  let component: ConceptPostsComponent;
  let fixture: ComponentFixture<ConceptPostsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConceptPostsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConceptPostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
