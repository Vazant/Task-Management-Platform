import { Routes } from '@angular/router';
import { provideState } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { TasksComponent } from './components/tasks/tasks.component';
import { KanbanBoardComponent } from './components/kanban-board/kanban-board.component';
import { tasksReducer } from './store/tasks.reducer';
import { TasksEffects } from './store/tasks.effects';

// Общие провайдеры для tasks routes
const tasksProviders = [
  provideState('tasks', tasksReducer),
  provideEffects([TasksEffects])
];

export const tasksRoutes: Routes = [
  {
    path: '',
    component: TasksComponent,
    providers: tasksProviders
  },
  {
    path: 'kanban',
    component: KanbanBoardComponent,
    providers: tasksProviders
  }
]; 