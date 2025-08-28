import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NetworkStatusService {
  private readonly online$ = new BehaviorSubject<boolean>(navigator.onLine);
  private readonly connectionType$ = new BehaviorSubject<string>('unknown');

  constructor() {
    this.initializeNetworkMonitoring();
  }

  private initializeNetworkMonitoring(): void {
    window.addEventListener('online', () => {
      this.online$.next(true);
      this.updateConnectionType();
    });

    window.addEventListener('offline', () => {
      this.online$.next(false);
      this.connectionType$.next('offline');
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
      this.connectionType$.next(connection.effectiveType || 'unknown');
    }
  }

  get isOnline$(): Observable<boolean> {
    return this.online$.asObservable();
  }

  get isOnline(): boolean {
    return this.online$.value;
  }

  get connectionType$(): Observable<string> {
    return this.connectionType$.asObservable();
  }

  get connectionType(): string {
    return this.connectionType$.value;
  }
}
