import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AnalyticsData {
  totalTasks: number;
  completedTasks: number;
  overdueTasks: number;
  averageCompletionTime: number;
  teamProductivity: number;
  projectProgress: number;
  timeTracking: TimeTrackingData;
  performance: PerformanceData;
  trends: TrendData[];
}

export interface TimeTrackingData {
  totalTime: number;
  billableTime: number;
  nonBillableTime: number;
  averageSessionTime: number;
  mostProductiveHours: number[];
}

export interface PerformanceData {
  tasksPerDay: number;
  completionRate: number;
  averageTaskTime: number;
  efficiency: number;
  qualityScore: number;
}

export interface TrendData {
  date: string;
  value: number;
  label: string;
}

export interface AnalyticsFilters {
  dateRange?: {
    start: Date;
    end: Date;
  };
  projectIds?: string[];
  userIds?: string[];
  taskStatus?: string[];
  priority?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {
  private readonly baseUrl = `${environment.apiUrl}/analytics`;
  private readonly http = inject(HttpClient);

  getDashboardAnalytics(filters?: AnalyticsFilters): Observable<AnalyticsData> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
      if (filters.projectIds) params = params.set('projectIds', filters.projectIds.join(','));
      if (filters.userIds) params = params.set('userIds', filters.userIds.join(','));
      if (filters.taskStatus) params = params.set('taskStatus', filters.taskStatus.join(','));
      if (filters.priority) params = params.set('priority', filters.priority.join(','));
    }

    return this.http.get<AnalyticsData>(`${this.baseUrl}/dashboard`, { params });
  }

  getProjectAnalytics(projectId: string, filters?: AnalyticsFilters): Observable<any> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
    }

    return this.http.get<any>(`${this.baseUrl}/projects/${projectId}`, { params });
  }

  getUserAnalytics(userId: string, filters?: AnalyticsFilters): Observable<any> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
    }

    return this.http.get<any>(`${this.baseUrl}/users/${userId}`, { params });
  }

  getTimeTrackingAnalytics(filters?: AnalyticsFilters): Observable<TimeTrackingData> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
      if (filters.projectIds) params = params.set('projectIds', filters.projectIds.join(','));
      if (filters.userIds) params = params.set('userIds', filters.userIds.join(','));
    }

    return this.http.get<TimeTrackingData>(`${this.baseUrl}/time-tracking`, { params });
  }

  getPerformanceAnalytics(filters?: AnalyticsFilters): Observable<PerformanceData> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
      if (filters.projectIds) params = params.set('projectIds', filters.projectIds.join(','));
      if (filters.userIds) params = params.set('userIds', filters.userIds.join(','));
    }

    return this.http.get<PerformanceData>(`${this.baseUrl}/performance`, { params });
  }

  getTrends(filters?: AnalyticsFilters): Observable<TrendData[]> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
      if (filters.projectIds) params = params.set('projectIds', filters.projectIds.join(','));
      if (filters.userIds) params = params.set('userIds', filters.userIds.join(','));
    }

    return this.http.get<TrendData[]>(`${this.baseUrl}/trends`, { params });
  }

  exportAnalytics(filters?: AnalyticsFilters, format: 'pdf' | 'excel' | 'csv' = 'pdf'): Observable<Blob> {
    let params = new HttpParams();
    
    if (filters) {
      if (filters.dateRange) {
        params = params.set('startDate', filters.dateRange.start.toISOString());
        params = params.set('endDate', filters.dateRange.end.toISOString());
      }
      if (filters.projectIds) params = params.set('projectIds', filters.projectIds.join(','));
      if (filters.userIds) params = params.set('userIds', filters.userIds.join(','));
    }
    
    params = params.set('format', format);

    return this.http.get(`${this.baseUrl}/export`, { 
      params, 
      responseType: 'blob' 
    });
  }
}
