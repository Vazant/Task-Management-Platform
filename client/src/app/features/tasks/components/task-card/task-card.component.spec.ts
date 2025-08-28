import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskCardComponent } from './task-card.component';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatMenuModule } from '@angular/material/menu';
import { LucideAngularModule } from 'lucide-angular';
import { Task } from '@models';
import { DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

describe('TaskCardComponent', () => {
  let component: TaskCardComponent;
  let fixture: ComponentFixture<TaskCardComponent>;
  let mockTask: Task;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TaskCardComponent,
        MatCardModule,
        MatChipsModule,
        MatIconModule,
        MatButtonModule,
        MatTooltipModule,
        MatMenuModule,
        LucideAngularModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskCardComponent);
    component = fixture.componentInstance;
    
    mockTask = {
      id: '1',
      title: 'Test Task',
      description: 'Test Description',
      status: 'in-progress',
      priority: 'high',
      projectId: 'p1',
      creatorId: 'u1',
      assigneeId: 'u2',
      dueDate: new Date('2024-12-31'),
      timeSpent: 5,
      labels: [{ name: 'Feature', color: '#3f51b5' }],
      subtasks: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };
    
    component.task = mockTask;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display task title', () => {
    const titleElement = fixture.debugElement.query(By.css('mat-card-title'));
    expect(titleElement.nativeElement.textContent).toContain('Test Task');
  });

  it('should display status chip with correct color', () => {
    const statusChip = fixture.debugElement.query(By.css('mat-chip'));
    expect(statusChip.nativeElement.textContent.trim()).toBe('in-progress');
    expect(component.getStatusColor('in-progress')).toBe('primary');
  });

  it('should display priority with icon', () => {
    const chips = fixture.debugElement.queryAll(By.css('mat-chip'));
    const priorityChip = chips[1];
    expect(priorityChip.nativeElement.textContent).toContain('high');
    expect(component.getPriorityIcon('high')).toBe('arrow_upward');
  });

  it('should show overdue indicator for past due dates', () => {
    component.task = { ...mockTask, dueDate: new Date('2020-01-01') };
    fixture.detectChanges();
    
    expect(component.isOverdue(component.task.dueDate)).toBeTruthy();
    const dueDateElement = fixture.debugElement.query(By.css('.info-item.overdue'));
    expect(dueDateElement).toBeTruthy();
  });

  it('should emit edit event', () => {
    spyOn(component.edit, 'emit');
    const editButton = fixture.debugElement.query(By.css('[mat-menu-item]'));
    editButton.nativeElement.click();
    expect(component.edit.emit).toHaveBeenCalledWith(mockTask);
  });

  it('should emit statusChange event', () => {
    spyOn(component.statusChange, 'emit');
    component.onStatusChange('done');
    expect(component.statusChange.emit).toHaveBeenCalledWith({
      task: mockTask,
      status: 'done'
    });
  });

  it('should hide description in compact mode', () => {
    component.compact = true;
    fixture.detectChanges();
    
    const subtitle = fixture.debugElement.query(By.css('mat-card-subtitle'));
    expect(subtitle).toBeFalsy();
  });

  it('should format date correctly', () => {
    const formatted = component.formatDate(new Date('2024-12-31'));
    expect(formatted).toBe('Dec 31');
  });

  it('should handle missing due date', () => {
    const formatted = component.formatDate(undefined);
    expect(formatted).toBe('No due date');
  });
});
