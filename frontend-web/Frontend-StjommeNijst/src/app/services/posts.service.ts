// src/app/services/post.service.ts
import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
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

  getPostById(id: number) {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }
  
  updatePost(id: number, post: Post) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-User-Role': this.authservice.getUserRole(),
    });
  
    return this.http.put<Post>(`${this.apiUrl}/${id}`, post, { headers });
  }

  // Create a new post (optional based on your need)
  createPost(post: any) {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'X-User-Role': this.authservice.getUserRole(), // Zorg dat de rol correct is ingesteld
    });

    return this.http.post(this.apiUrl, post, { headers });
  }

  getFilteredPosts(filter: any): Observable<Post[]> {
    let params = new HttpParams();

    // Format the dates properly
    if (filter.startDate) {
      params = params.set('startDate', this.formatDate(filter.startDate));
    }
    if (filter.endDate) {
      params = params.set('endDate', this.formatDate(filter.endDate));
    }
    if (filter.author) {
      params = params.set('author', filter.author);
    }
    if (filter.keyword) {
      params = params.set('keyword', filter.keyword);
    }

    return this.http.get<Post[]>(`${this.apiUrl}/filter`, { params });
  }

  // Helperfunctie om de datum te formatteren als yyyy-MM-dd%20HH:mm:ss
  private formatDate(date: string): string {
    const formattedDate = new Date(date);
    const year = formattedDate.getFullYear();
    const month = String(formattedDate.getMonth() + 1).padStart(2, '0');
    const day = String(formattedDate.getDate()).padStart(2, '0');
    const hours = String(formattedDate.getHours()).padStart(2, '0'); // 24-uurs formaat
    const minutes = String(formattedDate.getMinutes()).padStart(2, '0');
    const seconds = String(formattedDate.getSeconds()).padStart(2, '0');

    // Format de datum als yyyy-MM-dd%20HH:mm:ss (24-uurs klok)
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }
}