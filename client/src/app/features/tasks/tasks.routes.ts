import { Routes } from '@angular/router';
import { provideState } from '@ngrx/store';
import { TasksComponent } from './components/tasks/tasks.component';
import { KanbanBoardComponent } from './components/kanban-board/kanban-board.component';
import { tasksReducer } from './store/tasks.reducer';

// Общие провайдеры для tasks routes
const tasksProviders = [
  provideState('tasks', tasksReducer)
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