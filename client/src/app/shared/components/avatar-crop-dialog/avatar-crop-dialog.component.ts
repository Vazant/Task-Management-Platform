import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AvatarCropperComponent, CropResult } from '../avatar-cropper/avatar-cropper.component';

export interface AvatarCropDialogData {
  imageFile: File;
  options?: {
    aspectRatio?: number;
    minSize?: number;
    maxSize?: number;
    quality?: number;
  };
}

@Component({
  selector: 'app-avatar-crop-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    AvatarCropperComponent
  ],
  template: `
    <div class="avatar-crop-dialog">
      <app-avatar-cropper
        [imageFile]="data.imageFile"
        (cropped)="onCropped($event)"
        (cancelled)="onCancelled()">
      </app-avatar-cropper>
    </div>
  `,
  styles: [`
    .avatar-crop-dialog {
      width: 100%;
      height: 100%;
      max-width: 90vw;
      max-height: 90vh;
    }
  `]
})
export class AvatarCropDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<AvatarCropDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AvatarCropDialogData
  ) {}

  onCropped(result: CropResult) {
    this.dialogRef.close(result);
  }

  onCancelled() {
    this.dialogRef.close(null);
  }
}
