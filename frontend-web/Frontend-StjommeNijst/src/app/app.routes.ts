import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { PostsComponent } from './posts/posts.component';
import { CreatePostsComponent } from './create-posts/create-posts.component';  // Je maakt deze later

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'posts', component: PostsComponent },
  { path: 'create-post', component: CreatePostsComponent },  // Route voor het aanmaken van een post
];
