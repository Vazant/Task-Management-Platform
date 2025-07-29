import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

// Routes
import { projectsRoutes } from './projects.routes';

// Components
import { ProjectsComponent } from './components/projects/projects.component';
import { ProjectListComponent } from './components/project-list/project-list.component';
import { ProjectCardComponent } from './components/project-card/project-card.component';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    RouterModule.forChild(projectsRoutes),
    ProjectsComponent,
    ProjectListComponent,
    ProjectCardComponent
  ]
})
export class ProjectsModule { }
