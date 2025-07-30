import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

// Routes
import { analyticsRoutes } from './analytics.routes';

@NgModule({
  imports: [
    RouterModule.forChild(analyticsRoutes)
  ]
})
export class AnalyticsModule { }
