import { User } from './user.model';
import { Document } from './document';
export interface Message {
  id: number;
  sender: User;
  receiver: User;
  content: string;
  sentAt: string;
  isRead: boolean;
  documents?: Document[];
}