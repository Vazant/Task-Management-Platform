import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NetworkStatusService {
  private readonly isOnlineSubject = new BehaviorSubject<boolean>(navigator.onLine);
  private readonly connectionTypeSubject = new BehaviorSubject<string>('unknown');

  constructor() {
    this.initializeNetworkMonitoring();
  }

  private initializeNetworkMonitoring(): void {
    window.addEventListener('online', () => {
      this.isOnlineSubject.next(true);
      this.updateConnectionType();
    });

    window.addEventListener('offline', () => {
      this.isOnlineSubject.next(false);
      this.connectionTypeSubject.next('offline');
    });

    // Monitor connection changes
    if ('connection' in navigator) {
      const connection = (navigator as any).connection;
      connection.addEventListener('change', () => {
        this.updateConnectionType();
      });
      this.updateConnectionType();
    }
  }

  private updateConnectionType(): void {
    if ('connection' in navigator) {
      const connection = (navigator as any).connection;
      this.connectionTypeSubject.next(connection.effectiveType || 'unknown');
    }
  }

  get isOnline$(): Observable<boolean> {
    return this.isOnlineSubject.asObservable();
  }

  get isOnline(): boolean {
    return this.isOnlineSubject.value;
  }

  get connectionType$(): Observable<string> {
    return this.connectionTypeSubject.asObservable();
  }

  get connectionType(): string {
    return this.connectionTypeSubject.value;
  }
}
