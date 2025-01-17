import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { PostService } from '../services/posts.service';  // Importeer de PostService om een post aan te maken
import { AuthService } from '../services/auth.service';  // Importeer de AuthService om de gebruiker te controleren
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Status } from '../models/post-status.enum';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './create-posts.component.html',
  styleUrls: ['./create-posts.component.css']
})
export class CreatePostsComponent {
  title: string = '';
  content: string = '';
  author: string = '';
  now = new Date();

  constructor(private postService: PostService, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.author = this.authService.getUserName();
  }

  goToDeclinedPosts(): void {
    this.router.navigate(['/declined-posts']);
  }

  goToSavedPosts(): void {
    this.router.navigate(['/concept-posts']);
  }

  saveAsDraft(): void {
    if (this.title && this.content && this.author) {
      const pad = (num: number) => num < 10 ? '0' + num : num;
      const formattedDate = `${this.now.getFullYear()}-${pad(this.now.getMonth() + 1)}-${pad(this.now.getDate())} ${pad(this.now.getHours())}:${pad(this.now.getMinutes())}:${pad(this.now.getSeconds())}`;
    
      const newPost = {
        title: this.title,
        content: this.content,
        author: this.author,
        createdAt: formattedDate,
        status: Status.CONCEPT
      };
  
      this.postService.createPost(newPost).subscribe({
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
  

  createPost(): void {
    if (this.title && this.content && this.author) {
      const pad = (num: number) => num < 10 ? '0' + num : num;
      const formattedDate = `${this.now.getFullYear()}-${pad(this.now.getMonth() + 1)}-${pad(this.now.getDate())} ${pad(this.now.getHours())}:${pad(this.now.getMinutes())}:${pad(this.now.getSeconds())}`;
    
      const newPost = {
        title: this.title,
        content: this.content,
        author: this.author,
        createdAt: formattedDate,
        status: Status.WACHTEND
      };

      this.postService.createPost(newPost).subscribe({
        next: () => {
          this.router.navigate(['/posts']);
        },
        error: (err) => {
          console.error('Er is een fout opgetreden bij het aanmaken van de post', err);
        }
      });
    } else {
      alert('Vul alle velden in!');
    }
  }
}

