import { Component, OnInit } from '@angular/core';
import { PostService } from '../services/posts.service';
import { Post } from '../models/post.model';

@Component({
  selector: 'app-concept-posts',
  standalone: true,
  imports: [],
  templateUrl: './concept-posts.component.html',
  styleUrl: './concept-posts.component.css'
})
export class ConceptPostsComponent implements OnInit {
  conceptPosts: Post[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadSavedConcepts();
  }

  loadSavedConcepts(): void {
    this.postService.getSavedConcepts().subscribe({
      next: (data) => {
        this.conceptPosts = data;
      },
      error: (err) => {
        console.error('Er is een fout opgetreden bij het ophalen van concepten', err);
      }
    });
  }
}
