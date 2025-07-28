import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

// Components
import { SettingsComponent } from './components/settings/settings.component';

// Shared Module
import { SharedModule } from '../../shared/shared.module';

// Routes
import { settingsRoutes } from './settings.routes';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule.forChild(settingsRoutes),
    SharedModule,
    SettingsComponent
  ]
})
export class SettingsModule { }
