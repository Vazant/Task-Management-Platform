import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface TimeEntry {
  id: string;
  taskId: string;
  userId: string;
  startTime: Date;
  endTime?: Date;
  duration: number;
  description?: string;
  billable: boolean;
  projectId?: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface CreateTimeEntryRequest {
  taskId: string;
  startTime: Date;
  description?: string;
  billable?: boolean;
  projectId?: string;
}

export interface UpdateTimeEntryRequest {
  endTime?: Date;
  description?: string;
  billable?: boolean;
}

export interface TimeTrackingFilters {
  userId?: string;
  taskId?: string;
  projectId?: string;
  dateRange?: {
    start: Date;
    end: Date;
  };
  billable?: boolean;
}

export interface TimeTrackingSummary {
  totalTime: number;
  billableTime: number;
  nonBillableTime: number;
  averageSessionTime: number;
  totalSessions: number;
  mostProductiveHours: number[];
}

@Injectable({
  providedIn: 'root'
})
export class TimeTrackingService {
  private readonly baseUrl = `${environment.apiUrl}/time-tracking`;
  private readonly http = inject(HttpClient);

  getTimeEntries(filters?: TimeTrackingFilters): Observable<TimeEntry[]> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.userId) params = params.set('userId', filters.userId);
      if (filters.taskId) params = params.set('taskId', filters.taskId);
      if (filters.projectId) params = params.set('projectId', filters.projectId);
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
      if (filters.billable !== undefined) params = params.set('billable', filters.billable.toString());
    }

    return this.http.get<TimeEntry[]>(this.baseUrl, { params });
  }

  getTimeEntry(id: string): Observable<TimeEntry> {
    return this.http.get<TimeEntry>(`${this.baseUrl}/${id}`);
  }

  startTimeEntry(request: CreateTimeEntryRequest): Observable<TimeEntry> {
    return this.http.post<TimeEntry>(this.baseUrl, request);
  }

  stopTimeEntry(id: string): Observable<TimeEntry> {
    return this.http.post<TimeEntry>(`${this.baseUrl}/${id}/stop`, {});
  }

  updateTimeEntry(id: string, request: UpdateTimeEntryRequest): Observable<TimeEntry> {
    return this.http.put<TimeEntry>(`${this.baseUrl}/${id}`, request);
  }

  deleteTimeEntry(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  getTimeTrackingSummary(filters?: TimeTrackingFilters): Observable<TimeTrackingSummary> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.userId) params = params.set('userId', filters.userId);
      if (filters.taskId) params = params.set('taskId', filters.taskId);
      if (filters.projectId) params = params.set('projectId', filters.projectId);
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
      if (filters.billable !== undefined) params = params.set('billable', filters.billable.toString());
    }

    return this.http.get<TimeTrackingSummary>(`${this.baseUrl}/summary`, { params });
  }

  getActiveTimeEntry(): Observable<TimeEntry | null> {
    return this.http.get<TimeEntry | null>(`${this.baseUrl}/active`);
  }

  pauseTimeEntry(id: string): Observable<TimeEntry> {
    return this.http.post<TimeEntry>(`${this.baseUrl}/${id}/pause`, {});
  }

  resumeTimeEntry(id: string): Observable<TimeEntry> {
    return this.http.post<TimeEntry>(`${this.baseUrl}/${id}/resume`, {});
  }
}
