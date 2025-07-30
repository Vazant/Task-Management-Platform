import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MaterialModule } from './material.module';
import { UserMenuComponent } from './components/user-menu/user-menu.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { AvatarComponent } from './components/avatar/avatar.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MaterialModule,
    UserMenuComponent,
    PageNotFoundComponent,
    AvatarComponent
  ],
  exports: [
    MaterialModule,
    UserMenuComponent,
    PageNotFoundComponent,
    AvatarComponent
  ],
})
export class SharedModule {}
