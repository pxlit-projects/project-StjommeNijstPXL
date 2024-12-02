import { Status } from "./post-status.enum";
import { UserCommentResponse } from "./user-comment-response.model";

export interface Post {
    id: number;
    title: string;
    content: string;
    author: string;
    createdAt: string;
    status: Status;
    comments: UserCommentResponse[];
    showComments: boolean;
  }


  
  