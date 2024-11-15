// src/app/posts/posts.component.ts
import { Component, OnInit } from '@angular/core';
import { PostService } from '../services/posts.service';
import { Post } from '../models/post.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-posts',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css'],
})
export class PostsComponent implements OnInit {
  posts: Post[] = [];
  isRedacteur: boolean = false;

  constructor(private postService: PostService, private authservice: AuthService) {}

  ngOnInit(): void {
    this.loadPosts();
    this.isRedacteur = this.authservice.isRedacteur();
  }

  loadPosts(): void {
    this.postService.getAllPosts().subscribe({
      next: (response: Post[]) => {
        this.posts = response;
      },
      error: (err: any) => {
        console.error('Error loading posts:', err);
      },
    });
  }
}
