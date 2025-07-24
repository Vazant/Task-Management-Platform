import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Material Modules
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

// Components
import { AnalyticsComponent } from './components/analytics/analytics.component';

// Routes
import { analyticsRoutes } from './analytics.routes';

@NgModule({
  declarations: [
    AnalyticsComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(analyticsRoutes),
    
    // Material Modules
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ]
})
export class AnalyticsModule { } 