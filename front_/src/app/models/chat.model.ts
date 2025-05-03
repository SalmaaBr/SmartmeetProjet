export interface Message {
  id: number;
  content: string;
  timestamp: string;
  senderId: number;
  senderUsername: string;
  receiverId?: number;
  read: boolean;
  sender?: any;
}

export interface ChatParticipant {
  userID: number;
  username: string;
}

export interface ChatRoom {
  id: number;
  user1: {
    userID: number;
    username: string;
  };
  user2: {
    userID: number;
    username: string;
  };
  participants?: ChatParticipant[];
  lostFoundId: number;
  createdAt: string;
  isGroupChat?: boolean;
}

export interface TypingIndicator {
  userId: number;
  username: string;
  typing: boolean;
}

export interface MessageRequest {
  content: string;
  senderId: string;
  roomId: string;
}

export interface TypingRequest {
  roomId: string;
  userId: number;
  username: string;
  typing: boolean;
}
