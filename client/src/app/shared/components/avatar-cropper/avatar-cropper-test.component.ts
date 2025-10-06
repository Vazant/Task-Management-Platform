import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialog } from '@angular/material/dialog';
import { AvatarCropperComponent, CropResult } from './avatar-cropper.component';

@Component({
  selector: 'app-avatar-cropper-test',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatCardModule],
  template: `
    <div class="test-container">
      <mat-card class="test-card">
        <mat-card-header>
          <mat-card-title>Тест Avatar Cropper</mat-card-title>
          <mat-card-subtitle>Проверка работы компонента обрезки</mat-card-subtitle>
        </mat-card-header>
        
        <mat-card-content>
          <div class="test-section">
            <h4>Выберите изображение для обрезки:</h4>
            <input 
              type="file" 
              #fileInput
              (change)="onFileSelected($event)" 
              accept="image/*"
              style="display: none;" />
            
            <button mat-raised-button color="primary" (click)="fileInput.click()">
              Выбрать изображение
            </button>
          </div>
          
          <div class="result-section" *ngIf="result">
            <h4>Результат обрезки:</h4>
            <div class="result-info">
              <p><strong>Размер файла:</strong> {{ result.file.size }} байт</p>
              <p><strong>Тип:</strong> {{ result.file.type }}</p>
              <p><strong>Имя:</strong> {{ result.file.name }}</p>
            </div>
            
            <div class="result-preview">
              <h5>Предварительный просмотр:</h5>
              <img [src]="result.dataUrl" alt="Cropped result" class="preview-image" />
            </div>
          </div>
        </mat-card-content>
      </mat-card>
    </div>
  `,
  styles: [`
    .test-container {
      display: flex;
      justify-content: center;
      padding: 20px;
    }
    
    .test-card {
      max-width: 600px;
      width: 100%;
    }
    
    .test-section {
      margin-bottom: 20px;
    }
    
    .test-section h4 {
      margin-bottom: 16px;
      color: #333;
    }
    
    .result-section {
      margin-top: 20px;
      padding: 16px;
      background: #f5f5f5;
      border-radius: 8px;
    }
    
    .result-section h4 {
      margin-top: 0;
      color: #333;
    }
    
    .result-info {
      margin-bottom: 16px;
    }
    
    .result-info p {
      margin: 8px 0;
      color: #666;
    }
    
    .result-preview h5 {
      margin-bottom: 12px;
      color: #333;
    }
    
    .preview-image {
      width: 200px;
      height: 200px;
      border-radius: 50%;
      object-fit: cover;
      border: 3px solid #2196f3;
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }
  `]
})
export class AvatarCropperTestComponent {
  result: CropResult | null = null;

  constructor(private dialog: MatDialog) {}

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
        this.result = result;
        console.log('Результат обрезки:', result);
      }
    });
  }
}

