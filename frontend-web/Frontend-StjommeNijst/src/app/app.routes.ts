import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { PostsComponent } from './posts/posts.component';
import { CreatePostsComponent } from './create-posts/create-posts.component';  // Je maakt deze later
import { EditPostComponent } from './edit-post-component/edit-post-component.component';
import { ConceptPostsComponent } from './concept-posts/concept-posts.component';
import { NotApprovedPostsComponent } from './not-approved-posts/not-approved-posts.component';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'posts', component: PostsComponent },
  { path: 'create-post', component: CreatePostsComponent },
  { path: 'edit-post/:id', component: EditPostComponent },
  { path: 'concept-posts', component: ConceptPostsComponent },
  { path: 'not-approved', component: NotApprovedPostsComponent }
];
