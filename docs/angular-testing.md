# Angular Testing Patterns

## Обзор

Документация по тестированию Angular приложений с использованием Jasmine, Karma и Angular Testing Utilities.

## Ключевые концепции

### 1. Типы тестов
- **Unit тесты**: тестирование отдельных компонентов, сервисов, пайпов
- **Integration тесты**: тестирование взаимодействия между компонентами
- **E2E тесты**: тестирование полных пользовательских сценариев

### 2. Testing Utilities
- **ComponentFixture**: управление жизненным циклом компонента
- **TestBed**: конфигурация тестового окружения
- **async/fakeAsync**: управление асинхронными операциями
- **jasmine.createSpy**: создание моков

## Паттерны тестирования

### 1. Тестирование компонентов

#### Базовый паттерн
```typescript
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';

describe('ComponentName', () => {
  let component: ComponentName;
  let fixture: ComponentFixture<ComponentName>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComponentName]
    }).compileComponents();

    fixture = TestBed.createComponent(ComponentName);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
```

#### Тестирование с зависимостями
```typescript
describe('ComponentWithDependencies', () => {
  let component: ComponentWithDependencies;
  let fixture: ComponentFixture<ComponentWithDependencies>;
  let mockService: jasmine.SpyObj<MockService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('MockService', ['getData', 'saveData']);

    await TestBed.configureTestingModule({
      imports: [ComponentWithDependencies],
      providers: [
        { provide: MockService, useValue: spy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ComponentWithDependencies);
    component = fixture.componentInstance;
    mockService = TestBed.inject(MockService) as jasmine.SpyObj<MockService>;
    fixture.detectChanges();
  });

  it('should load data on init', () => {
    expect(mockService.getData).toHaveBeenCalled();
  });
});
```

#### Тестирование событий
```typescript
it('should emit event when button clicked', () => {
  const button = fixture.debugElement.query(By.css('button'));
  spyOn(component.eventEmitter, 'emit');

  button.triggerEventHandler('click', null);

  expect(component.eventEmitter.emit).toHaveBeenCalledWith(expectedValue);
});
```

### 2. Тестирование сервисов

#### Базовый паттерн
```typescript
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('DataService', () => {
  let service: DataService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DataService]
    });
    service = TestBed.inject(DataService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch data', () => {
    const testData = { id: 1, name: 'Test' };

    service.getData().subscribe(data => {
      expect(data).toEqual(testData);
    });

    const req = httpMock.expectOne('/api/data');
    expect(req.request.method).toBe('GET');
    req.flush(testData);
  });
});
```

#### Тестирование с моками
```typescript
describe('ServiceWithDependencies', () => {
  let service: ServiceWithDependencies;
  let mockDependency: jasmine.SpyObj<MockDependency>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('MockDependency', ['method1', 'method2']);

    TestBed.configureTestingModule({
      providers: [
        ServiceWithDependencies,
        { provide: MockDependency, useValue: spy }
      ]
    });
    service = TestBed.inject(ServiceWithDependencies);
    mockDependency = TestBed.inject(MockDependency) as jasmine.SpyObj<MockDependency>;
  });

  it('should call dependency method', () => {
    service.doSomething();
    expect(mockDependency.method1).toHaveBeenCalled();
  });
});
```

### 3. Тестирование NgRx

#### Тестирование Actions
```typescript
describe('Task Actions', () => {
  it('should create loadTasks action', () => {
    const action = TaskActions.loadTasks();
    expect(action.type).toBe('[Tasks] Load Tasks');
  });

  it('should create loadTasksSuccess action', () => {
    const tasks = [{ id: 1, title: 'Test' }];
    const action = TaskActions.loadTasksSuccess({ tasks });
    expect(action.type).toBe('[Tasks] Load Tasks Success');
    expect(action.tasks).toEqual(tasks);
  });
});
```

#### Тестирование Reducers
```typescript
describe('Tasks Reducer', () => {
  it('should return initial state', () => {
    const action = {} as any;
    const result = tasksReducer(undefined, action);
    expect(result).toEqual(initialState);
  });

  it('should handle loadTasksSuccess', () => {
    const tasks = [{ id: 1, title: 'Test' }];
    const action = TaskActions.loadTasksSuccess({ tasks });
    const result = tasksReducer(initialState, action);
    expect(result.entities).toEqual(tasks);
    expect(result.loading).toBe(false);
  });
});
```

#### Тестирование Selectors
```typescript
describe('Tasks Selectors', () => {
  let state: TasksState;

  beforeEach(() => {
    state = {
      entities: {
        1: { id: 1, title: 'Task 1', status: 'todo' },
        2: { id: 2, title: 'Task 2', status: 'done' }
      },
      ids: [1, 2],
      loading: false,
      error: null
    };
  });

  it('should select all tasks', () => {
    const result = selectAllTasks.projector(state);
    expect(result).toEqual([
      { id: 1, title: 'Task 1', status: 'todo' },
      { id: 2, title: 'Task 2', status: 'done' }
    ]);
  });

  it('should select tasks by status', () => {
    const result = selectTasksByStatus('todo').projector(state);
    expect(result).toEqual([
      { id: 1, title: 'Task 1', status: 'todo' }
    ]);
  });
});
```

#### Тестирование Effects
```typescript
import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable } from 'rxjs';
import { cold, hot } from 'jasmine-marbles';

describe('Tasks Effects', () => {
  let actions$: Observable<any>;
  let effects: TasksEffects;
  let mockService: jasmine.SpyObj<TaskService>;

  beforeEach(() => {
    const spy = jasmine.createSpyObj('TaskService', ['getTasks']);

    TestBed.configureTestingModule({
      providers: [
        TasksEffects,
        provideMockActions(() => actions$),
        { provide: TaskService, useValue: spy }
      ]
    });

    effects = TestBed.inject(TasksEffects);
    mockService = TestBed.inject(TaskService) as jasmine.SpyObj<TaskService>;
  });

  it('should load tasks on loadTasks action', () => {
    const tasks = [{ id: 1, title: 'Test' }];
    const action = TaskActions.loadTasks();
    const completion = TaskActions.loadTasksSuccess({ tasks });

    actions$ = hot('-a', { a: action });
    const response = cold('-a|', { a: tasks });
    mockService.getTasks.and.returnValue(response);

    const expected = cold('--b', { b: completion });
    expect(effects.loadTasks$).toBeObservable(expected);
  });
});
```

### 4. Тестирование форм

#### Тестирование Reactive Forms
```typescript
describe('Form Component', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormComponent, ReactiveFormsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create form with controls', () => {
    expect(component.form.get('name')).toBeTruthy();
    expect(component.form.get('email')).toBeTruthy();
  });

  it('should validate required fields', () => {
    const nameControl = component.form.get('name');
    nameControl?.setValue('');
    expect(nameControl?.errors?.['required']).toBeTruthy();
  });

  it('should validate email format', () => {
    const emailControl = component.form.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.errors?.['email']).toBeTruthy();
  });

  it('should submit form with valid data', () => {
    spyOn(component.formSubmit, 'emit');
    
    component.form.patchValue({
      name: 'Test User',
      email: 'test@example.com'
    });

    component.onSubmit();

    expect(component.formSubmit.emit).toHaveBeenCalledWith({
      name: 'Test User',
      email: 'test@example.com'
    });
  });
});
```

### 5. Тестирование асинхронных операций

#### Тестирование с async/await
```typescript
it('should load data asynchronously', async () => {
  const testData = { id: 1, name: 'Test' };
  mockService.getData.and.returnValue(Promise.resolve(testData));

  await component.loadData();

  expect(component.data).toEqual(testData);
  expect(mockService.getData).toHaveBeenCalled();
});
```

#### Тестирование с fakeAsync
```typescript
import { fakeAsync, tick } from '@angular/core/testing';

it('should handle timeout', fakeAsync(() => {
  let result: string | undefined;
  
  component.delayedOperation().subscribe(value => {
    result = value;
  });

  tick(1000); // Продвигаем время на 1 секунду
  
  expect(result).toBe('completed');
}));
```

### 6. Тестирование DOM

#### Тестирование элементов
```typescript
it('should display task title', () => {
  component.task = { id: 1, title: 'Test Task' };
  fixture.detectChanges();

  const titleElement = fixture.debugElement.query(By.css('.task-title'));
  expect(titleElement.nativeElement.textContent).toContain('Test Task');
});

it('should show loading spinner', () => {
  component.loading = true;
  fixture.detectChanges();

  const spinner = fixture.debugElement.query(By.css('.loading-spinner'));
  expect(spinner).toBeTruthy();
});

it('should hide loading spinner when not loading', () => {
  component.loading = false;
  fixture.detectChanges();

  const spinner = fixture.debugElement.query(By.css('.loading-spinner'));
  expect(spinner).toBeFalsy();
});
```

#### Тестирование событий
```typescript
it('should call method on button click', () => {
  spyOn(component, 'onButtonClick');
  const button = fixture.debugElement.query(By.css('button'));

  button.triggerEventHandler('click', null);

  expect(component.onButtonClick).toHaveBeenCalled();
});

it('should handle input change', () => {
  const input = fixture.debugElement.query(By.css('input'));
  const testValue = 'test input';

  input.triggerEventHandler('input', { target: { value: testValue } });

  expect(component.inputValue).toBe(testValue);
});
```

## Test Utilities

### 1. Mock Services
```typescript
export class MockTaskService {
  getTasks = jasmine.createSpy('getTasks').and.returnValue(of([]));
  createTask = jasmine.createSpy('createTask').and.returnValue(of({}));
  updateTask = jasmine.createSpy('updateTask').and.returnValue(of({}));
  deleteTask = jasmine.createSpy('deleteTask').and.returnValue(of({}));
}
```

### 2. Test Data Factories
```typescript
export class TestDataFactory {
  static createTask(overrides: Partial<Task> = {}): Task {
    return {
      id: '1',
      title: 'Test Task',
      description: 'Test Description',
      status: 'todo',
      priority: 'medium',
      assignee: 'user1',
      dueDate: new Date(),
      createdAt: new Date(),
      updatedAt: new Date(),
      ...overrides
    };
  }

  static createTaskList(count: number = 3): Task[] {
    return Array.from({ length: count }, (_, i) => 
      this.createTask({ id: `${i + 1}`, title: `Task ${i + 1}` })
    );
  }
}
```

### 3. Test Helpers
```typescript
export class TestHelpers {
  static createComponentFixture<T>(component: Type<T>): ComponentFixture<T> {
    return TestBed.createComponent(component);
  }

  static getElementByCss(fixture: ComponentFixture<any>, selector: string) {
    return fixture.debugElement.query(By.css(selector));
  }

  static getAllElementsByCss(fixture: ComponentFixture<any>, selector: string) {
    return fixture.debugElement.queryAll(By.css(selector));
  }

  static triggerEvent(element: DebugElement, eventName: string, eventObj: any = {}) {
    element.triggerEventHandler(eventName, eventObj);
  }

  static setInputValue(element: DebugElement, value: string) {
    element.triggerEventHandler('input', { target: { value } });
  }
}
```

## Best Practices

### 1. Организация тестов
- Группируйте тесты по функциональности
- Используйте описательные названия тестов
- Следуйте паттерну AAA (Arrange, Act, Assert)

### 2. Моки и стабы
- Мокайте только внешние зависимости
- Используйте реальные сервисы для интеграционных тестов
- Создавайте переиспользуемые моки

### 3. Изоляция тестов
- Каждый тест должен быть независимым
- Очищайте состояние между тестами
- Не полагайтесь на порядок выполнения тестов

### 4. Покрытие тестами
- Стремитесь к высокому покрытию кода
- Тестируйте как happy path, так и edge cases
- Включайте тесты на обработку ошибок

### 5. Производительность
- Используйте shallow testing для больших компонентов
- Оптимизируйте настройку TestBed
- Избегайте избыточных моков

## Конфигурация

### 1. Karma Configuration
```javascript
// karma.conf.js
module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    client: {
      clearContext: false
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' }
      ]
    },
    reporters: ['progress', 'kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    singleRun: false,
    restartOnFileChange: true
  });
};
```

### 2. Test Setup
```typescript
// test-setup.ts
import 'zone.js/testing';
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting()
);
```

## Примеры интеграционных тестов

### 1. Тестирование компонента с NgRx
```typescript
describe('TasksComponent Integration', () => {
  let component: TasksComponent;
  let fixture: ComponentFixture<TasksComponent>;
  let store: MockStore<TasksState>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TasksComponent, StoreModule.forRoot({})],
      providers: [
        provideMockStore({
          initialState: {
            tasks: {
              entities: {},
              ids: [],
              loading: false,
              error: null
            }
          }
        })
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TasksComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(MockStore);
    fixture.detectChanges();
  });

  it('should dispatch loadTasks on init', () => {
    spyOn(store, 'dispatch');
    
    component.ngOnInit();
    
    expect(store.dispatch).toHaveBeenCalledWith(loadTasks());
  });

  it('should display tasks from store', () => {
    const tasks = TestDataFactory.createTaskList(2);
    store.setState({
      tasks: {
        entities: tasks.reduce((acc, task) => ({ ...acc, [task.id]: task }), {}),
        ids: tasks.map(t => t.id),
        loading: false,
        error: null
      }
    });
    store.refreshState();
    fixture.detectChanges();

    const taskElements = fixture.debugElement.queryAll(By.css('.task-item'));
    expect(taskElements.length).toBe(2);
  });
});
```

### 2. Тестирование формы с валидацией
```typescript
describe('TaskFormComponent Integration', () => {
  let component: TaskFormComponent;
  let fixture: ComponentFixture<TaskFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskFormComponent, ReactiveFormsModule, NoopAnimationsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should validate form and show errors', () => {
    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    
    // Попытка отправить пустую форму
    submitButton.triggerEventHandler('click', null);
    fixture.detectChanges();

    // Проверяем наличие ошибок валидации
    const errorElements = fixture.debugElement.queryAll(By.css('.error-message'));
    expect(errorElements.length).toBeGreaterThan(0);
  });

  it('should submit valid form', () => {
    spyOn(component.formSubmit, 'emit');
    
    // Заполняем форму валидными данными
    component.form.patchValue({
      title: 'Test Task',
      description: 'Test Description',
      priority: 'medium',
      dueDate: new Date()
    });

    const submitButton = fixture.debugElement.query(By.css('button[type="submit"]'));
    submitButton.triggerEventHandler('click', null);

    expect(component.formSubmit.emit).toHaveBeenCalled();
  });
});
```

## Заключение

Правильное тестирование Angular приложений требует понимания различных паттернов и инструментов. Используйте эту документацию как руководство для создания качественных тестов, которые обеспечивают надежность и поддерживаемость вашего кода.
