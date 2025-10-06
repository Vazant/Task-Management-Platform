import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, ViewChild, ElementRef, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { LucideAngularModule, X, Save, ZoomIn, ZoomOut, RotateCcw } from 'lucide-angular';

export interface CropResult {
  file: File;
  blob: Blob;
  dataUrl: string;
}

@Component({
  selector: 'app-avatar-cropper',
  standalone: true,
  encapsulation: ViewEncapsulation.None,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    LucideAngularModule
  ],
  template: `
    <div class="avatar-cropper-container">
      <!-- Header -->
      <div class="cropper-header">
        <h3>Обрезка аватара</h3>
        <button mat-icon-button (click)="onCancel()" class="close-btn" aria-label="Закрыть">
          <i-lucide [img]="X" [size]="20"></i-lucide>
        </button>
      </div>

      <!-- Cropper Body -->
      <div class="cropper-body">
        <div class="cropper-wrapper">
          <canvas 
            #cropperCanvas
            class="cropper-canvas"
            (mousedown)="onMouseDown($event)"
            (mousemove)="onMouseMove($event)"
            (mouseup)="onMouseUp($event)"
            (wheel)="onWheel($event)">
          </canvas>
          <div class="crop-overlay">
            <div class="crop-circle"></div>
          </div>
        </div>
      </div>

      <!-- Controls -->
      <div class="cropper-controls">
        <button mat-button (click)="zoomOut()" aria-label="Уменьшить">
          <i-lucide [img]="ZoomOut" [size]="16"></i-lucide>
        </button>
        <button mat-button (click)="reset()" aria-label="Сбросить">
          <i-lucide [img]="RotateCcw" [size]="16"></i-lucide>
        </button>
        <button mat-button (click)="zoomIn()" aria-label="Увеличить">
          <i-lucide [img]="ZoomIn" [size]="16"></i-lucide>
        </button>
      </div>

      <!-- Actions -->
      <div class="cropper-actions">
        <button mat-button (click)="onCancel()" class="cancel-btn">
          Отмена
        </button>
        <button mat-raised-button color="primary" (click)="save()" class="save-btn">
          <i-lucide [img]="Save" [size]="16"></i-lucide>
          Сохранить
        </button>
      </div>
    </div>
  `,
  styles: [`
    .avatar-cropper-container {
      display: flex;
      flex-direction: column;
      height: 100%;
      max-width: 100vw;
      max-height: 100vh;
      background: #fff;
    }

    .cropper-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem 1.5rem;
      border-bottom: 1px solid #e0e0e0;
      background: #fff;
      flex-shrink: 0;
    }

    .cropper-header h3 {
      margin: 0;
      font-size: 1.25rem;
      font-weight: 600;
      color: #333;
    }

    .close-btn {
      color: #666;
    }

    .close-btn:hover {
      color: #333;
    }

    .cropper-body {
      flex: 1;
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 2rem;
      min-height: 0;
      background: #f8f9fa;
    }

    .cropper-wrapper {
      position: relative;
      width: 400px;
      height: 400px;
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      overflow: hidden;
    }

    .cropper-canvas {
      width: 100%;
      height: 100%;
      cursor: move;
      display: block;
      image-rendering: -webkit-optimize-contrast;
      image-rendering: crisp-edges;
      image-rendering: pixelated;
    }

    .crop-overlay {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .crop-circle {
      width: 200px;
      height: 200px;
      border: 3px solid #2196f3;
      border-radius: 50%;
      box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.5);
      position: relative;
    }

    .crop-circle::before {
      content: '';
      position: absolute;
      top: -3px;
      left: -3px;
      right: -3px;
      bottom: -3px;
      border: 1px solid #fff;
      border-radius: 50%;
    }

    .cropper-controls {
      display: flex;
      justify-content: center;
      gap: 1rem;
      padding: 1rem;
      background: #f5f5f5;
      border-top: 1px solid #e0e0e0;
    }

    .cropper-controls button {
      min-width: 40px;
      padding: 0.5rem;
    }

    .cropper-actions {
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
      padding: 1rem 1.5rem;
      border-top: 1px solid #e0e0e0;
      background: #fff;
      flex-shrink: 0;
    }

    .cancel-btn {
      min-width: 100px;
    }

    .save-btn {
      min-width: 120px;
      display: flex;
      align-items: center;
      gap: 0.5rem;
    }

    /* Responsive design */
    @media (max-width: 768px) {
      .cropper-body {
        padding: 1rem;
      }

      .cropper-wrapper {
        width: 300px;
        height: 300px;
      }

      .crop-circle {
        width: 150px;
        height: 150px;
      }

      .cropper-actions {
        flex-direction: column;
        gap: 0.5rem;
      }

      .cancel-btn,
      .save-btn {
        width: 100%;
      }
    }
  `]
})
export class AvatarCropperComponent implements OnInit, OnDestroy {
  @Input() imageFile: File | undefined = undefined;
  @Output() cropped = new EventEmitter<CropResult>();
  @Output() cancelled = new EventEmitter<void>();

  @ViewChild('cropperCanvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;

  // Icons
  X = X;
  Save = Save;
  ZoomIn = ZoomIn;
  ZoomOut = ZoomOut;
  RotateCcw = RotateCcw;

  private canvas!: HTMLCanvasElement;
  private ctx!: CanvasRenderingContext2D;
  private image!: HTMLImageElement;
  private scale = 1;
  private offsetX = 0;
  private offsetY = 0;
  private isDragging = false;
  private lastMouseX = 0;
  private lastMouseY = 0;

  ngOnInit(): void {
    this.canvas = this.canvasRef.nativeElement;
    this.ctx = this.canvas.getContext('2d')!;
    
    if (this.imageFile) {
      this.loadImage();
    }
  }

  ngOnDestroy(): void {
    // Cleanup
  }

  private loadImage(): void {
    if (!this.imageFile) return;

    const reader = new FileReader();
    reader.onload = (e) => {
      this.image = new Image();
      this.image.onload = () => {
        this.setupCanvas();
        this.drawImage();
      };
      this.image.src = e.target?.result as string;
    };
    reader.readAsDataURL(this.imageFile);
  }

  private setupCanvas(): void {
    if (!this.image) return;

    // Set canvas size to match the container
    const container = this.canvas.parentElement;
    if (container) {
      const rect = container.getBoundingClientRect();
      this.canvas.width = rect.width;
      this.canvas.height = rect.height;
    } else {
      this.canvas.width = 400;
      this.canvas.height = 400;
    }

    // Enable high DPI support
    const dpr = window.devicePixelRatio || 1;
    const rect = this.canvas.getBoundingClientRect();
    this.canvas.width = rect.width * dpr;
    this.canvas.height = rect.height * dpr;
    this.ctx.scale(dpr, dpr);
    this.canvas.style.width = rect.width + 'px';
    this.canvas.style.height = rect.height + 'px';

    // Enable image smoothing for better quality
    this.ctx.imageSmoothingEnabled = true;
    this.ctx.imageSmoothingQuality = 'high';
  }

  private drawImage(): void {
    if (!this.image) return;

    // Get actual canvas display size (not the high DPI size)
    const rect = this.canvas.getBoundingClientRect();
    const canvasWidth = rect.width;
    const canvasHeight = rect.height;
    
    // Clear canvas
    this.ctx.clearRect(0, 0, canvasWidth, canvasHeight);
    
    // Calculate image dimensions to fit canvas while maintaining aspect ratio
    const imageAspect = this.image.width / this.image.height;
    const canvasAspect = canvasWidth / canvasHeight;
    
    let drawWidth, drawHeight;
    if (imageAspect > canvasAspect) {
      // Image is wider than canvas
      drawWidth = canvasWidth;
      drawHeight = drawWidth / imageAspect;
    } else {
      // Image is taller than canvas
      drawHeight = canvasHeight;
      drawWidth = drawHeight * imageAspect;
    }
    
    // Apply scale
    drawWidth *= this.scale;
    drawHeight *= this.scale;
    
    // Calculate position (center the image)
    const x = (canvasWidth - drawWidth) / 2 + this.offsetX;
    const y = (canvasHeight - drawHeight) / 2 + this.offsetY;
    
    // Enable high quality rendering
    this.ctx.save();
    this.ctx.imageSmoothingEnabled = true;
    this.ctx.imageSmoothingQuality = 'high';
    
    // Draw image
    this.ctx.drawImage(this.image, x, y, drawWidth, drawHeight);
    
    this.ctx.restore();
  }

  onMouseDown(event: MouseEvent): void {
    this.isDragging = true;
    this.lastMouseX = event.clientX;
    this.lastMouseY = event.clientY;
    event.preventDefault();
  }

  onMouseMove(event: MouseEvent): void {
    if (!this.isDragging) return;

    const deltaX = event.clientX - this.lastMouseX;
    const deltaY = event.clientY - this.lastMouseY;
    
    this.offsetX += deltaX;
    this.offsetY += deltaY;
    
    this.lastMouseX = event.clientX;
    this.lastMouseY = event.clientY;
    
    this.drawImage();
  }

  onMouseUp(event: MouseEvent): void {
    this.isDragging = false;
  }

  onWheel(event: WheelEvent): void {
    event.preventDefault();
    
    const delta = event.deltaY > 0 ? -0.1 : 0.1;
    this.scale = Math.max(0.5, Math.min(3, this.scale + delta));
    this.drawImage();
  }

  zoomIn(): void {
    this.scale = Math.min(3, this.scale + 0.2);
    this.drawImage();
  }

  zoomOut(): void {
    this.scale = Math.max(0.5, this.scale - 0.2);
    this.drawImage();
  }

  reset(): void {
    this.scale = 1;
    this.offsetX = 0;
    this.offsetY = 0;
    this.drawImage();
  }

  save(): void {
    if (!this.image) return;

    // Create a high-resolution canvas for the cropped image
    const cropCanvas = document.createElement('canvas');
    const cropCtx = cropCanvas.getContext('2d')!;
    
    // Use high resolution for better quality
    const outputSize = 512; // Higher resolution for better quality
    cropCanvas.width = outputSize;
    cropCanvas.height = outputSize;
    
    // Enable high quality rendering
    cropCtx.imageSmoothingEnabled = true;
    cropCtx.imageSmoothingQuality = 'high';
    
    // Calculate crop area from the original image (not the display canvas)
    const rect = this.canvas.getBoundingClientRect();
    const canvasWidth = rect.width;
    const canvasHeight = rect.height;
    
    // Calculate the actual image position and size on the canvas
    const imageAspect = this.image.width / this.image.height;
    const canvasAspect = canvasWidth / canvasHeight;
    
    let imageWidth, imageHeight;
    if (imageAspect > canvasAspect) {
      imageWidth = canvasWidth;
      imageHeight = canvasWidth / imageAspect;
    } else {
      imageHeight = canvasHeight;
      imageWidth = canvasHeight * imageAspect;
    }
    
    // Apply scale
    imageWidth *= this.scale;
    imageHeight *= this.scale;
    
    // Calculate image position
    const imageX = (canvasWidth - imageWidth) / 2 + this.offsetX;
    const imageY = (canvasHeight - imageHeight) / 2 + this.offsetY;
    
    // Calculate crop area (center of the visible area)
    const cropSize = Math.min(imageWidth, imageHeight) * 0.8; // 80% of the smaller dimension
    const cropX = (canvasWidth - cropSize) / 2;
    const cropY = (canvasHeight - cropSize) / 2;
    
    // Calculate source coordinates on the original image
    const sourceX = (cropX - imageX) / imageWidth * this.image.width;
    const sourceY = (cropY - imageY) / imageHeight * this.image.height;
    const sourceSize = cropSize / imageWidth * this.image.width;
    
    // Draw circular crop with high quality
    cropCtx.save();
    cropCtx.beginPath();
    cropCtx.arc(outputSize / 2, outputSize / 2, outputSize / 2, 0, 2 * Math.PI);
    cropCtx.clip();
    
    // Draw the cropped image from the original image (not the canvas)
    cropCtx.drawImage(
      this.image,
      sourceX, sourceY, sourceSize, sourceSize,
      0, 0, outputSize, outputSize
    );
    
    cropCtx.restore();
    
    // Convert to blob with maximum quality
    cropCanvas.toBlob((blob) => {
      if (!blob) return;
      
      const file = new File([blob], 'avatar.png', { type: 'image/png' });
      const dataUrl = cropCanvas.toDataURL('image/png', 1.0); // Maximum quality
      
      const result: CropResult = {
        file: file,
        blob: blob,
        dataUrl: dataUrl
      };
      
      this.cropped.emit(result);
    }, 'image/png', 1.0); // Maximum quality
  }

  onCancel(): void {
    this.cancelled.emit();
  }
}