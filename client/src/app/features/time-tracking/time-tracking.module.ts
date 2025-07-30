import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

// Routes
import { timeTrackingRoutes } from './time-tracking.routes';

@NgModule({
  imports: [
    RouterModule.forChild(timeTrackingRoutes)
  ]
})
export class TimeTrackingModule { }
