import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NotificationToastComponent } from './components/notification-toast/notification-toast.component';

@NgModule({
  declarations: [
    NotificationToastComponent,
  ],
  imports: [
    CommonModule,
  ],
  exports: [
    NotificationToastComponent,
  ],
})
export class SharedModule {} 