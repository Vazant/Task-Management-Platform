import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MaterialModule } from './material.module';
import { NotificationToastComponent } from './components/notification-toast/notification-toast.component';

@NgModule({
  declarations: [
    NotificationToastComponent,
  ],
  imports: [
    CommonModule,
    MaterialModule
  ],
  exports: [
    NotificationToastComponent,
    MaterialModule
  ],
})
export class SharedModule {}
