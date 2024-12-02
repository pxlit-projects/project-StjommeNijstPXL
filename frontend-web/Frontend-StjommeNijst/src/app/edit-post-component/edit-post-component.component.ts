import { Component, OnInit } from '@angular/core';
import { Post } from '../models/post.model';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '../services/posts.service';
import { Status } from '../models/post-status.enum';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-post-component',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './edit-post-component.component.html',
  styleUrls: ['./edit-post-component.component.css']
})
export class EditPostComponent implements OnInit {
  now = new Date();
  postId: number = 0;
  post: Post = {
    id: 0,
    title: '',
    content: '',
    author: '',
    createdAt: '',
    status: Status.NIET_GOEDGEKEURD,
    showComments: false,
    comments: [],
  };

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.postId = Number(this.route.snapshot.paramMap.get('id'));
    this.fetchPostDetails();
  }

  fetchPostDetails(): void {
    this.postService.getPostById(this.postId).subscribe({
      next: (data) => {
        this.post = data;
      },
      error: (err) => {
        console.error('Fout bij het ophalen van de post', err);
      }
    });
  }

  savePost(): void {
    const pad = (num: number) => num < 10 ? '0' + num : num;
    const formattedDate = `${this.now.getFullYear()}-${pad(this.now.getMonth() + 1)}-${pad(this.now.getDate())} ${pad(this.now.getHours())}:${pad(this.now.getMinutes())}:${pad(this.now.getSeconds())}`;
    this.post.status = Status.WACHTEND;
    this.post.createdAt = formattedDate;
    this.postService.updatePost(this.postId, this.post).subscribe({
      next: () => {
        alert('Post succesvol bijgewerkt!');
        this.router.navigate(['/posts']);
      },
      error: (err) => {
        console.error('Fout bij het bijwerken van de post', err);
      }
    });
  }

  saveAsDraft(): void {
    if (this.post.title && this.post.content && this.post.author) {
      const pad = (num: number) => num < 10 ? '0' + num : num;
      const formattedDate = `${this.now.getFullYear()}-${pad(this.now.getMonth() + 1)}-${pad(this.now.getDate())} ${pad(this.now.getHours())}:${pad(this.now.getMinutes())}:${pad(this.now.getSeconds())}`;
      this.post.status = Status.CONCEPT;
      this.post.createdAt = formattedDate;

      this.postService.updatePost(this.postId, this.post).subscribe({
        next: () => {
          alert('Post succesvol opgeslagen als concept!');
          this.router.navigate(['/posts']);
        },
        error: (err) => {
          console.error('Er is een fout opgetreden bij het opslaan als concept', err);
        }
      });
    } else {
      alert('Vul alle velden in!');
    }
  }
}
