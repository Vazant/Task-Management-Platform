import { TestBed } from '@angular/core/testing';
import { AvatarService } from './avatar.service';
import { ApiService } from './api.service';
import { of } from 'rxjs';

describe('AvatarService', () => {
  let service: AvatarService;
  let apiServiceSpy: jasmine.SpyObj<ApiService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('ApiService', ['post', 'get', 'delete']);

    TestBed.configureTestingModule({
      providers: [
        AvatarService,
        { provide: ApiService, useValue: spy }
      ]
    });
    
    service = TestBed.inject(AvatarService);
    apiServiceSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should generate upload URL', () => {
    const mockResponse = {
      data: {
        uploadUrl: 'http://test.com/upload',
        storageKey: 'test-key',
        expiresAt: '2024-01-01T00:00:00Z'
      }
    };
    
    apiServiceSpy.post.and.returnValue(of(mockResponse));

    service.generateUploadUrl('test.jpg', 1024, 'image/jpeg').subscribe(result => {
      expect(result.uploadUrl).toBe('http://test.com/upload');
      expect(result.storageKey).toBe('test-key');
    });

    expect(apiServiceSpy.post).toHaveBeenCalledWith('/avatars/upload-url', {
      fileName: 'test.jpg',
      fileSize: 1024,
      contentType: 'image/jpeg'
    });
  });

  it('should confirm upload', () => {
    const mockResponse = {
      data: {
        id: 1,
        userId: 1,
        cdnUrl: 'http://test.com/avatar.jpg',
        fullUrl: 'http://test.com/avatar.jpg'
      }
    };
    
    apiServiceSpy.post.and.returnValue(of(mockResponse));

    service.confirmUpload('test-key').subscribe(result => {
      expect(result.id).toBe(1);
      expect(result.cdnUrl).toBe('http://test.com/avatar.jpg');
    });

    expect(apiServiceSpy.post).toHaveBeenCalledWith('/avatars/confirm', {
      storageKey: 'test-key'
    });
  });

  it('should get avatar URL correctly', () => {
    // Test with null/undefined
    expect(service.getAvatarUrl(null)).toBe('assets/images/default-avatar.svg');
    expect(service.getAvatarUrl(undefined)).toBe('assets/images/default-avatar.svg');

    // Test with full URL
    expect(service.getAvatarUrl('http://example.com/avatar.jpg')).toBe('http://example.com/avatar.jpg');
    expect(service.getAvatarUrl('https://example.com/avatar.jpg')).toBe('https://example.com/avatar.jpg');

    // Test with local API path
    expect(service.getAvatarUrl('/api/avatars/file/test-key')).toContain('/api/avatars/file/test-key');

    // Test with relative path
    expect(service.getAvatarUrl('/avatars/test.jpg')).toContain('/avatars/test.jpg');

    // Test with assets path
    expect(service.getAvatarUrl('assets/images/avatar.jpg')).toBe('assets/images/avatar.jpg');
  });

  it('should generate initials correctly', () => {
    expect(service.generateInitials('John Doe')).toBe('JD');
    expect(service.generateInitials('John')).toBe('JO');
    expect(service.generateInitials('J')).toBe('J');
    expect(service.generateInitials('')).toBe('U');
  });

  it('should generate background color', () => {
    const color1 = service.generateBackgroundColor('John Doe');
    const color2 = service.generateBackgroundColor('Jane Smith');
    
    expect(color1).toMatch(/^#[0-9A-F]{6}$/i);
    expect(color2).toMatch(/^#[0-9A-F]{6}$/i);
    expect(color1).not.toBe(color2); // Different names should generate different colors
  });
});
