import { Component, OnInit } from '@angular/core';
import { Post } from '../models/post.model';
import { PostService } from '../services/posts.service';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-not-approved-posts',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './not-approved-posts.component.html',
  styleUrl: './not-approved-posts.component.css'
})
export class NotApprovedPostsComponent implements OnInit {
  notApprovedPosts: Post[] = [];

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadNotApproved();
  }


  // Haal alle pending posts op
  loadNotApproved(): void {
    this.postService.getNotApprovedPosts().subscribe({
      next: (data) => {
        this.notApprovedPosts = data;
      },
      error: (err) => {
        console.error('Er is een fout opgetreden bij het ophalen van posts', err);
      }
    });
  }

  // Keur een post goed
  approvePost(postId: number): void {
    this.postService.approvePost(postId).subscribe({
      next: (response) => {
        console.log(response);
        this.loadNotApproved(); // Refresh de lijst
      },
      error: (err) => {
        console.error('Fout bij het goedkeuren van de post', err);
      }
    });
  }

  // Wijs een post af
  rejectPost(postId: number): void {
    this.postService.rejectPost(postId).subscribe({
      next: (response) => {
        console.log(response);
        this.loadNotApproved(); // Refresh de lijst
      },
      error: (err) => {
        console.error('Fout bij het afwijzen van de post', err);
      }
    });
  }
}
