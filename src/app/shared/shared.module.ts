import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MaterialModule } from './material.module';
import { UserMenuComponent } from './components/user-menu/user-menu.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MaterialModule,
    UserMenuComponent,
    PageNotFoundComponent
  ],
  exports: [
    MaterialModule,
    UserMenuComponent,
    PageNotFoundComponent
  ],
})
export class SharedModule {}
