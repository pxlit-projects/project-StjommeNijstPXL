import { Component, OnInit } from '@angular/core';
import { PostWithComment } from '../models/postWithComment.model';
import { PostService } from '../services/posts.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-declined-posts',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './declined-posts.component.html',
  styleUrl: './declined-posts.component.css'
})
export class DeclinedPostsComponent implements OnInit  {
  declinedPosts: PostWithComment[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadSavedConcepts();
  }

  loadSavedConcepts(): void {
    this.postService.getDeclinedPosts().subscribe({
      next: (data) => {
        this.declinedPosts = data;
      },
      error: (err) => {
        console.error('Er is een fout opgetreden bij het ophalen van concepten', err);
      }
    });
  }
}
