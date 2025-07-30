import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { settingsRoutes } from './settings.routes';

@NgModule({
  imports: [
    RouterModule.forChild(settingsRoutes)
  ]
})
export class SettingsModule { }
