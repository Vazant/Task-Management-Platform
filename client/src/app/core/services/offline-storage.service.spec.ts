import { TestBed } from '@angular/core/testing';
import { OfflineStorageService, SyncQueueItem } from './offline-storage.service';

describe('OfflineStorageService', () => {
  let service: OfflineStorageService;
  let mockIndexedDB: any;

  beforeEach(() => {
    // Mock IndexedDB
    mockIndexedDB = {
      open: jasmine.createSpy('open').and.returnValue({
        onerror: null,
        onsuccess: null,
        onupgradeneeded: null,
        result: {
          objectStoreNames: [],
          createObjectStore: jasmine.createSpy('createObjectStore').and.returnValue({
            createIndex: jasmine.createSpy('createIndex')
          }),
          transaction: jasmine.createSpy('transaction').and.returnValue({
            objectStore: jasmine.createSpy('objectStore').and.returnValue({
              put: jasmine.createSpy('put'),
              getAll: jasmine.createSpy('getAll').and.returnValue({
                onsuccess: null,
                onerror: null,
                result: []
              }),
              add: jasmine.createSpy('add'),
              delete: jasmine.createSpy('delete'),
              clear: jasmine.createSpy('clear')
            })
          })
        }
      })
    };

    // Mock global indexedDB
    Object.defineProperty(window, 'indexedDB', {
      value: mockIndexedDB,
      writable: true
    });

    TestBed.configureTestingModule({
      providers: [OfflineStorageService]
    });

    service = TestBed.inject(OfflineStorageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize database successfully', async () => {
    const request = mockIndexedDB.open();
    
    // Simulate successful database opening
    request.onsuccess();
    
    await service.initDatabase();
    
    expect(mockIndexedDB.open).toHaveBeenCalledWith('taskManagerDB', 1);
  });

  it('should handle database initialization error', async () => {
    const request = mockIndexedDB.open();
    const error = new Error('Database error');
    
    // Simulate database error
    request.onerror(error);
    
    await expectAsync(service.initDatabase()).toBeRejectedWithError('Database error');
  });

  it('should create object stores on upgrade', async () => {
    const request = mockIndexedDB.open();
    const mockDB = {
      objectStoreNames: [],
      createObjectStore: jasmine.createSpy('createObjectStore').and.returnValue({
        createIndex: jasmine.createSpy('createIndex')
      })
    };
    
    // Simulate upgrade needed
    request.onupgradeneeded({ target: { result: mockDB } });
    request.onsuccess();
    
    await service.initDatabase();
    
    expect(mockDB.createObjectStore).toHaveBeenCalledWith('tasks', { keyPath: 'id' });
    expect(mockDB.createObjectStore).toHaveBeenCalledWith('projects', { keyPath: 'id' });
    expect(mockDB.createObjectStore).toHaveBeenCalledWith('syncQueue', { keyPath: 'id', autoIncrement: true });
  });

  it('should save tasks to database', async () => {
    const tasks = [
      { id: '1', title: 'Task 1' },
      { id: '2', title: 'Task 2' }
    ];
    
    const request = mockIndexedDB.open();
    request.onsuccess();
    await service.initDatabase();
    
    await service.saveTasks(tasks);
    
    const transaction = mockIndexedDB.open().result.transaction;
    const store = transaction.objectStore;
    
    expect(transaction).toHaveBeenCalledWith(['tasks'], 'readwrite');
    expect(store.put).toHaveBeenCalledTimes(2);
  });

  it('should get tasks from database', async () => {
    const mockTasks = [
      { id: '1', title: 'Task 1' },
      { id: '2', title: 'Task 2' }
    ];
    
    const request = mockIndexedDB.open();
    const getAllRequest = {
      onsuccess: null,
      onerror: null,
      result: mockTasks
    };
    
    request.onsuccess();
    mockIndexedDB.open().result.transaction().objectStore().getAll.and.returnValue(getAllRequest);
    
    await service.initDatabase();
    
    // Simulate successful getAll
    if (getAllRequest.onsuccess) {
      (getAllRequest.onsuccess as any)();
    }
    
    const result = await service.getTasks();
    expect(result).toEqual(mockTasks);
  });

  it('should add item to sync queue', async () => {
    const action = { type: '[Tasks] Create Task', payload: { title: 'New Task' } };
    
    const request = mockIndexedDB.open();
    request.onsuccess();
    await service.initDatabase();
    
    await service.addToSyncQueue(action);
    
    const transaction = mockIndexedDB.open().result.transaction;
    const store = transaction.objectStore;
    
    expect(transaction).toHaveBeenCalledWith(['syncQueue'], 'readwrite');
    expect(store.add).toHaveBeenCalledWith({
      action,
      type: action.type,
      timestamp: jasmine.any(Number),
      retryCount: 0
    });
  });

  it('should get sync queue from database', async () => {
    const mockQueue = [
      { id: '1', action: { type: '[Tasks] Create Task' }, type: '[Tasks] Create Task', timestamp: Date.now(), retryCount: 0 }
    ];
    
    const request = mockIndexedDB.open();
    const getAllRequest = {
      onsuccess: null,
      onerror: null,
      result: mockQueue
    };
    
    request.onsuccess();
    mockIndexedDB.open().result.transaction().objectStore().getAll.and.returnValue(getAllRequest);
    
    await service.initDatabase();
    
    // Simulate successful getAll
    if (getAllRequest.onsuccess) {
      (getAllRequest.onsuccess as any)();
    }
    
    const result = await service.getSyncQueue();
    expect(result).toEqual(mockQueue);
  });

  it('should remove item from sync queue', async () => {
    const request = mockIndexedDB.open();
    request.onsuccess();
    await service.initDatabase();
    
    await service.removeFromSyncQueue('1');
    
    const transaction = mockIndexedDB.open().result.transaction;
    const store = transaction.objectStore;
    
    expect(transaction).toHaveBeenCalledWith(['syncQueue'], 'readwrite');
    expect(store.delete).toHaveBeenCalledWith('1');
  });

  it('should update sync queue item', async () => {
    const item: SyncQueueItem = {
      id: '1',
      action: { type: '[Tasks] Create Task' },
      type: '[Tasks] Create Task',
      timestamp: Date.now(),
      retryCount: 1
    };
    
    const request = mockIndexedDB.open();
    request.onsuccess();
    await service.initDatabase();
    
    await service.updateSyncQueueItem(item);
    
    const transaction = mockIndexedDB.open().result.transaction;
    const store = transaction.objectStore;
    
    expect(transaction).toHaveBeenCalledWith(['syncQueue'], 'readwrite');
    expect(store.put).toHaveBeenCalledWith(item);
  });

  it('should clear sync queue', async () => {
    const request = mockIndexedDB.open();
    request.onsuccess();
    await service.initDatabase();
    
    await service.clearSyncQueue();
    
    const transaction = mockIndexedDB.open().result.transaction;
    const store = transaction.objectStore;
    
    expect(transaction).toHaveBeenCalledWith(['syncQueue'], 'readwrite');
    expect(store.clear).toHaveBeenCalled();
  });

  it('should throw error when database not initialized', async () => {
    await expectAsync(service.saveTasks([])).toBeRejectedWithError('Database not initialized');
    await expectAsync(service.getTasks()).toBeRejectedWithError('Database not initialized');
    await expectAsync(service.addToSyncQueue({})).toBeRejectedWithError('Database not initialized');
  });
});
