import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './header.component';
import { FooterComponent } from './footer.component';



@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, HeaderComponent, FooterComponent],
  template: `
    <div class="layout-container" [class.with-header]="showHeader" [class.with-footer]="showFooter">
      <app-header *ngIf="showHeader"></app-header>

      <main class="main-content" [class.with-header]="showHeader" [class.with-footer]="showFooter">
        <ng-content></ng-content>
      </main>

      <app-footer *ngIf="showFooter"></app-footer>
    </div>
  `,
  styles: [`
    .layout-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }

    .main-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }

    .main-content.with-header {
      padding-top: 80px; /* Высота header */
    }

    .main-content.with-footer {
      min-height: calc(100vh - 80px); /* 100vh минус высота header */
    }

    /* Для страниц без header (например, главная) */
    .layout-container:not(.with-header) .main-content {
      padding-top: 0;
    }

    /* Для страниц без footer */
    .layout-container:not(.with-footer) .main-content {
      min-height: 100vh;
    }
  `]
})
export class LayoutComponent {
  @Input() showHeader = true;
  @Input() showFooter = true;
}
