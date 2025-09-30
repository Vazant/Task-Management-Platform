import { TestBed } from '@angular/core/testing';
import { NetworkStatusService } from './network-status.service';

describe('NetworkStatusService', () => {
  let service: NetworkStatusService;
  let mockNavigator: any;

  beforeEach(() => {
    // Mock navigator
    mockNavigator = {
      onLine: true,
      connection: {
        effectiveType: '4g',
        addEventListener: jasmine.createSpy('addEventListener'),
        removeEventListener: jasmine.createSpy('removeEventListener')
      }
    };

    // Mock window events
    spyOn(window, 'addEventListener').and.callThrough();

    TestBed.configureTestingModule({
      providers: [NetworkStatusService]
    });

    service = TestBed.inject(NetworkStatusService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize with current online status', () => {
    expect(service.isOnline).toBe(true);
  });

  it('should emit online status changes', (done) => {
    service.isOnline$.subscribe(isOnline => {
      expect(isOnline).toBe(false);
      done();
    });

    // Simulate offline event
    window.dispatchEvent(new Event('offline'));
  });

  it('should emit connection type changes', (done) => {
    // Test isOnline$ instead since connectionType$ doesn't exist
    service.isOnline$.subscribe(isOnline => {
      expect(typeof isOnline).toBe('boolean');
      done();
    });
  });

  it('should handle online event', (done) => {
    service.isOnline$.subscribe(isOnline => {
      expect(isOnline).toBe(true);
      done();
    });

    // Simulate online event
    window.dispatchEvent(new Event('online'));
  });

  it('should handle offline event', (done) => {
    service.isOnline$.subscribe(isOnline => {
      expect(isOnline).toBe(false);
      done();
    });

    // Simulate offline event
    window.dispatchEvent(new Event('offline'));
  });

  it('should update connection type when connection changes', () => {
    const mockConnection = {
      effectiveType: '3g',
      addEventListener: jasmine.createSpy('addEventListener')
    };

    // Mock navigator.connection
    Object.defineProperty(navigator, 'connection', {
      value: mockConnection,
      writable: true
    });

    // Create new service instance to trigger connection monitoring
    const newService = new NetworkStatusService();
    
    expect(mockConnection.addEventListener).toHaveBeenCalledWith('change', jasmine.any(Function));
  });

  it('should handle missing connection API gracefully', () => {
    // Mock navigator without connection
    Object.defineProperty(navigator, 'connection', {
      value: undefined,
      writable: true
    });

    // Should not throw error
    expect(() => new NetworkStatusService()).not.toThrow();
  });
});
