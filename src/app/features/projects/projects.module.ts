import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Components
import { ProjectsComponent } from './components/projects/projects.component';

// Shared Module
import { SharedModule } from '../../shared/shared.module';

// Routes
import { projectsRoutes } from './projects.routes';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild(projectsRoutes),
    SharedModule,
    ProjectsComponent
  ]
})
export class ProjectsModule { }
