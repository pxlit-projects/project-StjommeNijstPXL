import { Component } from '@angular/core';
import { Post } from '../models/post.model';
import { PostService } from '../services/posts.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-not-approved-posts',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './not-approved-posts.component.html',
  styleUrl: './not-approved-posts.component.css'
})
export class NotApprovedPostsComponent {
  notApporvedPosts: Post[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadNotApproved();
  }

  loadNotApproved(): void {
    this.postService.getNotApprovedPosts().subscribe({
      next: (data) => {
        this.notApporvedPosts = data;
      },
      error: (err) => {
        console.error('Er is een fout opgetreden bij het ophalen van concepten', err);
      }
    });
  }
}
