import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, timer } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, map, catchError } from 'rxjs/operators';
import { SearchResult, SearchQuery, SearchResponse, SearchSuggestion, SearchHistory } from '../models/search.model';
import { Task } from '../models/task.model';
import { Project } from '../models/project.model';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class GlobalSearchService {
  private readonly searchSubject = new BehaviorSubject<string>('');
  search$ = this.searchSubject.asObservable();
  
  private readonly searchResultsSubject = new BehaviorSubject<SearchResult[]>([]);
  searchResults$ = this.searchResultsSubject.asObservable();
  
  private readonly searchHistorySubject = new BehaviorSubject<SearchHistory[]>([]);
  searchHistory$ = this.searchHistorySubject.asObservable();
  
  private readonly suggestionsSubject = new BehaviorSubject<SearchSuggestion[]>([]);
  suggestions$ = this.suggestionsSubject.asObservable();
  
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();
  
  private searchHistory: SearchHistory[] = [];
  private readonly searchCache = new Map<string, SearchResult[]>();
  
  constructor() {
    this.loadSearchHistory();
  }
  
  search(query: string): void {
    if (!query?.trim()) {
      this.searchResultsSubject.next([]);
      this.suggestionsSubject.next([]);
      return;
    }
    
    this.searchSubject.next(query.trim());
    this.loadingSubject.next(true);
    
    // Simulate search with debouncing
    timer(300).pipe(
      switchMap(() => this.performSearch(query.trim()))
    ).subscribe({
      next: (results) => {
        this.searchResultsSubject.next(results);
        this.loadingSubject.next(false);
        this.addToHistory(query.trim(), results.length);
      },
      error: () => {
        this.loadingSubject.next(false);
        this.searchResultsSubject.next([]);
      }
    });
  }
  
  getSuggestions(query: string): Observable<SearchSuggestion[]> {
    if (!query?.trim()) {
      return of([]);
    }
    
    // Simulate suggestions based on history and popular searches
    const suggestions: SearchSuggestion[] = [
      ...this.searchHistory
        .filter(h => h.query.toLowerCase().includes(query.toLowerCase()))
        .slice(0, 3)
        .map(h => ({ text: h.query, type: 'recent' as const })),
      { text: `${query} task`, type: 'suggestion' },
      { text: `${query} project`, type: 'suggestion' },
      { text: `${query} urgent`, type: 'suggestion' }
    ];
    
    return of(suggestions);
  }
  
  clearSearch(): void {
    this.searchSubject.next('');
    this.searchResultsSubject.next([]);
    this.suggestionsSubject.next([]);
  }
  
  getSearchHistory(): Observable<SearchHistory[]> {
    return this.searchHistory$;
  }
  
  clearSearchHistory(): void {
    this.searchHistory = [];
    this.searchHistorySubject.next([]);
    localStorage.removeItem('searchHistory');
  }
  
  private performSearch(query: string): Observable<SearchResult[]> {
    // Check cache first
    if (this.searchCache.has(query)) {
      return of(this.searchCache.get(query)!);
    }
    
    // Simulate search across different entities
    const results: SearchResult[] = [
      // Simulate task results
      ...this.searchTasks(query),
      // Simulate project results
      ...this.searchProjects(query),
      // Simulate user results
      ...this.searchUsers(query)
    ];
    
    // Sort by relevance score
    results.sort((a, b) => b.score - a.score);
    
    // Cache results
    this.searchCache.set(query, results);
    
    return of(results);
  }
  
  private searchTasks(query: string): SearchResult[] {
    // Simulate task search
    const mockTasks: Partial<Task>[] = [
      { id: '1', title: 'Implement user authentication', status: 'in-progress', priority: 'high' },
      { id: '2', title: 'Design database schema', status: 'completed', priority: 'medium' },
      { id: '3', title: 'Create API documentation', status: 'todo', priority: 'low' }
    ];
    
    return mockTasks
      .filter(task => 
        task.title?.toLowerCase().includes(query.toLowerCase()) ||
        task.status?.toLowerCase().includes(query.toLowerCase()) ||
        task.priority?.toLowerCase().includes(query.toLowerCase())
      )
      .map(task => ({
        id: task.id!,
        type: 'task' as const,
        title: task.title!,
        subtitle: `${task.status} • ${task.priority} priority`,
        icon: 'assignment',
        route: `/tasks/${task.id}`,
        score: this.calculateScore(task.title!, query),
        metadata: { status: task.status, priority: task.priority }
      }));
  }
  
  private searchProjects(query: string): SearchResult[] {
    // Simulate project search
    const mockProjects: Partial<Project>[] = [
      { id: '1', name: 'Task Management Platform', description: 'Modern task management application' },
      { id: '2', name: 'E-commerce Website', description: 'Online shopping platform' },
      { id: '3', name: 'Mobile App Development', description: 'Cross-platform mobile application' }
    ];
    
    return mockProjects
      .filter(project => 
        project.name?.toLowerCase().includes(query.toLowerCase()) ||
        project.description?.toLowerCase().includes(query.toLowerCase())
      )
      .map(project => ({
        id: project.id!,
        type: 'project' as const,
        title: project.name!,
        subtitle: project.description!,
        icon: 'folder',
        route: `/projects/${project.id}`,
        score: this.calculateScore(project.name!, query),
        metadata: { description: project.description }
      }));
  }
  
  private searchUsers(query: string): SearchResult[] {
    // Simulate user search
    const mockUsers: Partial<User>[] = [
      { id: '1', firstName: 'John', lastName: 'Doe', email: 'john.doe@example.com' },
      { id: '2', firstName: 'Jane', lastName: 'Smith', email: 'jane.smith@example.com' },
      { id: '3', firstName: 'Bob', lastName: 'Johnson', email: 'bob.johnson@example.com' }
    ];
    
    return mockUsers
      .filter(user => 
        `${user.firstName} ${user.lastName}`.toLowerCase().includes(query.toLowerCase()) ||
        user.email?.toLowerCase().includes(query.toLowerCase())
      )
      .map(user => ({
        id: user.id!,
        type: 'user' as const,
        title: `${user.firstName} ${user.lastName}`,
        subtitle: user.email!,
        icon: 'person',
        route: `/users/${user.id}`,
        score: this.calculateScore(`${user.firstName} ${user.lastName}`, query),
        metadata: { email: user.email }
      }));
  }
  
  private calculateScore(text: string, query: string): number {
    const textLower = text.toLowerCase();
    const queryLower = query.toLowerCase();
    
    // Exact match gets highest score
    if (textLower === queryLower) return 100;
    
    // Starts with query gets high score
    if (textLower.startsWith(queryLower)) return 90;
    
    // Contains query gets medium score
    if (textLower.includes(queryLower)) return 70;
    
    // Partial match gets lower score
    const words = queryLower.split(' ');
    const matchCount = words.filter(word => textLower.includes(word)).length;
    return (matchCount / words.length) * 50;
  }
  
  private addToHistory(query: string, resultCount: number): void {
    const historyItem: SearchHistory = {
      query,
      timestamp: new Date(),
      resultCount
    };
    
    // Remove duplicate entries
    this.searchHistory = this.searchHistory.filter(h => h.query !== query);
    
    // Add to beginning
    this.searchHistory.unshift(historyItem);
    
    // Keep only last 20 searches
    if (this.searchHistory.length > 20) {
      this.searchHistory = this.searchHistory.slice(0, 20);
    }
    
    this.searchHistorySubject.next([...this.searchHistory]);
    this.saveSearchHistory();
  }
  
  private loadSearchHistory(): void {
    const saved = localStorage.getItem('searchHistory');
    if (saved) {
      try {
        this.searchHistory = JSON.parse(saved).map((item: any) => ({
          ...item,
          timestamp: new Date(item.timestamp)
        }));
        this.searchHistorySubject.next([...this.searchHistory]);
      } catch (error) {
        console.error('Failed to load search history:', error);
        this.searchHistory = [];
      }
    }
  }
  
  private saveSearchHistory(): void {
    try {
      localStorage.setItem('searchHistory', JSON.stringify(this.searchHistory));
    } catch (error) {
      console.error('Failed to save search history:', error);
    }
  }
}
