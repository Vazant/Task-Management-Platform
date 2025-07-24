import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Material Modules
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

// Components
import { TasksComponent } from './components/tasks/tasks.component';

// Routes
import { tasksRoutes } from './tasks.routes';

@NgModule({
  declarations: [
    TasksComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(tasksRoutes),
    
    // Material Modules
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ]
})
export class TasksModule { } 