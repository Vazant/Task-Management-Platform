import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { LucideAngularModule, Bell } from 'lucide-angular';

@Component({
  selector: 'app-notification-settings',
  standalone: true,
  imports: [CommonModule, MatCardModule, LucideAngularModule],
  template: `
    <div class="notification-settings">
      <mat-card>
        <mat-card-header>
          <mat-card-title>
            <i-lucide [img]="BellIcon" [size]="24"></i-lucide>
            Настройки уведомлений
          </mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Здесь будут настройки уведомлений и оповещений</p>
          <p>Этап 4: Уведомления - в разработке</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .notification-settings {
      padding: 1rem 0;
    }

    mat-card {
      margin-bottom: 1rem;
    }

    mat-card-header {
      margin-bottom: 1rem;
    }

    mat-card-title {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      font-size: 1.25rem;
      color: #2c3e50;
      margin: 0;

      i-lucide {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
      }
    }

    p {
      color: #6c757d;
      margin-bottom: 0.5rem;
    }
  `]
})
export class NotificationSettingsComponent {
  // Icons
  readonly BellIcon = Bell;
}
