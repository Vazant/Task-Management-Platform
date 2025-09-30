import { NgModule } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { ProjectMockService } from '../services/project-mock.service';
import { ProjectService } from '@services/project.service';
import { projectsReducer } from '../store/projects.reducer';
import { ProjectsEffects } from '../store/projects.effects';

@NgModule({
  imports: [
    HttpClientTestingModule,
    NoopAnimationsModule,
    MatDialogModule,
    MatSnackBarModule,
    StoreModule.forRoot({ projects: projectsReducer }),
    EffectsModule.forRoot([ProjectsEffects])
  ],
  providers: [
    { provide: ProjectService, useClass: ProjectMockService }
  ]
})
export class ProjectTestingModule { }
