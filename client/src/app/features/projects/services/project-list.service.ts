import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject, of, throwError } from 'rxjs';
import {
  catchError,
  shareReplay,
  tap,
  switchMap,
  distinctUntilChanged,
  debounceTime
} from 'rxjs/operators';
import { Project } from '@models';
import {
  ProjectListRequest,
  ProjectListResponse,
  ProjectListConfig
} from '../models/project-list.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectListService {
  private readonly apiUrl = '/api/projects';
  private readonly cache = new Map<string, ProjectListResponse>();
  private readonly cacheTimeout = 5 * 60 * 1000; // 5 минут

  // BehaviorSubject для отслеживания состояния загрузки
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);
  public readonly loading$ = this.loadingSubject.asObservable();

  // BehaviorSubject для отслеживания ошибок
  private readonly errorSubject = new BehaviorSubject<string | null>(null);
  public readonly error$ = this.errorSubject.asObservable();

  // Кэш для последнего запроса
  private readonly lastRequest: ProjectListRequest | null = null;

  private readonly http = inject(HttpClient);

  /**
   * Получение списка проектов с поддержкой фильтрации, сортировки и пагинации
   */
  getProjects(request: ProjectListRequest): Observable<ProjectListResponse> {
    const cacheKey = this.generateCacheKey(request);

    // Проверяем кэш
    const cached = this.cache.get(cacheKey);
    if (cached && this.isCacheValid(cacheKey)) {
      return of(cached);
    }

    this.loadingSubject.next(true);
    this.errorSubject.next(null);

    const params = this.buildHttpParams(request);

    return this.http.get<ProjectListResponse>(this.apiUrl, { params }).pipe(
      tap(response => {
        // Сохраняем в кэш
        this.cache.set(cacheKey, response);
        this.setCacheTimestamp(cacheKey);
        this.loadingSubject.next(false);
      }),
      catchError(error => {
        this.loadingSubject.next(false);
        this.errorSubject.next(this.handleError(error));
        return throwError(() => error);
      }),
      shareReplay(1) // Кэшируем последний результат для подписчиков
    );
  }

  /**
   * Получение проекта по ID
   */
  getProjectById(id: string): Observable<Project> {
    const cacheKey = `project_${id}`;
    const cached = this.cache.get(cacheKey);

    if (cached && this.isCacheValid(cacheKey)) {
      return of(cached.projects[0]);
    }

    return this.http.get<Project>(`${this.apiUrl}/${id}`).pipe(
      tap(project => {
        // Сохраняем в кэш
        const response: ProjectListResponse = {
          projects: [project],
          total: 1,
          hasMore: false
        };
        this.cache.set(cacheKey, response);
        this.setCacheTimestamp(cacheKey);
      }),
      catchError(error => {
        this.errorSubject.next(this.handleError(error));
        return throwError(() => error);
      }),
      shareReplay(1)
    );
  }

  /**
   * Создание нового проекта
   */
  createProject(project: Partial<Project>): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, project).pipe(
      tap(() => this.clearCache()), // Очищаем кэш при создании
      catchError(error => {
        this.errorSubject.next(this.handleError(error));
        return throwError(() => error);
      })
    );
  }

  /**
   * Обновление проекта
   */
  updateProject(id: string, project: Partial<Project>): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, project).pipe(
      tap(() => this.clearCache()), // Очищаем кэш при обновлении
      catchError(error => {
        this.errorSubject.next(this.handleError(error));
        return throwError(() => error);
      })
    );
  }

  /**
   * Удаление проекта
   */
  deleteProject(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.clearCache()), // Очищаем кэш при удалении
      catchError(error => {
        this.errorSubject.next(this.handleError(error));
        return throwError(() => error);
      })
    );
  }

  /**
   * Архивирование проекта
   */
  archiveProject(id: string): Observable<Project> {
    return this.http.patch<Project>(`${this.apiUrl}/${id}/archive`, {}).pipe(
      tap(() => this.clearCache()),
      catchError(error => {
        this.errorSubject.next(this.handleError(error));
        return throwError(() => error);
      })
    );
  }

  /**
   * Дублирование проекта
   */
  duplicateProject(id: string): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}/${id}/duplicate`, {}).pipe(
      tap(() => this.clearCache()),
      catchError(error => {
        this.errorSubject.next(this.handleError(error));
        return throwError(() => error);
      })
    );
  }

  /**
   * Получение проектов с debounce для поиска
   */
  getProjectsWithDebounce(
    request: ProjectListRequest,
    config: ProjectListConfig
  ): Observable<ProjectListResponse> {
    return of(request).pipe(
      debounceTime(config.debounceTime),
      distinctUntilChanged((prev, curr) =>
        JSON.stringify(prev) === JSON.stringify(curr)
      ),
      switchMap(req => this.getProjects(req))
    );
  }

  /**
   * Предварительная загрузка следующей страницы
   */
  prefetchNextPage(currentRequest: ProjectListRequest): Observable<ProjectListResponse> {
    const nextRequest: ProjectListRequest = {
      ...currentRequest,
      pagination: {
        ...currentRequest.pagination,
        page: currentRequest.pagination.page + 1
      }
    };

    return this.getProjects(nextRequest);
  }

  /**
   * Очистка кэша
   */
  clearCache(): void {
    this.cache.clear();
  }

  /**
   * Очистка ошибок
   */
  clearError(): void {
    this.errorSubject.next(null);
  }

  /**
   * Получение состояния загрузки
   */
  getLoadingState(): boolean {
    return this.loadingSubject.value;
  }

  /**
   * Получение текущей ошибки
   */
  getCurrentError(): string | null {
    return this.errorSubject.value;
  }

  // Приватные методы

  private buildHttpParams(request: ProjectListRequest): HttpParams {
    let params = new HttpParams()
      .set('page', request.pagination.page.toString())
      .set('pageSize', request.pagination.pageSize.toString())
      .set('sortField', request.sort.field)
      .set('sortDirection', request.sort.direction);

    // Добавляем фильтры
    if (request.filters.searchTerm) {
      params = params.set('search', request.filters.searchTerm);
    }

    if (request.filters.status && request.filters.status !== 'all') {
      params = params.set('status', request.filters.status);
    }

    if (request.filters.ownerId) {
      params = params.set('ownerId', request.filters.ownerId);
    }

    if (request.filters.dateRange.start) {
      params = params.set('startDate', request.filters.dateRange.start.toISOString());
    }

    if (request.filters.dateRange.end) {
      params = params.set('endDate', request.filters.dateRange.end.toISOString());
    }

    return params;
  }

  private generateCacheKey(request: ProjectListRequest): string {
    return `projects_${JSON.stringify(request)}`;
  }

  private isCacheValid(key: string): boolean {
    const timestamp = this.cache.get(`${key}_timestamp`) as number | undefined;
    if (!timestamp) return false;

    return Date.now() - timestamp < this.cacheTimeout;
  }

  private setCacheTimestamp(key: string): void {
    this.cache.set(`${key}_timestamp`, Date.now() as unknown as ProjectListResponse);
  }

  private handleError(error: unknown): string {
    if (typeof error === 'object' && error !== null && 'status' in error) {
      const status = (error as { status: number }).status;

      if (status === 404) {
        return 'Проекты не найдены';
      } else if (status === 403) {
        return 'Нет доступа к проектам';
      } else if (status === 500) {
        return 'Ошибка сервера';
      } else if (status === 0) {
        return 'Нет подключения к серверу';
      }
    }

    if (typeof error === 'object' && error !== null && 'error' in error) {
      const errorObj = (error as { error?: { message?: string } }).error;
      return errorObj?.message ?? 'Произошла неизвестная ошибка';
    }

    return 'Произошла неизвестная ошибка';
  }
}
