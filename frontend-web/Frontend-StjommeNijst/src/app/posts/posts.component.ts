import { Component, OnInit } from '@angular/core';
import { PostService } from '../services/posts.service';
import { Post } from '../models/post.model';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { PostFilterComponent } from '../post-filter/post-filter.component' // Import the filter component
import { UserCommentRequest } from '../models/user-comment-request.model';
import { FormsModule } from '@angular/forms';
import { UserCommentResponse } from '../models/user-comment-response.model';

@Component({
  selector: 'app-posts',
  standalone: true,
  imports: [CommonModule, RouterModule, PostFilterComponent, FormsModule], // Include the filter component here
  templateUrl: './posts.component.html',
  styleUrls: ['./posts.component.css'],
})
export class PostsComponent implements OnInit {
  posts: Post[] = [];
  isRedacteur: boolean = false;
  newCommentContent: { [postId: number]: string } = {};
  now = new Date();

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

  toggleComments(postId: number): void {
    const post = this.posts.find((p) => p.id === postId);
    if (!post) return;

    post.showComments = !post.showComments;

    if (post.showComments) {
      this.postService.getCommentsByPost(postId).subscribe({
        next: (comments) => (post.comments = comments),
        error: (err) => console.error(`Fout bij het laden van reacties voor post ${postId}:`, err),
      });
    }
  }

  addComment(postId: number): void {
    const content = this.newCommentContent[postId];
    if (!content) return;

    const pad = (num: number) => num < 10 ? '0' + num : num;
    const formattedDate = `${this.now.getFullYear()}-${pad(this.now.getMonth() + 1)}-${pad(this.now.getDate())} ${pad(this.now.getHours())}:${pad(this.now.getMinutes())}:${pad(this.now.getSeconds())}`;
    

    const newComment: UserCommentRequest = {
      content,
      author: this.authservice.getUserName(),
      createdAt: formattedDate,
      postId,
    };

    this.postService.addCommentToPost(postId, newComment).subscribe({
      next: () => {
        const post = this.posts.find((p) => p.id === postId);
        if (post) {
          post.comments.push({ ...newComment, id: Date.now() });
          this.newCommentContent[postId] = '';
        }
      },
      error: (err) => console.error(`Fout bij het toevoegen van reactie voor post ${postId}:`, err),
    });
  }

  trackById(index: number, item: Post): number {
    return item.id;
  }

  editComment(postId: number, comment: UserCommentResponse): void {
    const updatedContent = prompt("Bewerk je reactie:", comment.content);
    if (updatedContent !== null && updatedContent !== comment.content) {
      const updatedComment: UserCommentRequest = {
        content: updatedContent,
        author: comment.author,
        createdAt: comment.createdAt,
        postId,
      };

      this.postService.updateComment(comment.id, updatedComment).subscribe(response => {
        comment.content = updatedContent; // Update the comment in the UI
      });
    }
  }

  deleteComment(postId: number, commentId: number): void {
    if (confirm('Weet je zeker dat je deze reactie wilt verwijderen?')) {
      this.postService.deleteComment(postId, commentId).subscribe(() => {
        const post = this.posts.find(p => p.id === postId);
        if (post) {
          post.comments = post.comments.filter(c => c.id !== commentId);
        }
      });
    }
  }
}
