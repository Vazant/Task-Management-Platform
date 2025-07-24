import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';

import { SettingsComponent } from './components/settings/settings.component';
import { SharedModule } from '../../shared/shared.module';
import { settingsRoutes } from './settings.routes';

@NgModule({
  declarations: [
    SettingsComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    RouterModule.forChild(settingsRoutes)
  ]
})
export class SettingsModule { }
