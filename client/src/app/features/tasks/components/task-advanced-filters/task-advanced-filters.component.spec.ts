import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { TaskAdvancedFiltersComponent } from './task-advanced-filters.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('TaskAdvancedFiltersComponent', () => {
  let component: TaskAdvancedFiltersComponent;
  let fixture: ComponentFixture<TaskAdvancedFiltersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TaskAdvancedFiltersComponent,
        ReactiveFormsModule,
        MatExpansionModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatDatepickerModule,
        MatNativeDateModule,
        BrowserAnimationsModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskAdvancedFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    expect(component.filterForm.value).toEqual({
      query: '',
      status: 'all',
      priority: 'all',
      assignee: 'all',
      project: 'all',
      dueDateFrom: null,
      dueDateTo: null,
      createdFrom: null,
      createdTo: null
    });
  });

  it('should debounce query input', fakeAsync(() => {
    spyOn(component.filtersChange, 'emit');
    
    component.filterForm.get('query')?.setValue('test');
    tick(100);
    expect(component.filtersChange.emit).not.toHaveBeenCalled();
    
    tick(250);
    expect(component.filtersChange.emit).toHaveBeenCalledWith({ query: 'test' });
  }));

  it('should emit immediately for select changes', () => {
    spyOn(component.filtersChange, 'emit');
    
    component.filterForm.get('status')?.setValue('done');
    expect(component.filtersChange.emit).toHaveBeenCalledWith({ status: 'done' });
  });

  it('should update active filters count', () => {
    expect(component.activeFiltersCount).toBe(0);
    
    component.filterForm.patchValue({
      query: 'test',
      status: 'done',
      priority: 'high'
    });
    
    component['updateActiveFiltersCount']();
    expect(component.activeFiltersCount).toBe(3);
  });

  it('should clear all filters', () => {
    spyOn(component.clearFilters, 'emit');
    
    component.filterForm.patchValue({
      query: 'test',
      status: 'done',
      priority: 'high'
    });
    
    component.onClearFilters();
    
    expect(component.filterForm.value.query).toBe('');
    expect(component.filterForm.value.status).toBe('all');
    expect(component.clearFilters.emit).toHaveBeenCalled();
    expect(component.activeFiltersCount).toBe(0);
  });

  it('should emit date range filters', () => {
    spyOn(component.filtersChange, 'emit');
    
    const fromDate = new Date('2024-01-01');
    const toDate = new Date('2024-12-31');
    
    component.filterForm.patchValue({
      dueDateFrom: fromDate,
      dueDateTo: toDate
    });
    
    expect(component.filtersChange.emit).toHaveBeenCalledWith({
      dueDateRange: { from: fromDate, to: toDate }
    });
  });

  it('should remove individual filter', () => {
    component.filterForm.get('status')?.setValue('done');
    component.removeFilter('status');
    expect(component.filterForm.get('status')?.value).toBe('all');
  });
});
