import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Subject, Observable, combineLatest } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil, filter, switchMap } from 'rxjs/operators';
import { Router } from '@angular/router';

import { GlobalSearchService } from '../../services/global-search.service';
import { SearchResult, SearchSuggestion, SearchHistory } from '../../models/search.model';

@Component({
  selector: 'app-global-search',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatListModule,
    MatChipsModule,
    MatMenuModule,
    MatTooltipModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './global-search.component.html',
  styleUrls: ['./global-search.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GlobalSearchComponent implements OnInit, OnDestroy {
  @ViewChild('searchInput', { static: true }) searchInput!: ElementRef<HTMLInputElement>;
  
  searchControl = new FormControl('');
  searchResults$: Observable<SearchResult[]>;
  searchHistory$: Observable<SearchHistory[]>;
  suggestions$: Observable<SearchSuggestion[]>;
  loading$: Observable<boolean>;
  
  showResults = false;
  showHistory = false;
  showSuggestions = false;
  
  private destroy$ = new Subject<void>();
  
  constructor(
    private searchService: GlobalSearchService,
    private router: Router
  ) {
    this.searchResults$ = this.searchService.searchResults$;
    this.searchHistory$ = this.searchService.searchHistory$;
    this.suggestions$ = this.searchService.suggestions$;
    this.loading$ = this.searchService.loading$;
  }
  
  ngOnInit(): void {
    this.setupSearchControl();
    this.setupFocusHandling();
  }
  
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
  
  onSearchInput(): void {
    const query = this.searchControl.value?.trim();
    
    if (!query) {
      this.showResults = false;
      this.showSuggestions = false;
      this.showHistory = true;
      return;
    }
    
    this.searchService.search(query);
    this.showResults = true;
    this.showHistory = false;
    this.showSuggestions = false;
  }
  
  onSearchFocus(): void {
    const query = this.searchControl.value?.trim();
    
    if (!query) {
      this.showHistory = true;
      this.showResults = false;
      this.showSuggestions = false;
    } else {
      this.showResults = true;
      this.showHistory = false;
      this.showSuggestions = false;
    }
  }
  
  onSearchBlur(): void {
    // Delay hiding to allow for clicks on results
    setTimeout(() => {
      this.showResults = false;
      this.showHistory = false;
      this.showSuggestions = false;
    }, 200);
  }
  
  onResultClick(result: SearchResult): void {
    this.router.navigate([result.route]);
    this.clearSearch();
  }
  
  onHistoryItemClick(historyItem: SearchHistory): void {
    this.searchControl.setValue(historyItem.query);
    this.searchService.search(historyItem.query);
    this.showResults = true;
    this.showHistory = false;
  }
  
  onSuggestionClick(suggestion: SearchSuggestion): void {
    this.searchControl.setValue(suggestion.text);
    this.searchService.search(suggestion.text);
    this.showResults = true;
    this.showSuggestions = false;
  }
  
  clearSearch(): void {
    this.searchControl.setValue('');
    this.searchService.clearSearch();
    this.showResults = false;
    this.showHistory = false;
    this.showSuggestions = false;
  }
  
  clearHistory(): void {
    this.searchService.clearSearchHistory();
  }
  
  getResultIcon(type: string): string {
    switch (type) {
      case 'task': return 'assignment';
      case 'project': return 'folder';
      case 'user': return 'person';
      case 'comment': return 'comment';
      case 'file': return 'description';
      default: return 'search';
    }
  }
  
  getResultTypeLabel(type: string): string {
    switch (type) {
      case 'task': return 'Task';
      case 'project': return 'Project';
      case 'user': return 'User';
      case 'comment': return 'Comment';
      case 'file': return 'File';
      default: return 'Result';
    }
  }
  
  getSuggestionIcon(type: string): string {
    switch (type) {
      case 'recent': return 'history';
      case 'popular': return 'trending_up';
      case 'suggestion': return 'lightbulb';
      default: return 'search';
    }
  }
  
  trackByResultId(index: number, result: SearchResult): string {
    return result.id;
  }
  
  trackByHistoryId(index: number, history: SearchHistory): string {
    return `${history.query}-${history.timestamp.getTime()}`;
  }
  
  trackBySuggestionId(index: number, suggestion: SearchSuggestion): string {
    return `${suggestion.text}-${suggestion.type}`;
  }
  
  private setupSearchControl(): void {
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(query => {
      if (query?.trim()) {
        this.searchService.search(query.trim());
        this.showResults = true;
        this.showHistory = false;
        this.showSuggestions = false;
      } else {
        this.showResults = false;
        this.showHistory = true;
        this.showSuggestions = false;
      }
    });
  }
  
  private setupFocusHandling(): void {
    // Handle keyboard navigation
    this.searchControl.valueChanges.pipe(
      filter(query => !!query?.trim()),
      switchMap(query => this.searchService.getSuggestions(query!))
    ).subscribe(suggestions => {
      this.showSuggestions = suggestions.length > 0;
    });
  }
}
