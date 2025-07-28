import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Components
import { AnalyticsComponent } from './components/analytics/analytics.component';

// Shared Module
import { SharedModule } from '../../shared/shared.module';

// Routes
import { analyticsRoutes } from './analytics.routes';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild(analyticsRoutes),
    SharedModule,
    AnalyticsComponent
  ]
})
export class AnalyticsModule { }
