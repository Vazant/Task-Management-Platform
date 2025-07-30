import { NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ApiService } from './services';
import { AuthService } from './services';
import { NotificationService } from './services';

@NgModule({
  declarations: [],
  imports: [
    CommonModule
  ],
  providers: [
    ApiService,
    AuthService,
    NotificationService
  ],
  exports: []
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error('CoreModule is already loaded. Import it in the AppModule only.');
    }
  }
}
