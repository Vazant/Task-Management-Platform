import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Material Modules
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

// Components
import { TimeTrackingComponent } from './components/time-tracking/time-tracking.component';

// Routes
import { timeTrackingRoutes } from './time-tracking.routes';

@NgModule({
  declarations: [
    TimeTrackingComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(timeTrackingRoutes),
    
    // Material Modules
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ]
})
export class TimeTrackingModule { } 