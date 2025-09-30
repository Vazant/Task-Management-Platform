import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Project, CreateProjectRequest, UpdateProjectRequest, ProjectFilters, ProjectAnalytics, ProjectHistoryEntry, ProjectMember, ProjectStatus, ProjectPriority, ProjectStatistics } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) { }

  getProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.apiUrl);
  }

  getProjectById(id: string): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  createProject(request: CreateProjectRequest): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, request);
  }

  updateProject(id: string, request: UpdateProjectRequest): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, request);
  }

  deleteProject(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  filterProjects(filters: ProjectFilters): Observable<Project[]> {
    return this.http.get<Project[]>(this.apiUrl, { params: filters as any });
  }

  searchProjects(query: string): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/search`, { params: { query } });
  }

  getProjectHistory(projectId: string): Observable<ProjectHistoryEntry[]> {
    return this.http.get<ProjectHistoryEntry[]>(`${this.apiUrl}/${projectId}/history`);
  }

  getProjectsStatistics(): Observable<ProjectStatistics> {
    return this.http.get<ProjectStatistics>(`${this.apiUrl}/statistics`);
  }

  getProjectAnalytics(projectId: string): Observable<ProjectAnalytics> {
    return this.http.get<ProjectAnalytics>(`${this.apiUrl}/${projectId}/analytics`);
  }

  getProjectTags(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/tags`);
  }

  getProjectMembers(projectId: string): Observable<ProjectMember[]> {
    return this.http.get<ProjectMember[]>(`${this.apiUrl}/${projectId}/members`);
  }

  getProjectStatuses(): Observable<ProjectStatus[]> {
    return this.http.get<ProjectStatus[]>(`${this.apiUrl}/statuses`);
  }

  getProjectPriorities(): Observable<ProjectPriority[]> {
    return this.http.get<ProjectPriority[]>(`${this.apiUrl}/priorities`);
  }
}