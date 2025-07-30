import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { LucideAngularModule, Shield } from 'lucide-angular';

@Component({
  selector: 'app-security-settings',
  standalone: true,
  imports: [CommonModule, MatCardModule, LucideAngularModule],
  template: `
    <div class="security-settings">
      <mat-card>
        <mat-card-header>
          <mat-card-title>
            <i-lucide [img]="ShieldIcon" [size]="24"></i-lucide>
            Настройки безопасности
          </mat-card-title>
        </mat-card-header>
        <mat-card-content>
          <p>Здесь будут настройки пароля, 2FA и безопасности аккаунта</p>
          <p>Этап 5: Безопасность - в разработке</p>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .security-settings {
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
export class SecuritySettingsComponent {
  // Icons
  readonly ShieldIcon = Shield;
}
