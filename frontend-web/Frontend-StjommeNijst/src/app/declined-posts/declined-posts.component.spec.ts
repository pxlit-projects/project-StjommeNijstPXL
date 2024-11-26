import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeclinedPostsComponent } from './declined-posts.component';

describe('DeclinedPostsComponent', () => {
  let component: DeclinedPostsComponent;
  let fixture: ComponentFixture<DeclinedPostsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeclinedPostsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeclinedPostsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
