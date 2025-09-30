import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of } from 'rxjs';

import { GlobalSearchComponent } from './global-search.component';
import { GlobalSearchService } from '../../services/global-search.service';
import { SearchResult, SearchSuggestion, SearchHistory } from '../../models/search.model';

describe('GlobalSearchComponent', () => {
  let component: GlobalSearchComponent;
  let fixture: ComponentFixture<GlobalSearchComponent>;
  let searchService: jasmine.SpyObj<GlobalSearchService>;
  let router: jasmine.SpyObj<Router>;

  const mockSearchResults: SearchResult[] = [
    {
      id: '1',
      type: 'task',
      title: 'Test Task',
      subtitle: 'in-progress • high priority',
      icon: 'assignment',
      route: '/tasks/1',
      score: 85,
      metadata: { status: 'in-progress', priority: 'high' }
    }
  ];

  const mockSearchHistory: SearchHistory[] = [
    {
      query: 'test',
      timestamp: new Date(),
      resultCount: 5
    }
  ];

  const mockSuggestions: SearchSuggestion[] = [
    {
      text: 'test task',
      type: 'suggestion'
    }
  ];

  beforeEach(async () => {
    const searchServiceSpy = jasmine.createSpyObj('GlobalSearchService', [
      'clearSearch', 'clearSearchHistory', 'getSuggestions'
    ]);
    
    // Set up observables as properties
    Object.defineProperty(searchServiceSpy, 'searchResults$', {
      value: of(mockSearchResults),
      writable: true
    });
    Object.defineProperty(searchServiceSpy, 'searchHistory$', {
      value: of(mockSearchHistory),
      writable: true
    });
    Object.defineProperty(searchServiceSpy, 'suggestions$', {
      value: of(mockSuggestions),
      writable: true
    });
    Object.defineProperty(searchServiceSpy, 'loading$', {
      value: of(false),
      writable: true
    });
    
    searchServiceSpy.search = jasmine.createSpy('search');

    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [GlobalSearchComponent, ReactiveFormsModule],
      providers: [
        { provide: GlobalSearchService, useValue: searchServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GlobalSearchComponent);
    component = fixture.componentInstance;
    searchService = TestBed.inject(GlobalSearchService) as jasmine.SpyObj<GlobalSearchService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty search control', () => {
    expect(component.searchControl.value).toBe('');
  });

  it('should setup search control on init', fakeAsync(() => {
    component.ngOnInit();
    tick();
    
    expect(component.searchControl).toBeDefined();
  }));

  it('should call search service when search input changes', fakeAsync(() => {
    component.ngOnInit();
    component.searchControl.setValue('test');
    tick(300); // debounce time
    
    expect(searchService.search).toHaveBeenCalledWith('test');
  }));

  it('should show results when search is performed', fakeAsync(() => {
    component.ngOnInit();
    component.searchControl.setValue('test');
    component.onSearchInput();
    tick();
    
    expect(component.showResults).toBe(true);
    expect(component.showHistory).toBe(false);
  }));

  it('should show history when search input is empty', fakeAsync(() => {
    component.ngOnInit();
    component.searchControl.setValue('');
    component.onSearchInput();
    tick();
    
    expect(component.showHistory).toBe(true);
    expect(component.showResults).toBe(false);
  }));

  it('should navigate to result route when result is clicked', () => {
    const result: SearchResult = {
      id: '1',
      type: 'task',
      title: 'Test Task',
      subtitle: 'Test subtitle',
      icon: 'assignment',
      route: '/tasks/1',
      score: 85
    };

    component.onResultClick(result);

    expect(router.navigate).toHaveBeenCalledWith(['/tasks/1']);
    expect(searchService.clearSearch).toHaveBeenCalled();
  });

  it('should set search value and perform search when history item is clicked', () => {
    const historyItem: SearchHistory = {
      query: 'test query',
      timestamp: new Date(),
      resultCount: 5
    };

    component.onHistoryItemClick(historyItem);

    expect(component.searchControl.value).toBe('test query');
    expect(searchService.search).toHaveBeenCalledWith('test query');
    expect(component.showResults).toBe(true);
    expect(component.showHistory).toBe(false);
  });

  it('should set search value and perform search when suggestion is clicked', () => {
    const suggestion: SearchSuggestion = {
      text: 'test suggestion',
      type: 'suggestion'
    };

    component.onSuggestionClick(suggestion);

    expect(component.searchControl.value).toBe('test suggestion');
    expect(searchService.search).toHaveBeenCalledWith('test suggestion');
    expect(component.showResults).toBe(true);
    expect(component.showSuggestions).toBe(false);
  });

  it('should clear search when clearSearch is called', () => {
    component.clearSearch();

    expect(component.searchControl.value).toBe('');
    expect(searchService.clearSearch).toHaveBeenCalled();
    expect(component.showResults).toBe(false);
    expect(component.showHistory).toBe(false);
  });

  it('should clear history when clearHistory is called', () => {
    component.clearHistory();

    expect(searchService.clearSearchHistory).toHaveBeenCalled();
  });

  it('should return correct icon for task type', () => {
    expect(component.getResultIcon('task')).toBe('assignment');
  });

  it('should return correct icon for project type', () => {
    expect(component.getResultIcon('project')).toBe('folder');
  });

  it('should return correct icon for user type', () => {
    expect(component.getResultIcon('user')).toBe('person');
  });

  it('should return correct icon for unknown type', () => {
    expect(component.getResultIcon('unknown')).toBe('search');
  });

  it('should return correct type label for task', () => {
    expect(component.getResultTypeLabel('task')).toBe('Task');
  });

  it('should return correct type label for project', () => {
    expect(component.getResultTypeLabel('project')).toBe('Project');
  });

  it('should return correct type label for user', () => {
    expect(component.getResultTypeLabel('user')).toBe('User');
  });

  it('should return correct type label for unknown type', () => {
    expect(component.getResultTypeLabel('unknown')).toBe('Result');
  });

  it('should return correct suggestion icon for recent type', () => {
    expect(component.getSuggestionIcon('recent')).toBe('history');
  });

  it('should return correct suggestion icon for popular type', () => {
    expect(component.getSuggestionIcon('popular')).toBe('trending_up');
  });

  it('should return correct suggestion icon for suggestion type', () => {
    expect(component.getSuggestionIcon('suggestion')).toBe('lightbulb');
  });

  it('should return correct suggestion icon for unknown type', () => {
    expect(component.getSuggestionIcon('unknown')).toBe('search');
  });

  it('should track results by id', () => {
    const result: SearchResult = {
      id: '1',
      type: 'task',
      title: 'Test',
      subtitle: 'Test',
      icon: 'assignment',
      route: '/test',
      score: 85
    };

    expect(component.trackByResultId(0, result)).toBe('1');
  });

  it('should track history by query and timestamp', () => {
    const history: SearchHistory = {
      query: 'test',
      timestamp: new Date('2023-01-01'),
      resultCount: 5
    };

    const result = component.trackByHistoryId(0, history);
    expect(result).toContain('test');
    expect(result).toContain('1672531200000'); // timestamp for 2023-01-01
  });

  it('should track suggestions by text and type', () => {
    const suggestion: SearchSuggestion = {
      text: 'test',
      type: 'suggestion'
    };

    expect(component.trackBySuggestionId(0, suggestion)).toBe('test-suggestion');
  });

  it('should show history on focus when search is empty', () => {
    component.searchControl.setValue('');
    component.onSearchFocus();

    expect(component.showHistory).toBe(true);
    expect(component.showResults).toBe(false);
    expect(component.showSuggestions).toBe(false);
  });

  it('should show results on focus when search has value', () => {
    component.searchControl.setValue('test');
    component.onSearchFocus();

    expect(component.showResults).toBe(true);
    expect(component.showHistory).toBe(false);
    expect(component.showSuggestions).toBe(false);
  });

  it('should hide dropdowns on blur after delay', fakeAsync(() => {
    component.showResults = true;
    component.showHistory = true;
    component.showSuggestions = true;

    component.onSearchBlur();
    tick(200);

    expect(component.showResults).toBe(false);
    expect(component.showHistory).toBe(false);
    expect(component.showSuggestions).toBe(false);
  }));

  it('should unsubscribe on destroy', () => {
    spyOn(component['destroy$'], 'next');
    spyOn(component['destroy$'], 'complete');

    component.ngOnDestroy();

    expect(component['destroy$'].next).toHaveBeenCalled();
    expect(component['destroy$'].complete).toHaveBeenCalled();
  });
});
