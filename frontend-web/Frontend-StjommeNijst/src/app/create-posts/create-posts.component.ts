import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { PostService } from '../services/posts.service';  // Importeer de PostService om een post aan te maken
import { AuthService } from '../services/auth.service';  // Importeer de AuthService om de gebruiker te controleren
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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

  createPost(): void {
    if (this.title && this.content && this.author) {
      const formattedDate = `${this.now.getFullYear()}-${this.now.getMonth() + 1}-${this.now.getDate()} ${this.now.getHours()}:${this.now.getMinutes()}:${this.now.getSeconds()}`;
      const newPost = {
        title: this.title,
        content: this.content,
        author: this.author,
        createdAt: formattedDate,
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
