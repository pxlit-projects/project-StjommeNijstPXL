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
    status: Status.NIET_GOEDGEKEURD
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

  // Ophalen van post details op basis van postId
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

  // Normaal opslaan van post (bijvoorbeeld goedkeuren)
  savePost(): void {
    const formattedDate = `${this.now.getFullYear()}-${this.now.getMonth() + 1}-${this.now.getDate()} ${this.now.getHours()}:${this.now.getMinutes()}:${this.now.getSeconds()}`;
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

  // Opslaan als concept (draft)
  saveAsDraft(): void {
    if (this.post.title && this.post.content && this.post.author) {
      const formattedDate = `${this.now.getFullYear()}-${this.now.getMonth() + 1}-${this.now.getDate()} ${this.now.getHours()}:${this.now.getMinutes()}:${this.now.getSeconds()}`;
      // Stel de status in als CONCEPT (draft)
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
