import { Document } from './document';
import { Message } from './message.model';

export interface MessageAttachment {
  id: number;
  message: Message;
  document: Document;
}