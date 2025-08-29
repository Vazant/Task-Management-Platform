import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject, timer } from 'rxjs';
import { filter, map, retryWhen, switchMap, takeUntil } from 'rxjs/operators';
import { Notification } from '../models/notification.model';

export interface WebSocketMessage {
  type: 'notification' | 'ping' | 'pong' | 'error';
  data?: any;
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket?: WebSocket;
  private reconnectAttempts = 0;
  private readonly maxReconnectAttempts = 5;
  private readonly reconnectDelay = 1000;
  private readonly destroy$ = new Subject<void>();

  private readonly connectionStatus$ = new BehaviorSubject<'connected' | 'disconnected' | 'connecting'>('disconnected');
  private readonly messages$ = new Subject<WebSocketMessage>();

  constructor() {}

  // Observable streams
  get connectionStatus(): Observable<'connected' | 'disconnected' | 'connecting'> {
    return this.connectionStatus$.asObservable();
  }

  get messages(): Observable<WebSocketMessage> {
    return this.messages$.asObservable();
  }

  get notifications(): Observable<Notification> {
    return this.messages$.pipe(
      filter(message => message.type === 'notification'),
      map(message => message.data as Notification)
    );
  }

  // Connection management
  connect(url: string): void {
    if (this.socket?.readyState === WebSocket.OPEN) {
      return;
    }

    this.connectionStatus$.next('connecting');

    try {
      this.socket = new WebSocket(url);
      this.setupEventHandlers();
    } catch (error) {
      console.error('Failed to create WebSocket connection:', error);
      this.connectionStatus$.next('disconnected');
      this.scheduleReconnect();
    }
  }

  disconnect(): void {
    this.destroy$.next();
    this.destroy$.complete();

    if (this.socket) {
      this.socket.close();
      this.socket = undefined;
    }

    this.connectionStatus$.next('disconnected');
    this.reconnectAttempts = 0;
  }

  send(message: WebSocketMessage): void {
    if (this.socket?.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(message));
    } else {
      console.warn('WebSocket is not connected. Message not sent:', message);
    }
  }

  // Utility methods
  private setupEventHandlers(): void {
    if (!this.socket) return;

    this.socket.onopen = () => {
      console.log('WebSocket connected');
      this.connectionStatus$.next('connected');
      this.reconnectAttempts = 0;
      this.startHeartbeat();
    };

    this.socket.onmessage = (event) => {
      try {
        const message: WebSocketMessage = JSON.parse(event.data);
        this.messages$.next(message);
      } catch (error) {
        console.error('Failed to parse WebSocket message:', error);
      }
    };

    this.socket.onclose = (event) => {
      console.log('WebSocket disconnected:', event.code, event.reason);
      this.connectionStatus$.next('disconnected');
      
      if (!event.wasClean) {
        this.scheduleReconnect();
      }
    };

    this.socket.onerror = (error) => {
      console.error('WebSocket error:', error);
      this.connectionStatus$.next('disconnected');
    };
  }

  private startHeartbeat(): void {
    // Send ping every 30 seconds
    timer(0, 30000).pipe(
      takeUntil(this.destroy$),
      filter(() => this.socket?.readyState === WebSocket.OPEN)
    ).subscribe(() => {
      this.send({
        type: 'ping',
        timestamp: Date.now()
      });
    });
  }

  private scheduleReconnect(): void {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.error('Max reconnection attempts reached');
      return;
    }

    this.reconnectAttempts++;
    const delay = this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1);

    console.log(`Scheduling reconnection attempt ${this.reconnectAttempts} in ${delay}ms`);

    timer(delay).pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => {
      if (this.socket?.readyState !== WebSocket.OPEN) {
        this.connect(this.socket?.url || 'ws://localhost:8080/ws');
      }
    });
  }

  // Public methods for specific message types
  subscribeToNotifications(userId: string): void {
    this.send({
      type: 'notification',
      data: { action: 'subscribe', userId },
      timestamp: Date.now()
    });
  }

  unsubscribeFromNotifications(userId: string): void {
    this.send({
      type: 'notification',
      data: { action: 'unsubscribe', userId },
      timestamp: Date.now()
    });
  }

  // Connection status helpers
  isConnected(): boolean {
    return this.socket?.readyState === WebSocket.OPEN;
  }

  isConnecting(): boolean {
    return this.socket?.readyState === WebSocket.CONNECTING;
  }

  isDisconnected(): boolean {
    return !this.socket || this.socket.readyState === WebSocket.CLOSED;
  }
}

