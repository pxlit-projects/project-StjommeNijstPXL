import { Status } from "./post-status.enum";

export interface PostWithComment {
    id: number;
    title: string;
    content: string;
    author: string;
    createdAt: string;
    status: Status;
    comment: string;
  }

  
  