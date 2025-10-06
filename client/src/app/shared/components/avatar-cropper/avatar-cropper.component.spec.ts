import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AvatarCropperComponent } from './avatar-cropper.component';

describe('AvatarCropperComponent', () => {
  let component: AvatarCropperComponent;
  let fixture: ComponentFixture<AvatarCropperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvatarCropperComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AvatarCropperComponent);
    component = fixture.componentInstance;
    
    // Создаем тестовый файл
    const file = new File(['test'], 'test.jpg', { type: 'image/jpeg' });
    component.imageFile = file;
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.selectedSize).toBe('medium');
    expect(component.customSize).toBe(128);
    expect(component.zoom).toBe(100);
    expect(component.rotation).toBe(0);
  });

  it('should change size when selected', () => {
    component.onSizeChange('large');
    expect(component.selectedSize).toBe('large');
    expect(component.cropArea.width).toBe(256);
    expect(component.cropArea.height).toBe(256);
  });

  it('should change zoom when slider changes', () => {
    component.onZoomChange();
    expect(component.zoom).toBe(100);
  });

  it('should reset crop area', () => {
    component.zoom = 150;
    component.rotation = 45;
    component.resetCrop();
    
    expect(component.zoom).toBe(100);
    expect(component.rotation).toBe(0);
  });
});
