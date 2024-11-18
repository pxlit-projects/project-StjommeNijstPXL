import { Component, OnInit } from '@angular/core';
import { PostService } from '../services/posts.service';
import { Post } from '../models/post.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { PostFilterComponent } from '../post-filter/post-filter.component' // Import the filter component

@Component({
  selector: 'app-posts',
  standalone: true,
  imports: [CommonModule, RouterModule, PostFilterComponent], // Include the filter component here
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css'],
})
export class PostsComponent implements OnInit {
  posts: Post[] = [];
  isRedacteur: boolean = false;

  // Define variables for filter parameters
  startDate: string | null = null;
  endDate: string | null = null;
  author: string | null = null;
  keyword: string | null = null;

  constructor(private postService: PostService, private authservice: AuthService) {}

  ngOnInit(): void {
    this.loadPosts(); // Load posts initially
    this.isRedacteur = this.authservice.isRedacteur(); // Check if the user is a 'Redacteur'
  }

  // Method to load posts (with optional filters)
  loadPosts(): void {
    const filter = {
      startDate: this.startDate,
      endDate: this.endDate,
      author: this.author,
      keyword: this.keyword,
    };

    this.postService.getFilteredPosts(filter).subscribe({
      next: (response: Post[]) => {
        this.posts = response; // Assign filtered posts
      },
      error: (err: any) => {
        console.error('Error loading posts:', err); // Handle error
      },
    });
  }

  // Method to handle changes in the filter
  onFilterChanged(filter: any): void {
    this.startDate = filter.startDate;
    this.endDate = filter.endDate;
    this.author = filter.author;
    this.keyword = filter.keyword;
    this.loadPosts(); // Load posts based on the filter changes
  }
}
