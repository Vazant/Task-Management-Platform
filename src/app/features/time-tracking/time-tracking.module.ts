import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Components
import { TimeTrackingComponent } from './components/time-tracking/time-tracking.component';

// Shared Module
import { SharedModule } from '../../shared/shared.module';

// Routes
import { timeTrackingRoutes } from './time-tracking.routes';

@NgModule({
  declarations: [
    TimeTrackingComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(timeTrackingRoutes),
    SharedModule
  ]
})
export class TimeTrackingModule { }
