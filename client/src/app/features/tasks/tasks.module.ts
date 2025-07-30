import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

// Routes
import { tasksRoutes } from './tasks.routes';

@NgModule({
  imports: [
    RouterModule.forChild(tasksRoutes)
  ]
})
export class TasksModule { }
