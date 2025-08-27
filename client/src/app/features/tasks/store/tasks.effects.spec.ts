import { TestBed } from '@angular/core/testing';
import { provideMockActions } from '@ngrx/effects/testing';
import { Observable, of } from 'rxjs';
import { TasksEffects } from './tasks.effects';
import * as TasksActions from './tasks.actions';
import { NotificationService } from '@services';

class NotificationServiceMock {
  success() {}
  error() {}
}

describe('TasksEffects', () => {
  let actions$: Observable<any>;
  let effects: TasksEffects;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TasksEffects,
        provideMockActions(() => actions$),
        { provide: NotificationService, useClass: NotificationServiceMock },
      ],
    });

    effects = TestBed.inject(TasksEffects);
  });

  it('should emit loadTasksSuccess', (done) => {
    actions$ = of(TasksActions.loadTasks());
    effects.loadTasks$.subscribe((action) => {
      expect(action.type).toBe('[Tasks] Load Tasks Success');
      done();
    });
  });
});

