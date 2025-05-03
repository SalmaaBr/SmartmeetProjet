import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ChatService } from '../../../services/chat.service';
import { LostAndFoundResponse } from '../../../models/lost-and-found.model';
import { Message, ChatRoom, TypingIndicator } from '../../../models/chat.model';
import { Subject } from 'rxjs';
import { takeUntil, debounceTime, distinctUntilChanged, filter, map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-chat-modal',
  templateUrl: './chat-modal.component.html',
  styleUrls: ['./chat-modal.component.css']
})
export class ChatModalComponent implements OnInit, OnDestroy {
  chatRoom?: ChatRoom;
  messages: Message[] = [];
  newMessageControl = new FormControl('', [Validators.required]);
  loading = false;
  error: string | null = null;
  private destroy$ = new Subject<void>();
  typingTimer?: any;
  userTyping = false;
  otherUserTyping = false;
  otherUsername = '';
  private baseUrl = environment.apiUrl;
  currentUsername = '';

  constructor(
    private chatService: ChatService,
    private http: HttpClient,
    public dialogRef: MatDialogRef<ChatModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      lostFoundItem: LostAndFoundResponse,
      currentUserId: number
    }
  ) { }

  ngOnInit(): void {
    this.currentUsername = this.getUsernameFromStorage();
    this.initializeChatRoom();

    // Setup typing indicator
    this.newMessageControl.valueChanges.pipe(
      takeUntil(this.destroy$),
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(val => {
      if (val && !this.userTyping) {
        this.userTyping = true;
        this.sendTypingIndicator(true);
      } else if (!val && this.userTyping) {
        this.userTyping = false;
        this.sendTypingIndicator(false);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.chatService.disconnect();
  }

  initializeChatRoom(): void {
    this.loading = true;
    const { lostFoundItem } = this.data;

    // Get or create a group chat room for this lost and found item
    this.chatService.getLostAndFoundChatRoom(lostFoundItem.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('Chat room response:', response);
          this.chatRoom = {
            id: response.id,
            user1: response.user1,
            user2: response.user2,
            lostFoundId: response.lostFoundId,
            createdAt: new Date().toISOString(),
            isGroupChat: true
          };
          this.loadMessages();
          this.connectToWebSocket();
        },
        error: (err) => {
          console.error('Failed to initialize chat room:', err);
          this.error = 'Failed to initialize chat room.';
          this.loading = false;
        }
      });
  }

  loadMessages(): void {
    console.log("Chat room for loading messages:", this.chatRoom);
    if (!this.chatRoom) return;

    this.loading = true;
    this.http.get<Message[]>(`${this.baseUrl}/chat/messages/${this.chatRoom.id}`)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (messages) => {
          this.messages = messages;
          this.loading = false;
          this.scrollToBottom();
        },
        error: (err) => {
          this.error = 'Failed to load messages.';
          this.loading = false;
          console.error('Failed to load messages:', err);
        }
      });
  }

  connectToWebSocket(): void {
    if (!this.chatRoom) return;

    // Connect to WebSocket
    this.chatService.connect();

    // Monitor connection status
    this.chatService.getConnectionStatus()
      .pipe(
        takeUntil(this.destroy$),
        filter(connected => connected) // Only proceed when connected
      )
      .subscribe(() => {
        console.log('WebSocket connected, joining room:', this.chatRoom!.id);
        // Join the room when connected
        this.chatService.joinRoom(this.chatRoom!.id);

        // Subscribe to messages and typing indicators
        this.setupSubscriptions();
      });
  }

  setupSubscriptions(): void {
    if (!this.chatRoom) return;

    console.log('Setting up message subscription for room:', this.chatRoom.id);
    // Subscribe to messages for this chat room
    this.chatService.subscribeToMessages(this.chatRoom.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe(message => {
        console.log('Received message:', message);

        // Check if this message is already in our array
        const exists = this.messages.some(m => m.id === message.id);
        if (!exists) {
          this.messages.push(message);
          this.scrollToBottom();
        }
      });

    // Subscribe to typing indicators
    this.chatService.subscribeToTypingIndicator(this.chatRoom.id)
      .pipe(takeUntil(this.destroy$))
      .subscribe((typingEvent: TypingIndicator) => {
        if (typingEvent.userId !== this.data.currentUserId) {
          this.otherUserTyping = typingEvent.typing;
          this.otherUsername = typingEvent.username;
        }
      });
  }

  // Handle form submission and prevent page reload
  onSubmit(event: Event): void {
    event.preventDefault();
    this.sendMessage();
  }

  sendMessage(): void {
    if (!this.chatRoom || !this.newMessageControl.valid) return;

    const content = this.newMessageControl.value || '';
    console.log('Sending message:', content, 'to room:', this.chatRoom.id);

    this.chatService.sendMessage({
      content,
      senderId: this.data.currentUserId.toString(),
      roomId: this.chatRoom.id.toString()
    }).pipe(takeUntil(this.destroy$))
    .subscribe({
      next: () => {
        console.log('Message sent successfully');
        // Don't add the message locally - wait for it to come from the server
        this.newMessageControl.reset();
      },
      error: (err) => {
        console.error('Failed to send message:', err);
      }
    });
  }

  sendTypingIndicator(isTyping: boolean): void {
    if (!this.chatRoom) return;

    this.chatService.sendTypingIndicator({
      roomId: this.chatRoom.id.toString(),
      userId: this.data.currentUserId,
      username: this.getUsernameFromStorage(),
      typing: isTyping
    });
  }

  getUsernameFromStorage(): string {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      return user.username || 'Unknown';
    }
    return 'Unknown';
  }

  scrollToBottom(): void {
    setTimeout(() => {
      const chatContainer = document.querySelector('.chat-messages');
      if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
      }
    }, 100);
  }

  isCurrentUserMessage(message: Message): boolean {
    console.log(message.sender);
    console.log(message.senderId === this.data.currentUserId || message.sender?.UserID === this.data.currentUserId);

    return message.senderId === this.data.currentUserId || message.sender?.userID === this.data.currentUserId;
  }

  close(): void {
    this.dialogRef.close();
  }
}