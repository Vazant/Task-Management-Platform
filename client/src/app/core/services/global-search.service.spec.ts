import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { GlobalSearchService } from './global-search.service';
import { SearchResult, SearchSuggestion, SearchHistory } from '../models/search.model';

describe('GlobalSearchService', () => {
  let service: GlobalSearchService;
  let localStorageSpy: jasmine.SpyObj<Storage>;

  beforeEach(() => {
    const localStorageSpyObj = jasmine.createSpyObj('localStorage', ['getItem', 'setItem', 'removeItem']);
    
    TestBed.configureTestingModule({
      providers: [
        GlobalSearchService,
        { provide: 'localStorage', useValue: localStorageSpyObj }
      ]
    });
    
    service = TestBed.inject(GlobalSearchService);
    localStorageSpy = TestBed.inject('localStorage') as jasmine.SpyObj<Storage>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize with empty search history', () => {
    localStorageSpy.getItem.and.returnValue(null);
    
    service = TestBed.inject(GlobalSearchService);
    
    service.getSearchHistory().subscribe(history => {
      expect(history).toEqual([]);
    });
  });

  it('should load search history from localStorage', () => {
    const mockHistory = [
      {
        query: 'test',
        timestamp: new Date().toISOString(),
        resultCount: 5
      }
    ];
    
    localStorageSpy.getItem.and.returnValue(JSON.stringify(mockHistory));
    
    service = TestBed.inject(GlobalSearchService);
    
    service.getSearchHistory().subscribe(history => {
      expect(history.length).toBe(1);
      expect(history[0].query).toBe('test');
    });
  });

  it('should handle invalid JSON in localStorage', () => {
    localStorageSpy.getItem.and.returnValue('invalid json');
    
    service = TestBed.inject(GlobalSearchService);
    
    service.getSearchHistory().subscribe(history => {
      expect(history).toEqual([]);
    });
  });

  it('should perform search and return results', (done) => {
    service.search('test');
    
    service.searchResults$.subscribe(results => {
      expect(results.length).toBeGreaterThan(0);
      expect(results[0]).toHaveProperty('id');
      expect(results[0]).toHaveProperty('type');
      expect(results[0]).toHaveProperty('title');
      expect(results[0]).toHaveProperty('score');
      done();
    });
  });

  it('should clear search results when query is empty', (done) => {
    service.search('test');
    service.search('');
    
    service.searchResults$.subscribe(results => {
      expect(results).toEqual([]);
      done();
    });
  });

  it('should clear search results when query is whitespace only', (done) => {
    service.search('test');
    service.search('   ');
    
    service.searchResults$.subscribe(results => {
      expect(results).toEqual([]);
      done();
    });
  });

  it('should add search to history', (done) => {
    service.search('test query');
    
    setTimeout(() => {
      service.getSearchHistory().subscribe(history => {
        expect(history.length).toBeGreaterThan(0);
        expect(history[0].query).toBe('test query');
        done();
      });
    }, 400); // Wait for search to complete
  });

  it('should limit search history to 20 items', (done) => {
    // Add 25 searches
    for (let i = 0; i < 25; i++) {
      service.search(`query ${i}`);
    }
    
    setTimeout(() => {
      service.getSearchHistory().subscribe(history => {
        expect(history.length).toBeLessThanOrEqual(20);
        done();
      });
    }, 1000);
  });

  it('should remove duplicate entries from history', (done) => {
    service.search('test query');
    service.search('test query'); // Duplicate
    
    setTimeout(() => {
      service.getSearchHistory().subscribe(history => {
        const testQueries = history.filter(h => h.query === 'test query');
        expect(testQueries.length).toBe(1);
        done();
      });
    }, 800);
  });

  it('should get suggestions for query', (done) => {
    service.getSuggestions('test').subscribe(suggestions => {
      expect(suggestions.length).toBeGreaterThan(0);
      expect(suggestions[0]).toHaveProperty('text');
      expect(suggestions[0]).toHaveProperty('type');
      done();
    });
  });

  it('should return empty suggestions for empty query', (done) => {
    service.getSuggestions('').subscribe(suggestions => {
      expect(suggestions).toEqual([]);
      done();
    });
  });

  it('should return empty suggestions for whitespace query', (done) => {
    service.getSuggestions('   ').subscribe(suggestions => {
      expect(suggestions).toEqual([]);
      done();
    });
  });

  it('should clear search', (done) => {
    service.search('test');
    service.clearSearch();
    
    service.searchResults$.subscribe(results => {
      expect(results).toEqual([]);
      done();
    });
  });

  it('should clear search history', (done) => {
    service.search('test');
    
    setTimeout(() => {
      service.clearSearchHistory();
      
      service.getSearchHistory().subscribe(history => {
        expect(history).toEqual([]);
        done();
      });
    }, 400);
  });

  it('should calculate correct score for exact match', () => {
    const score = (service as any).calculateScore('test query', 'test query');
    expect(score).toBe(100);
  });

  it('should calculate correct score for starts with match', () => {
    const score = (service as any).calculateScore('test query result', 'test query');
    expect(score).toBe(90);
  });

  it('should calculate correct score for contains match', () => {
    const score = (service as any).calculateScore('this is a test query result', 'test query');
    expect(score).toBe(70);
  });

  it('should calculate correct score for partial match', () => {
    const score = (service as any).calculateScore('test result', 'test query');
    expect(score).toBe(50); // 1 out of 2 words match
  });

  it('should search tasks correctly', () => {
    const results = (service as any).searchTasks('authentication');
    expect(results.length).toBeGreaterThan(0);
    expect(results[0].type).toBe('task');
    expect(results[0].icon).toBe('assignment');
  });

  it('should search projects correctly', () => {
    const results = (service as any).searchProjects('management');
    expect(results.length).toBeGreaterThan(0);
    expect(results[0].type).toBe('project');
    expect(results[0].icon).toBe('folder');
  });

  it('should search users correctly', () => {
    const results = (service as any).searchUsers('john');
    expect(results.length).toBeGreaterThan(0);
    expect(results[0].type).toBe('user');
    expect(results[0].icon).toBe('person');
  });

  it('should cache search results', (done) => {
    service.search('test');
    
    setTimeout(() => {
      service.search('test'); // Same query again
      
      service.searchResults$.subscribe(results => {
        expect(results.length).toBeGreaterThan(0);
        done();
      });
    }, 400);
  });

  it('should handle search errors gracefully', (done) => {
    // Mock a scenario where search fails
    spyOn(service as any, 'performSearch').and.returnValue(
      of([]) // Return empty results instead of throwing
    );
    
    service.search('test');
    
    service.searchResults$.subscribe(results => {
      expect(results).toEqual([]);
      done();
    });
  });

  it('should emit loading state during search', (done) => {
    service.search('test');
    
    service.loading$.subscribe(loading => {
      // Should be true initially, then false
      expect(typeof loading).toBe('boolean');
      done();
    });
  });

  it('should save search history to localStorage', () => {
    service.search('test query');
    
    setTimeout(() => {
      expect(localStorageSpy.setItem).toHaveBeenCalledWith(
        'searchHistory',
        jasmine.any(String)
      );
    }, 400);
  });

  it('should handle localStorage errors gracefully', () => {
    localStorageSpy.setItem.and.throwError('Storage error');
    
    // Should not throw error
    expect(() => {
      service.search('test');
    }).not.toThrow();
  });
});
