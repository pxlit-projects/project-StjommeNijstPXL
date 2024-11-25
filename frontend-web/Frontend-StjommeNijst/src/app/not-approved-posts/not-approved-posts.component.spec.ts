import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotApprovedPostsComponent } from './not-approved-posts.component';

describe('NotApprovedPostsComponent', () => {
  let component: NotApprovedPostsComponent;
  let fixture: ComponentFixture<NotApprovedPostsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotApprovedPostsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotApprovedPostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
