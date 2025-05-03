export enum LostAndFoundType {
  LOST = 'LOST',
  FOUND = 'FOUND'
}

export interface LostAndFoundRequest {
  title: string;
  description: string;
  type: LostAndFoundType;
  imageUrl?: string;
  eventId: number;
}

export interface LostAndFoundResponse {
  id: number;
  title: string;
  description: string;
  type: LostAndFoundType;
  createdAt: string;
  imageUrl?: string;
  eventId: number;
  eventTitle: string;
  creatorId: number;
  creatorUsername: string;
}

export interface Notification {
  title: string;
  message: string;
  timestamp: string;
  relatedItemId: number;
  type: string;
  read?: boolean;
}
