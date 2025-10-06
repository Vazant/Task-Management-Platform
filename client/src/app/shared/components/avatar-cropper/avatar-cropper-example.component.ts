import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AvatarService } from '../../../core/services/avatar.service';
import { User } from '../../../core/models/user.model';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-avatar-cropper-example',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  template: `
    <div class="example-container">
      <h2>Пример использования Avatar Cropper</h2>
      
      <div class="current-avatar">
        <img [src]="currentAvatarUrl || 'assets/images/default-avatar.svg'" 
             alt="Current Avatar" 
             class="avatar-image">
        <p>Текущий аватар</p>
      </div>
      
      <div class="actions">
        <input type="file" 
               #fileInput 
               accept="image/*" 
               (change)="onFileSelected($event)"
               style="display: none;">
        
        <button mat-raised-button color="primary" (click)="fileInput.click()">
          Выбрать изображение
        </button>
        
        <button mat-button (click)="removeAvatar()" *ngIf="currentAvatarUrl">
          Удалить аватар
        </button>
      </div>
    </div>
  `,
  styles: [`
    .example-container {
      padding: 20px;
      max-width: 600px;
      margin: 0 auto;
    }
    
    .current-avatar {
      text-align: center;
      margin: 20px 0;
    }
    
    .avatar-image {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid #e0e0e0;
    }
    
    .actions {
      display: flex;
      gap: 12px;
      justify-content: center;
    }
  `]
})
export class AvatarCropperExampleComponent {
  currentUser: User | null = null;
  currentAvatarUrl: string | undefined = undefined;

  constructor(private avatarService: AvatarService) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    
    if (!file) return;
    
    if (!file.type.startsWith('image/')) {
      alert('Пожалуйста, выберите изображение');
      return;
    }
    
    this.onCropped(file);
  }

  onCropped(file: File) {
    this.avatarService.upload(file).subscribe({
      next: (user: User) => {
        this.currentUser = user;
        this.currentAvatarUrl = user.avatarUrl; // avatarUrl?: string
      },
      error: (error: unknown) => {
        console.error(error);
      }
    });
  }

  removeAvatar() {
    this.avatarService.deleteAvatar().subscribe({
      next: (user: User) => {
        this.currentUser = user;
        this.currentAvatarUrl = user.avatarUrl;
      },
      error: (error: unknown) => console.error(error)
    });
  }
}
