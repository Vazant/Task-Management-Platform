import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatCardModule } from '@angular/material/card';
import { AvatarCropperComponent, CropResult } from './avatar-cropper.component';
import { AvatarService } from '../../../core/services/avatar.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-avatar-cropper-demo',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatCardModule],
  template: `
    <div class="demo-container">
      <mat-card class="demo-card">
        <mat-card-header>
          <mat-card-title>Демо Avatar Cropper</mat-card-title>
          <mat-card-subtitle>Простой пример обрезки аватара</mat-card-subtitle>
        </mat-card-header>
        
        <mat-card-content>
          <div class="avatar-section">
            <div class="current-avatar">
              <img 
                [src]="currentAvatarUrl || '/assets/images/default-avatar.svg'" 
                alt="Current Avatar"
                class="avatar-image" />
            </div>
            
            <div class="avatar-info">
              <p><strong>Текущий аватар:</strong></p>
              <p *ngIf="currentUser">Пользователь: {{ currentUser.username }}</p>
              <p *ngIf="currentUser?.email">Email: {{ currentUser?.email }}</p>
            </div>
          </div>
        </mat-card-content>
        
        <mat-card-actions>
          <input 
            type="file" 
            #fileInput
            (change)="onFileSelected($event)" 
            accept="image/*"
            style="display: none;" />
          
          <button mat-raised-button color="primary" (click)="fileInput.click()">
            Выбрать фото
          </button>
          
          <button mat-button (click)="removeAvatar()" *ngIf="currentAvatarUrl">
            Удалить аватар
          </button>
        </mat-card-actions>
      </mat-card>
      
      <mat-card class="info-card">
        <mat-card-header>
          <mat-card-title>Как это работает</mat-card-title>
        </mat-card-header>
        
        <mat-card-content>
          <ol>
            <li><strong>Выберите фото</strong> - нажмите кнопку "Выбрать фото"</li>
            <li><strong>Обрежьте</strong> - в открывшемся окне двигайте и зумите изображение</li>
            <li><strong>Сохраните</strong> - нажмите "Сохранить" для применения изменений</li>
            <li><strong>Готово!</strong> - аватар обновится автоматически</li>
          </ol>
          
          <div class="features">
            <h4>Особенности:</h4>
            <ul>
              <li>✅ Фиксированная круглая маска (256×256px)</li>
              <li>✅ Панорамирование и зум изображения</li>
              <li>✅ PNG формат с высоким качеством</li>
              <li>✅ Простой UX без лишних настроек</li>
            </ul>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .demo-container {
      display: flex;
      flex-direction: column;
      gap: 20px;
      padding: 20px;
      max-width: 800px;
      margin: 0 auto;
    }
    
    .demo-card, .info-card {
      width: 100%;
    }
    
    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 20px;
      padding: 20px 0;
    }
    
    .current-avatar {
      display: flex;
      justify-content: center;
    }
    
    .avatar-image {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      object-fit: cover;
      border: 3px solid #e0e0e0;
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }
    
    .avatar-info {
      text-align: center;
    }
    
    .avatar-info p {
      margin: 8px 0;
      color: #666;
    }
    
    .features {
      margin-top: 20px;
    }
    
    .features h4 {
      margin-bottom: 10px;
      color: #333;
    }
    
    .features ul {
      margin: 0;
      padding-left: 20px;
    }
    
    .features li {
      margin: 8px 0;
      color: #666;
    }
    
    mat-card-actions {
      justify-content: center;
      gap: 12px;
    }
    
    @media (max-width: 600px) {
      .demo-container {
        padding: 10px;
      }
      
      .avatar-image {
        width: 100px;
        height: 100px;
      }
    }
  `]
})
export class AvatarCropperDemoComponent {
  currentUser: User | null = null;
  currentAvatarUrl: string | undefined = undefined;

  constructor(
    private dialog: MatDialog,
    private avatarService: AvatarService
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    if (!file.type.startsWith('image/')) {
      alert('Пожалуйста, выберите изображение');
      return;
    }

    // Открываем диалог обрезки
    const dialogRef = this.dialog.open(AvatarCropperComponent, {
      width: '600px',
      height: '600px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { imageFile: file },
      disableClose: true
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.onCropped(result);
      }
    });
  }

  onCropped(result: CropResult): void {
    console.log('Результат обрезки:', result);
    
    // Загружаем на сервер
    this.avatarService.upload(result.file).subscribe({
      next: (user: User) => {
        this.currentUser = user;
        this.currentAvatarUrl = user.avatarUrl;
        console.log('Аватар успешно загружен:', user);
        alert('Аватар успешно обновлен!');
      },
      error: (error: unknown) => {
        console.error('Ошибка загрузки аватара:', error);
        alert('Ошибка загрузки аватара. Попробуйте еще раз.');
      }
    });
  }

  removeAvatar(): void {
    if (confirm('Вы уверены, что хотите удалить аватар?')) {
      this.avatarService.deleteAvatar().subscribe({
        next: (user: User) => {
          this.currentUser = user;
          this.currentAvatarUrl = user.avatarUrl;
          console.log('Аватар удален:', user);
          alert('Аватар удален!');
        },
        error: (error: unknown) => {
          console.error('Ошибка удаления аватара:', error);
          alert('Ошибка удаления аватара. Попробуйте еще раз.');
        }
      });
    }
  }
}
