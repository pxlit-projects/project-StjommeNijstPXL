// src/app/services/post.service.ts
import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../models/post.model'; // Import the PostResponse model
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private apiUrl = 'http://localhost:8085/api/posts';  // Adjust this URL to your backend

  constructor(private http: HttpClient = inject(HttpClient), private authservice: AuthService) {}

  // Fetch all posts from the backend
  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl);
  }

  // Create a new post (optional based on your need)
  createPost(post: any) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-User-Role': this.authservice.getUserRole(), // Zorg dat de rol correct is ingesteld
    });

    return this.http.post(this.apiUrl, post, { headers });
  }
}
