export interface SearchResult {
  id: string;
  type: SearchResultType;
  title: string;
  subtitle: string;
  description?: string;
  icon: string;
  route: string;
  score: number;
  metadata?: Record<string, any>;
}

export type SearchResultType = 'task' | 'project' | 'user' | 'comment' | 'file';

export interface SearchFilters {
  types?: SearchResultType[];
  dateRange?: {
    from: Date;
    to: Date;
  };
  status?: string[];
  priority?: string[];
}

export interface SearchQuery {
  query: string;
  filters?: SearchFilters;
  limit?: number;
  offset?: number;
}

export interface SearchResponse {
  results: SearchResult[];
  total: number;
  hasMore: boolean;
  query: string;
  executionTime: number;
}

export interface SearchSuggestion {
  text: string;
  type: 'recent' | 'popular' | 'suggestion';
  count?: number;
}

export interface SearchHistory {
  query: string;
  timestamp: Date;
  resultCount: number;
}

export interface SearchAnalytics {
  totalSearches: number;
  averageQueryLength: number;
  mostSearchedTerms: Array<{
    term: string;
    count: number;
  }>;
  searchSuccessRate: number;
  averageResponseTime: number;
}
