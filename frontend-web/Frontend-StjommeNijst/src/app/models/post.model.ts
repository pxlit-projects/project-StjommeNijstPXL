import { Status } from "./post-status.enum";

export interface Post {
    id: number;
    title: string;
    content: string;
    author: string;
    createdAt: string;
    status: Status;
  }

  
  