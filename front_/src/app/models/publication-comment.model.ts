import { User } from "./user.model";

export class PublicationComment {
  commentId!: number;
  publicationId!: number;
  userId!: number;
  username?: string;
  user?: User;
  content!: string;
  createdAt!: string;
  updatedAt?: string;
  isEditing?: boolean = false;
}
