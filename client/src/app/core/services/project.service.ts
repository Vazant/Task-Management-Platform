import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { 
  Project, 
  CreateProjectRequest, 
  UpdateProjectRequest, 
  ProjectFilters,
  ProjectStatistics 
} from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private readonly apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  /**
   * Получить список всех проектов
   */
  getProjects(filters?: ProjectFilters): Observable<Project[]> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.status) params = params.set('status', filters.status);
      if (filters.priority) params = params.set('priority', filters.priority);
      if (filters.ownerId) params = params.set('ownerId', filters.ownerId);
      if (filters.tags?.length) params = params.set('tags', filters.tags.join(','));
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
    }

    return this.http.get<Project[]>(this.apiUrl, { params })
      .pipe(
        map(projects => projects.map(project => this.mapToProject(project))),
        catchError(this.handleError)
      );
  }

  /**
   * Получить проект по ID
   */
  getProject(id: string): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`)
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Создать новый проект
   */
  createProject(request: CreateProjectRequest): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, request)
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Обновить проект
   */
  updateProject(id: string, request: UpdateProjectRequest): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, request)
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Удалить проект
   */
  deleteProject(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Получить статистику проектов
   */
  getProjectsStatistics(): Observable<ProjectStatistics> {
    return this.http.get<ProjectStatistics>(`${this.apiUrl}/statistics`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Поиск проектов
   */
  searchProjects(query: string): Observable<Project[]> {
    const params = new HttpParams().set('q', query);
    
    return this.http.get<Project[]>(`${this.apiUrl}/search`, { params })
      .pipe(
        map(projects => projects.map(project => this.mapToProject(project))),
        catchError(this.handleError)
      );
  }

  /**
   * Получить проекты пользователя
   */
  getUserProjects(userId: string): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/user/${userId}`)
      .pipe(
        map(projects => projects.map(project => this.mapToProject(project))),
        catchError(this.handleError)
      );
  }

  /**
   * Получить проекты с пагинацией
   */
  getProjectsPaginated(page: number = 0, size: number = 10, filters?: ProjectFilters): Observable<{
    projects: Project[];
    totalElements: number;
    totalPages: number;
    currentPage: number;
  }> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    if (filters) {
      if (filters.status) params = params.set('status', filters.status);
      if (filters.priority) params = params.set('priority', filters.priority);
      if (filters.ownerId) params = params.set('ownerId', filters.ownerId);
      if (filters.tags?.length) params = params.set('tags', filters.tags.join(','));
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
    }

    return this.http.get<any>(`${this.apiUrl}/paginated`, { params })
      .pipe(
        map(response => ({
          projects: response.content.map((project: any) => this.mapToProject(project)),
          totalElements: response.totalElements,
          totalPages: response.totalPages,
          currentPage: response.number
        })),
        catchError(this.handleError)
      );
  }

  /**
   * Получить проекты по тегам
   */
  getProjectsByTags(tags: string[]): Observable<Project[]> {
    const params = new HttpParams().set('tags', tags.join(','));
    
    return this.http.get<Project[]>(`${this.apiUrl}/by-tags`, { params })
      .pipe(
        map(projects => projects.map(project => this.mapToProject(project))),
        catchError(this.handleError)
      );
  }

  /**
   * Получить проекты по дате создания
   */
  getProjectsByDateRange(startDate: Date, endDate: Date): Observable<Project[]> {
    const params = new HttpParams()
      .set('startDate', startDate.toISOString())
      .set('endDate', endDate.toISOString());
    
    return this.http.get<Project[]>(`${this.apiUrl}/by-date`, { params })
      .pipe(
        map(projects => projects.map(project => this.mapToProject(project))),
        catchError(this.handleError)
      );
  }

  /**
   * Дублировать проект
   */
  duplicateProject(projectId: string, newName?: string): Observable<Project> {
    const request = newName ? { name: newName } : {};
    
    return this.http.post<Project>(`${this.apiUrl}/${projectId}/duplicate`, request)
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Архивировать проект
   */
  archiveProject(projectId: string): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${projectId}/archive`, {})
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Восстановить проект из архива
   */
  unarchiveProject(projectId: string): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${projectId}/unarchive`, {})
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Изменить статус проекта
   */
  changeProjectStatus(projectId: string, status: ProjectStatus): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${projectId}/status`, { status })
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Изменить приоритет проекта
   */
  changeProjectPriority(projectId: string, priority: ProjectPriority): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${projectId}/priority`, { priority })
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Получить историю изменений проекта
   */
  getProjectHistory(projectId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${projectId}/history`)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Экспортировать проекты в CSV
   */
  exportProjectsToCSV(filters?: ProjectFilters): Observable<Blob> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.status) params = params.set('status', filters.status);
      if (filters.priority) params = params.set('priority', filters.priority);
      if (filters.ownerId) params = params.set('ownerId', filters.ownerId);
      if (filters.tags?.length) params = params.set('tags', filters.tags.join(','));
    }

    return this.http.get(`${this.apiUrl}/export/csv`, { 
      params,
      responseType: 'blob'
    }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Импортировать проекты из CSV
   */
  importProjectsFromCSV(file: File): Observable<{ success: number; errors: any[] }> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<{ success: number; errors: any[] }>(`${this.apiUrl}/import/csv`, formData)
      .pipe(
        catchError(this.handleError)
      );
  }

  /**
   * Добавить участника в проект
   */
  addTeamMember(projectId: string, userId: string, role: string): Observable<Project> {
    return this.http.post<Project>(`${this.apiUrl}/${projectId}/members`, {
      userId,
      role
    })
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Удалить участника из проекта
   */
  removeTeamMember(projectId: string, userId: string): Observable<Project> {
    return this.http.delete<Project>(`${this.apiUrl}/${projectId}/members/${userId}`)
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Обновить роль участника
   */
  updateTeamMemberRole(projectId: string, userId: string, role: string): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${projectId}/members/${userId}`, {
      role
    })
      .pipe(
        map(project => this.mapToProject(project)),
        catchError(this.handleError)
      );
  }

  /**
   * Маппинг данных проекта
   */
  private mapToProject(data: any): Project {
    return {
      id: data.id,
      name: data.name,
      description: data.description,
      status: data.status,
      priority: data.priority,
      startDate: data.startDate ? new Date(data.startDate) : undefined,
      endDate: data.endDate ? new Date(data.endDate) : undefined,
      tags: data.tags || [],
      color: data.color,
      ownerId: data.ownerId,
      ownerName: data.ownerName,
      teamMembers: data.teamMembers || [],
      tasksCount: data.tasksCount || 0,
      completedTasksCount: data.completedTasksCount || 0,
      progress: data.progress || 0,
      createdAt: new Date(data.createdAt),
      updatedAt: new Date(data.updatedAt)
    };
  }

  /**
   * Обработка ошибок
   */
  private handleError(error: any): Observable<never> {
    console.error('Project Service Error:', error);
    
    let errorMessage = 'Произошла ошибка при работе с проектами';
    
    if (error.error?.message) {
      errorMessage = error.error.message;
    } else if (error.message) {
      errorMessage = error.message;
    }
    
    return throwError(() => new Error(errorMessage));
  }
}
