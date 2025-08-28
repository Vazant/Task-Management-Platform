import { Routes } from '@angular/router';
import { TasksComponent } from './components/tasks/tasks.component';
import { KanbanBoardComponent } from './components/kanban-board/kanban-board.component';

export const tasksRoutes: Routes = [
  {
    path: '',
    component: TasksComponent
  },
  {
    path: 'kanban',
    component: KanbanBoardComponent
  }
]; 