import { Component, OnInit } from '@angular/core';
import { Post } from '../models/post.model';
import { PostService } from '../services/posts.service';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-not-approved-posts',
  standalone: true,
  imports: [RouterModule, CommonModule, FormsModule],
  templateUrl: './not-approved-posts.component.html',
  styleUrl: './not-approved-posts.component.css'
})
export class NotApprovedPostsComponent implements OnInit {
  notApprovedPosts: Post[] = [];
  activeRejectForm: number | null = null; // ID van de actieve afwijsvorm
  rejectComments: { [key: number]: string } = {}; // Opslag voor afwijzingscommentaren per post

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

  // Toon/hide afwijzingsformulier
  toggleRejectForm(postId: number): void {
    this.activeRejectForm = this.activeRejectForm === postId ? null : postId;
  }

  // Annuleer afwijzen
  cancelReject(): void {
    this.activeRejectForm = null;
  }

  // Bevestig afwijzing met commentaar
  confirmReject(postId: number): void {
    const comment = this.rejectComments[postId];
    this.postService.rejectPostWithComment(postId, comment).subscribe({
      next: (response) => {
        console.log(response);
        this.loadNotApproved(); // Refresh de lijst
        this.activeRejectForm = null; // Reset het formulier
      },
      error: (err) => {
        console.error('Fout bij het afwijzen van de post', err);
      }
    });
  }
}
