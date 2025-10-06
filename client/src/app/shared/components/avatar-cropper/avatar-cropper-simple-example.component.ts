import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { AvatarCropperComponent } from './avatar-cropper.component';
import { AvatarService } from '../../../core/services/avatar.service';
import { User } from '../../../core/models/user.model';

@Component({
  selector: 'app-avatar-cropper-simple-example',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  template: `
    <div class="example-container">
      <h2>Простой пример Avatar Cropper</h2>
      
      <!-- PrimeNG Avatar (если установлен) -->
      <div class="avatar-section">
        <div class="current-avatar">
          <img 
            [src]="currentAvatarUrl || '/assets/images/default-avatar.svg'" 
            alt="Current Avatar"
            class="avatar-image" />
        </div>
        
        <div class="avatar-actions">
          <input 
            type="file" 
            #fileInput
            (change)="onFileSelected($event)" 
            accept="image/*"
            style="display: none;" />
          
          <button mat-raised-button color="primary" (click)="fileInput.click()">
            Изменить аватар
          </button>
          
          <button mat-button (click)="removeAvatar()" *ngIf="currentAvatarUrl">
            Удалить
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .example-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 20px;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 8px;
      margin: 20px;
    }
    
    .avatar-section {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;
    }
    
    .current-avatar {
      display: flex;
      justify-content: center;
    }
    
    .avatar-image {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid #e0e0e0;
    }
    
    .avatar-actions {
      display: flex;
      gap: 12px;
      justify-content: center;
    }
  `]
})
export class AvatarCropperSimpleExampleComponent {
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

  onCropped(result: any): void {
    // result содержит { file, blob, dataUrl }
    this.avatarService.upload(result.file).subscribe({
      next: (user: User) => {
        this.currentUser = user;
        this.currentAvatarUrl = user.avatarUrl;
        console.log('Аватар успешно загружен:', user);
      },
      error: (error: unknown) => {
        console.error('Ошибка загрузки аватара:', error);
        alert('Ошибка загрузки аватара');
      }
    });
  }

  removeAvatar(): void {
    this.avatarService.deleteAvatar().subscribe({
      next: (user: User) => {
        this.currentUser = user;
        this.currentAvatarUrl = user.avatarUrl;
        console.log('Аватар удален:', user);
      },
      error: (error: unknown) => {
        console.error('Ошибка удаления аватара:', error);
        alert('Ошибка удаления аватара');
      }
    });
  }
}

