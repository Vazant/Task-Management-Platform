// Примеры кода для ключевых фич Task Management Platform
// Разработано согласно рабочему плану на сентябрь 2025

// ============================================================================
// 1. NgRx Entity Adapter для Tasks State Management
// ============================================================================

import { createEntityAdapter, EntityState } from '@ngrx/entity';
import { createReducer, on } from '@ngrx/store';
import { Task } from '../models/task.model';
import * as TaskActions from './task.actions';

export interface TasksState extends EntityState<Task> {
  loading: boolean;
  error: string | null;
  filters: {
    status: string;
    priority: string;
    assignee: string;
  };
  sortBy: string;
}

export const tasksAdapter = createEntityAdapter<Task>({
  selectId: (task: Task) => task.id,
  sortComparer: (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
});

export const initialState = tasksAdapter.getInitialState({
  loading: false,
  error: null,
  filters: {
    status: 'all',
    priority: 'all',
    assignee: 'all'
  },
  sortBy: 'createdAt'
});

export const tasksReducer = createReducer(
  initialState,
  on(TaskActions.loadTasks, (state) => ({
    ...state,
    loading: true,
    error: null
  })),
  on(TaskActions.loadTasksSuccess, (state, { tasks }) =>
    tasksAdapter.setAll(tasks, { ...state, loading: false })
  ),
  on(TaskActions.loadTasksFailure, (state, { error }) =>
    tasksAdapter.removeAll({ ...state, loading: false, error })
  ),
  on(TaskActions.addTask, (state, { task }) =>
    tasksAdapter.addOne(task, { ...state, loading: true })
  ),
  on(TaskActions.updateTask, (state, { update }) =>
    tasksAdapter.updateOne(update, { ...state, loading: true })
  ),
  on(TaskActions.deleteTask, (state, { id }) =>
    tasksAdapter.removeOne(id, { ...state, loading: true })
  ),
  on(TaskActions.setTaskFilters, (state, { filters }) => ({
    ...state,
    filters: { ...state.filters, ...filters }
  })),
  on(TaskActions.setTaskSortBy, (state, { sortBy }) => ({
    ...state,
    sortBy
  }))
);

// ============================================================================
// 2. Task List Component с фильтрацией и сортировкой
// ============================================================================

import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, combineLatest } from 'rxjs';
import { map, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { FormControl } from '@angular/forms';
import { MatTableDataSource } from '@angular/material/table';
import { Task } from '../models/task.model';
import * as TaskSelectors from '../store/task.selectors';
import * as TaskActions from '../store/task.actions';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TaskListComponent implements OnInit {
  displayedColumns: string[] = ['title', 'status', 'priority', 'assignee', 'dueDate', 'actions'];
  dataSource = new MatTableDataSource<Task>([]);
  
  searchControl = new FormControl('');
  statusFilterControl = new FormControl('all');
  priorityFilterControl = new FormControl('all');
  
  tasks$: Observable<Task[]>;
  loading$: Observable<boolean>;
  error$: Observable<string | null>;
  
  filteredTasks$: Observable<Task[]>;

  constructor(private store: Store) {
    this.tasks$ = this.store.select(TaskSelectors.selectAllTasks);
    this.loading$ = this.store.select(TaskSelectors.selectTasksLoading);
    this.error$ = this.store.select(TaskSelectors.selectTasksError);
    
    this.setupFiltering();
  }

  ngOnInit(): void {
    this.store.dispatch(TaskActions.loadTasks());
  }

  private setupFiltering(): void {
    const searchTerm$ = this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      map(term => term?.toLowerCase() || '')
    );

    const statusFilter$ = this.statusFilterControl.valueChanges.pipe(
      distinctUntilChanged()
    );

    const priorityFilter$ = this.priorityFilterControl.valueChanges.pipe(
      distinctUntilChanged()
    );

    this.filteredTasks$ = combineLatest([
      this.tasks$,
      searchTerm$,
      statusFilter$,
      priorityFilter$
    ]).pipe(
      map(([tasks, search, status, priority]) => 
        tasks.filter(task => 
          task.title.toLowerCase().includes(search) &&
          (status === 'all' || task.status === status) &&
          (priority === 'all' || task.priority === priority)
        )
      )
    );

    this.filteredTasks$.subscribe(tasks => {
      this.dataSource.data = tasks;
    });
  }

  onStatusChange(task: Task, newStatus: string): void {
    this.store.dispatch(TaskActions.updateTask({
      update: { id: task.id, changes: { status: newStatus } }
    }));
  }

  onDeleteTask(taskId: string): void {
    this.store.dispatch(TaskActions.deleteTask({ id: taskId }));
  }

  trackByTaskId(index: number, task: Task): string {
    return task.id;
  }
}

// ============================================================================
// 3. Kanban Board с Drag & Drop
// ============================================================================

import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CdkDragDrop, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Task } from '../models/task.model';
import * as TaskSelectors from '../store/task.selectors';
import * as TaskActions from '../store/task.actions';

interface KanbanColumn {
  id: string;
  title: string;
  tasks: Task[];
}

@Component({
  selector: 'app-kanban-board',
  templateUrl: './kanban-board.component.html',
  styleUrls: ['./kanban-board.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class KanbanBoardComponent implements OnInit {
  columns: KanbanColumn[] = [
    { id: 'backlog', title: 'Backlog', tasks: [] },
    { id: 'in-progress', title: 'In Progress', tasks: [] },
    { id: 'review', title: 'Review', tasks: [] },
    { id: 'done', title: 'Done', tasks: [] }
  ];

  tasks$: Observable<Task[]>;

  constructor(private store: Store) {
    this.tasks$ = this.store.select(TaskSelectors.selectAllTasks);
  }

  ngOnInit(): void {
    this.store.dispatch(TaskActions.loadTasks());
    this.setupKanbanColumns();
  }

  private setupKanbanColumns(): void {
    this.tasks$.subscribe(tasks => {
      this.columns.forEach(column => {
        column.tasks = tasks.filter(task => task.status === column.id);
      });
    });
  }

  onDrop(event: CdkDragDrop<Task[]>): void {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      
      // Обновляем статус задачи
      const task = event.container.data[event.currentIndex];
      const newStatus = event.container.id;
      
      this.store.dispatch(TaskActions.updateTask({
        update: { id: task.id, changes: { status: newStatus } }
      }));
    }
  }

  getConnectedLists(): string[] {
    return this.columns.map(column => column.id);
  }

  trackByTaskId(index: number, task: Task): string {
    return task.id;
  }
}

// ============================================================================
// 4. Time Tracking Timer Service
// ============================================================================

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, interval, Subscription } from 'rxjs';
import { map, takeWhile } from 'rxjs/operators';

export interface TimerState {
  isRunning: boolean;
  isPaused: boolean;
  currentTime: number; // в секундах
  totalTime: number; // в секундах
  taskId: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class TimerService {
  private timerState$ = new BehaviorSubject<TimerState>({
    isRunning: false,
    isPaused: false,
    currentTime: 0,
    totalTime: 0,
    taskId: null
  });

  private timerSubscription: Subscription | null = null;

  getTimerState(): Observable<TimerState> {
    return this.timerState$.asObservable();
  }

  getFormattedTime(): Observable<string> {
    return this.timerState$.pipe(
      map(state => this.formatTime(state.currentTime))
    );
  }

  startTimer(taskId: string): void {
    const currentState = this.timerState$.value;
    
    if (currentState.isRunning) {
      return;
    }

    this.timerState$.next({
      ...currentState,
      isRunning: true,
      isPaused: false,
      taskId
    });

    this.startTimerInterval();
  }

  pauseTimer(): void {
    const currentState = this.timerState$.value;
    
    if (!currentState.isRunning) {
      return;
    }

    this.timerState$.next({
      ...currentState,
      isPaused: true
    });

    this.stopTimerInterval();
  }

  resumeTimer(): void {
    const currentState = this.timerState$.value;
    
    if (!currentState.isRunning || !currentState.isPaused) {
      return;
    }

    this.timerState$.next({
      ...currentState,
      isPaused: false
    });

    this.startTimerInterval();
  }

  stopTimer(): void {
    const currentState = this.timerState$.value;
    
    if (!currentState.isRunning) {
      return;
    }

    this.stopTimerInterval();
    
    this.timerState$.next({
      isRunning: false,
      isPaused: false,
      currentTime: 0,
      totalTime: currentState.totalTime + currentState.currentTime,
      taskId: null
    });
  }

  private startTimerInterval(): void {
    this.stopTimerInterval();
    
    this.timerSubscription = interval(1000).pipe(
      takeWhile(() => this.timerState$.value.isRunning && !this.timerState$.value.isPaused)
    ).subscribe(() => {
      const currentState = this.timerState$.value;
      this.timerState$.next({
        ...currentState,
        currentTime: currentState.currentTime + 1
      });
    });
  }

  private stopTimerInterval(): void {
    if (this.timerSubscription) {
      this.timerSubscription.unsubscribe();
      this.timerSubscription = null;
    }
  }

  private formatTime(seconds: number): string {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  }
}

// ============================================================================
// 5. Global Search Service
// ============================================================================

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Task } from '../models/task.model';
import { Project } from '../models/project.model';

export interface SearchResult {
  type: 'task' | 'project' | 'user';
  id: string;
  title: string;
  description?: string;
  url: string;
  score: number;
}

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private readonly API_URL = '/api/search';

  constructor(private http: HttpClient) {}

  search(query: string): Observable<SearchResult[]> {
    if (!query || query.trim().length < 2) {
      return of([]);
    }

    return this.http.get<SearchResult[]>(`${this.API_URL}?q=${encodeURIComponent(query)}`).pipe(
      map(results => this.sortByRelevance(results)),
      catchError(() => of([]))
    );
  }

  searchTasks(query: string): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.API_URL}/tasks?q=${encodeURIComponent(query)}`).pipe(
      catchError(() => of([]))
    );
  }

  searchProjects(query: string): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.API_URL}/projects?q=${encodeURIComponent(query)}`).pipe(
      catchError(() => of([]))
    );
  }

  getSearchSuggestions(query: string): Observable<string[]> {
    if (!query || query.trim().length < 2) {
      return of([]);
    }

    return this.http.get<string[]>(`${this.API_URL}/suggestions?q=${encodeURIComponent(query)}`).pipe(
      catchError(() => of([]))
    );
  }

  private sortByRelevance(results: SearchResult[]): SearchResult[] {
    return results.sort((a, b) => b.score - a.score);
  }

  // RxJS оператор для debounced search
  createDebouncedSearch<T>(
    searchFn: (query: string) => Observable<T[]>,
    debounceMs: number = 300
  ) {
    return (query$: Observable<string>) => 
      query$.pipe(
        debounceTime(debounceMs),
        distinctUntilChanged(),
        switchMap(query => searchFn(query))
      );
  }
}

// ============================================================================
// 6. Notification Service с Real-time Updates
// ============================================================================

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, interval } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

export interface Notification {
  id: string;
  type: 'info' | 'success' | 'warning' | 'error';
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
  actionUrl?: string;
  data?: any;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications$ = new BehaviorSubject<Notification[]>([]);
  private unreadCount$ = new BehaviorSubject<number>(0);

  getNotifications(): Observable<Notification[]> {
    return this.notifications$.asObservable();
  }

  getUnreadCount(): Observable<number> {
    return this.unreadCount$.asObservable();
  }

  addNotification(notification: Omit<Notification, 'id' | 'timestamp' | 'read'>): void {
    const newNotification: Notification = {
      ...notification,
      id: this.generateId(),
      timestamp: new Date(),
      read: false
    };

    const currentNotifications = this.notifications$.value;
    const updatedNotifications = [newNotification, ...currentNotifications];
    
    this.notifications$.next(updatedNotifications);
    this.updateUnreadCount();
  }

  markAsRead(notificationId: string): void {
    const currentNotifications = this.notifications$.value;
    const updatedNotifications = currentNotifications.map(notification =>
      notification.id === notificationId 
        ? { ...notification, read: true }
        : notification
    );
    
    this.notifications$.next(updatedNotifications);
    this.updateUnreadCount();
  }

  markAllAsRead(): void {
    const currentNotifications = this.notifications$.value;
    const updatedNotifications = currentNotifications.map(notification =>
      ({ ...notification, read: true })
    );
    
    this.notifications$.next(updatedNotifications);
    this.updateUnreadCount();
  }

  removeNotification(notificationId: string): void {
    const currentNotifications = this.notifications$.value;
    const updatedNotifications = currentNotifications.filter(
      notification => notification.id !== notificationId
    );
    
    this.notifications$.next(updatedNotifications);
    this.updateUnreadCount();
  }

  clearAll(): void {
    this.notifications$.next([]);
    this.updateUnreadCount();
  }

  // Симуляция real-time уведомлений
  startRealTimeNotifications(): void {
    interval(30000).pipe( // Проверяем каждые 30 секунд
      switchMap(() => this.checkForNewNotifications())
    ).subscribe(notifications => {
      if (notifications.length > 0) {
        notifications.forEach(notification => {
          this.addNotification(notification);
        });
      }
    });
  }

  private checkForNewNotifications(): Observable<Omit<Notification, 'id' | 'timestamp' | 'read'>[]> {
    // Здесь будет реальный API вызов
    return new Observable(observer => {
      // Симуляция получения новых уведомлений
      const mockNotifications: Omit<Notification, 'id' | 'timestamp' | 'read'>[] = [
        {
          type: 'info',
          title: 'Новая задача',
          message: 'Вам назначена новая задача: "Обновить документацию"',
          actionUrl: '/tasks/123'
        }
      ];
      
      observer.next(mockNotifications);
      observer.complete();
    });
  }

  private updateUnreadCount(): void {
    const unreadCount = this.notifications$.value.filter(n => !n.read).length;
    this.unreadCount$.next(unreadCount);
  }

  private generateId(): string {
    return Math.random().toString(36).substr(2, 9);
  }
}

// ============================================================================
// 7. Analytics Dashboard Service
// ============================================================================

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';

export interface TaskAnalytics {
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  overdueTasks: number;
  completionRate: number;
  averageCompletionTime: number;
}

export interface TimeAnalytics {
  totalTimeSpent: number;
  averageTimePerTask: number;
  timeByProject: { projectId: string; projectName: string; time: number }[];
  timeByUser: { userId: string; userName: string; time: number }[];
  timeByDate: { date: string; time: number }[];
}

export interface ProjectAnalytics {
  projectId: string;
  projectName: string;
  totalTasks: number;
  completedTasks: number;
  completionRate: number;
  totalTimeSpent: number;
  averageTimePerTask: number;
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly API_URL = '/api/analytics';

  constructor(private http: HttpClient) {}

  getTaskAnalytics(projectId?: string): Observable<TaskAnalytics> {
    const url = projectId 
      ? `${this.API_URL}/tasks?projectId=${projectId}`
      : `${this.API_URL}/tasks`;
    
    return this.http.get<TaskAnalytics>(url);
  }

  getTimeAnalytics(projectId?: string, dateRange?: { start: Date; end: Date }): Observable<TimeAnalytics> {
    let url = projectId 
      ? `${this.API_URL}/time?projectId=${projectId}`
      : `${this.API_URL}/time`;
    
    if (dateRange) {
      url += `&start=${dateRange.start.toISOString()}&end=${dateRange.end.toISOString()}`;
    }
    
    return this.http.get<TimeAnalytics>(url);
  }

  getProjectAnalytics(): Observable<ProjectAnalytics[]> {
    return this.http.get<ProjectAnalytics[]>(`${this.API_URL}/projects`);
  }

  getDashboardData(): Observable<{
    taskAnalytics: TaskAnalytics;
    timeAnalytics: TimeAnalytics;
    projectAnalytics: ProjectAnalytics[];
  }> {
    return combineLatest([
      this.getTaskAnalytics(),
      this.getTimeAnalytics(),
      this.getProjectAnalytics()
    ]).pipe(
      map(([taskAnalytics, timeAnalytics, projectAnalytics]) => ({
        taskAnalytics,
        timeAnalytics,
        projectAnalytics
      }))
    );
  }

  exportReport(type: 'tasks' | 'time' | 'projects', format: 'pdf' | 'excel'): Observable<Blob> {
    return this.http.get(`${this.API_URL}/export/${type}?format=${format}`, {
      responseType: 'blob'
    });
  }
}

// ============================================================================
// 8. User Profile Service с Avatar Upload
// ============================================================================

import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export interface UserProfile {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  avatar?: string;
  bio?: string;
  preferences: {
    theme: 'light' | 'dark';
    language: string;
    notifications: {
      email: boolean;
      push: boolean;
      taskUpdates: boolean;
      projectUpdates: boolean;
    };
  };
  createdAt: Date;
  updatedAt: Date;
}

export interface ProfileUpdateRequest {
  firstName?: string;
  lastName?: string;
  bio?: string;
  preferences?: Partial<UserProfile['preferences']>;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private readonly API_URL = '/api/profile';
  private profile$ = new BehaviorSubject<UserProfile | null>(null);

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(this.API_URL).pipe(
      tap(profile => this.profile$.next(profile))
    );
  }

  getProfileObservable(): Observable<UserProfile | null> {
    return this.profile$.asObservable();
  }

  updateProfile(updates: ProfileUpdateRequest): Observable<UserProfile> {
    return this.http.patch<UserProfile>(this.API_URL, updates).pipe(
      tap(profile => this.profile$.next(profile))
    );
  }

  uploadAvatar(file: File): Observable<{ avatarUrl: string }> {
    const formData = new FormData();
    formData.append('avatar', file);

    return this.http.post<{ avatarUrl: string }>(`${this.API_URL}/avatar`, formData).pipe(
      tap(response => {
        const currentProfile = this.profile$.value;
        if (currentProfile) {
          this.profile$.next({
            ...currentProfile,
            avatar: response.avatarUrl
          });
        }
      })
    );
  }

  deleteAvatar(): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/avatar`).pipe(
      tap(() => {
        const currentProfile = this.profile$.value;
        if (currentProfile) {
          this.profile$.next({
            ...currentProfile,
            avatar: undefined
          });
        }
      })
    );
  }

  changePassword(currentPassword: string, newPassword: string): Observable<void> {
    return this.http.post<void>(`${this.API_URL}/change-password`, {
      currentPassword,
      newPassword
    });
  }

  updatePreferences(preferences: Partial<UserProfile['preferences']>): Observable<UserProfile> {
    return this.updateProfile({ preferences });
  }

  // Валидация файла аватара
  validateAvatarFile(file: File): { valid: boolean; error?: string } {
    const maxSize = 5 * 1024 * 1024; // 5MB
    const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];

    if (file.size > maxSize) {
      return { valid: false, error: 'Файл слишком большой. Максимальный размер: 5MB' };
    }

    if (!allowedTypes.includes(file.type)) {
      return { valid: false, error: 'Неподдерживаемый формат файла. Используйте JPEG, PNG или GIF' };
    }

    return { valid: true };
  }
}

console.log('Примеры кода для ключевых фич загружены!');
console.log('Включены: NgRx Entity Adapter, Task List, Kanban Board, Timer Service, Search Service, Notification Service, Analytics Service, Profile Service');
