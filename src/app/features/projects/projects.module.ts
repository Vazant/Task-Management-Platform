import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Material Modules
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

// Components
import { ProjectsComponent } from './components/projects/projects.component';

// Routes
import { projectsRoutes } from './projects.routes';

@NgModule({
  declarations: [
    ProjectsComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(projectsRoutes),
    
    // Material Modules
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ]
})
export class ProjectsModule { } 