import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Post } from '../models/post.model'; 
import { AuthService } from './auth.service';
import { Status } from '../models/post-status.enum';
import { PostWithComment } from '../models/postWithComment.model';
import { UserCommentRequest } from '../models/user-comment-request.model';
import { UserCommentResponse } from '../models/user-comment-response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  
  private apiUrl = environment.apiBaseUrl;
  private apiUrlReview = environment.apiReviewBaseUrl;

  constructor(private http: HttpClient = inject(HttpClient), private authservice: AuthService) {}

  private getHeaders() {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'X-User-Role': this.authservice.getUserRole(),
    });
  }

  updateComment(commentId: number, comment: UserCommentRequest): Observable<UserCommentResponse> {
    return this.http.put<UserCommentResponse>(`${this.apiUrl}/comments/${commentId}`, comment, {
      headers: this.getHeaders()
    });
  }

  deleteComment(postId: number, commentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${postId}/comments/${commentId}`, {
      headers: this.getHeaders()
    });
  }

  addCommentToPost(postId: number, comment: UserCommentRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${postId}/comments`, comment, {
      headers: this.getHeaders()
    });
  }

  getCommentsByPost(postId: number): Observable<UserCommentResponse[]> {
    return this.http.get<UserCommentResponse[]>(`${this.apiUrl}/${postId}/comments`, {
      headers: this.getHeaders()
    });
  }

  getNotApprovedPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrlReview}/pending`, {
      headers: this.getHeaders()
    });
  }

  getDeclinedPosts(): Observable<PostWithComment[]>{
    return this.http.get<PostWithComment[]>(`${this.apiUrl}/declined`, {
      headers: this.getHeaders()
    });
  }

  approvePost(postId: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrlReview}/${postId}/approve`, null, {
      headers: this.getHeaders()
    });
  }

  rejectPostWithComment(postId: number, comment: string): Observable<string> {
    return this.http.post<string>(`${this.apiUrlReview}/${postId}/reject`, { comment }, {
      headers: this.getHeaders()
    });
  }

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.apiUrl, {
      headers: this.getHeaders()
    });
  }

  getPostById(id: number) {
    return this.http.get<Post>(`${this.apiUrl}/${id}`, {
      headers: this.getHeaders()
    });
  }
  
  updatePost(id: number, post: Post) {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, post, {
      headers: this.getHeaders()
    });
  }

  getSavedConcepts(): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/concepts`, {
      headers: this.getHeaders()
    });
  }

  createPost(post: any) {
    return this.http.post(this.apiUrl, post, {
      headers: this.getHeaders()
    });
  }

  getFilteredPosts(filter: any): Observable<Post[]> {
    let params = new HttpParams();

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

    params = params.set('status', Status.GOEDGEKEURD)

    return this.http.get<Post[]>(`${this.apiUrl}/filter`, {
      headers: this.getHeaders(),
      params
    });
  }

  private formatDate(date: string): string {
    const formattedDate = new Date(date);
    const year = formattedDate.getFullYear();
    const month = String(formattedDate.getMonth() + 1).padStart(2, '0');
    const day = String(formattedDate.getDate()).padStart(2, '0');
    const hours = String(formattedDate.getHours()).padStart(2, '0');
    const minutes = String(formattedDate.getMinutes()).padStart(2, '0');
    const seconds = String(formattedDate.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  }
}
