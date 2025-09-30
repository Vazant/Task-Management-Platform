import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, fromEvent, merge } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class NetworkStatusService {
  private readonly isOnlineSubject = new BehaviorSubject<boolean>(navigator.onLine);

  constructor() {
    this.initializeNetworkStatus();
  }

  get isOnline$(): Observable<boolean> {
    return this.isOnlineSubject.asObservable();
  }

  get isOnline(): boolean {
    return this.isOnlineSubject.value;
  }

  private initializeNetworkStatus(): void {
    const online$ = fromEvent(window, 'online').pipe(map(() => true));
    const offline$ = fromEvent(window, 'offline').pipe(map(() => false));

    merge(online$, offline$)
      .pipe(startWith(navigator.onLine))
      .subscribe(isOnline => {
        this.isOnlineSubject.next(isOnline);
      });
  }
}