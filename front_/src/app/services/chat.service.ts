import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment';
import { ChatRoom, Message, MessageRequest, TypingIndicator, TypingRequest } from '../models/chat.model';

// Using require directly to avoid TypeScript import issues with Socket.IO v2
// @ts-ignore
const socketIo = require('socket.io-client');

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private baseUrl = environment.apiUrl;
  private socketUrl = 'http://localhost:9099'; // Socket.IO server URL
  private socket: any = null;
  private messageSubjects: Map<number, Subject<Message>> = new Map();
  private typingSubjects: Map<number, Subject<TypingIndicator>> = new Map();
  private connectionStatus = new BehaviorSubject<boolean>(false);
  private reconnecting = false;

  constructor(private http: HttpClient) { }

  // WebSocket Connection Management
  connect(): void {
    if (this.socket && this.socket.connected) {
      return; // Already connected
    }

    if (this.reconnecting) {
      return; // Already trying to reconnect
    }

    try {
      console.log('Connecting to Socket.IO server at:', this.socketUrl);

      // Disconnect any existing socket
      if (this.socket) {
        this.disconnect();
      }

      // Create Socket.IO connection with v2.x compatibility options
      this.socket = socketIo(this.socketUrl, {
        transports: ['websocket'], // Use websocket transport only to avoid polling issues
        forceNew: true,           // Force a new connection
        reconnection: true,       // Enable reconnection
        reconnectionAttempts: 5,  // Number of reconnection attempts
        reconnectionDelay: 1000,  // Time between reconnection attempts (ms)
        timeout: 10000,           // Connection timeout (ms)
        autoConnect: true,        // Connect automatically
        query: {                  // Add client info in query params
          clientId: this.generateClientId()
        }
      });

      // Set up connection event handlers
      this.socket.on('connect', () => {
        console.log('Socket connected successfully');
        this.connectionStatus.next(true);
        this.reconnecting = false;
      });

      this.socket.on('disconnect', () => {
        console.log('Socket disconnected');
        this.connectionStatus.next(false);
      });

      this.socket.on('connect_error', (error: any) => {
        console.error('Socket connection error:', error);
        this.connectionStatus.next(false);

        // Try reconnecting after a timeout
        if (!this.reconnecting) {
          this.reconnecting = true;
          setTimeout(() => {
            console.log('Attempting to reconnect...');
            this.connect();
          }, 5000);
        }
      });
    } catch (error) {
      console.error('Error initializing Socket.IO:', error);
      this.connectionStatus.next(false);
    }
  }

  // Generate a unique client ID
  private generateClientId(): string {
    return 'client_' + new Date().getTime() + '_' + Math.floor(Math.random() * 1000);
  }

  getConnectionStatus(): Observable<boolean> {
    return this.connectionStatus.asObservable();
  }

  disconnect(): void {
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
      this.connectionStatus.next(false);
      this.reconnecting = false;
    }
  }

  joinRoom(roomId: number): void {
    if (this.socket && this.socket.connected) {
      console.log(`Joining room: ${roomId}`);

      // Get user data to add to room join event
      const userStr = localStorage.getItem('user');
      let userData = null;

      if (userStr) {
        try {
          userData = JSON.parse(userStr);
        } catch (e) {
          console.error('Error parsing user data:', e);
        }
      }

      // Join with user data for group chat management
      this.socket.emit('join-room', {
        roomId,
        userId: userData?.id,
        username: userData?.username || 'Anonymous'
      });
    } else {
      console.warn('Cannot join room: socket not connected');
    }
  }

  // Get or create a room for a lost and found item
  getLostAndFoundChatRoom(lostFoundId: number): Observable<ChatRoom> {
    return this.http.get<ChatRoom>(`${this.baseUrl}/chat/room`, {
      params: {
        lostFoundId: lostFoundId.toString()
      }
    });
  }

  subscribeToMessages(roomId: number): Observable<Message> {
    if (!this.messageSubjects.has(roomId)) {
      const subject = new Subject<Message>();
      this.messageSubjects.set(roomId, subject);

      this.subscribeToMessagesInternal(roomId, subject);
    }

    return this.messageSubjects.get(roomId)!.asObservable();
  }

  private subscribeToMessagesInternal(roomId: number, subject: Subject<Message>): void {
    if (this.socket && this.socket.connected) {
      // Join the room
      this.joinRoom(roomId);

      // Listen for messages in this room
      const eventName = `chat.${roomId}`;
      this.socket.on(eventName, (message: Message) => {
        console.log(`Received message in room ${roomId}:`, message);
        subject.next(message);
      });
    } else {
      console.warn(`Cannot subscribe to messages: socket not connected (room ${roomId})`);

      // Try to connect and retry
      this.connect();

      // Set up a listener for connection
      this.connectionStatus.subscribe(connected => {
        if (connected) {
          this.subscribeToMessagesInternal(roomId, subject);
        }
      });
    }
  }

  subscribeToTypingIndicator(roomId: number): Observable<TypingIndicator> {
    if (!this.typingSubjects.has(roomId)) {
      const subject = new Subject<TypingIndicator>();
      this.typingSubjects.set(roomId, subject);

      this.subscribeToTypingIndicatorInternal(roomId, subject);
    }

    return this.typingSubjects.get(roomId)!.asObservable();
  }

  private subscribeToTypingIndicatorInternal(roomId: number, subject: Subject<TypingIndicator>): void {
    if (this.socket && this.socket.connected) {
      // Listen for typing indicators in this room
      const eventName = `chat.${roomId}.typing`;
      this.socket.on(eventName, (typingData: TypingIndicator) => {
        subject.next(typingData);
      });
    }
  }

  // Send messages via WebSocket
  sendMessage(request: MessageRequest): Observable<void> {
    const observable = new Subject<void>();

    try {
      if (this.socket && this.socket.connected) {
        console.log('Sending message:', request);
        this.socket.emit('send-message', request, () => {
          // Callback when the server acknowledges receipt
          observable.complete();
        });
      } else {
        console.warn('Cannot send message: socket not connected');
        observable.error(new Error('Socket not connected'));
      }
    } catch (error) {
      console.error('Error sending message:', error);
      observable.error(error);
    }

    return observable.asObservable();
  }

  sendTypingIndicator(request: TypingRequest): void {
    try {
      if (this.socket && this.socket.connected) {
        // For Socket.IO v2, we need to ensure data is sent in the correct format
        const payload = {
          roomId: request.roomId,
          userId: String(request.userId), // Convert to string for compatibility with socket server
          username: request.username,
          typing: request.typing
        };

        this.socket.emit('typing-indicator', payload);
      } else {
        console.warn('Cannot send typing indicator: socket not connected');
      }
    } catch (error) {
      console.error('Failed to send typing indicator', error);
    }
  }
}
