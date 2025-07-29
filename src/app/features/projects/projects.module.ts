import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

// Routes
import { projectsRoutes } from './projects.routes';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(projectsRoutes)
  ]
})
export class ProjectsModule { }
