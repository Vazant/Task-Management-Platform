import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Components
import { TasksComponent } from './components/tasks/tasks.component';

// Shared Module
import { SharedModule } from '../../shared/shared.module';

// Routes
import { tasksRoutes } from './tasks.routes';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild(tasksRoutes),
    SharedModule,
    TasksComponent
  ]
})
export class TasksModule { }
