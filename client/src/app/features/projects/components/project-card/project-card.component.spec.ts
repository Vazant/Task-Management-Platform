import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProjectCardComponent } from './project-card.component';
import { Project } from '@models';

describe('ProjectCardComponent', () => {
  let component: ProjectCardComponent;
  let fixture: ComponentFixture<ProjectCardComponent>;

  const mockProject: Project = {
    id: '1',
    name: 'Test Project',
    description: 'This is a test project description',
    ownerId: 'user1',
    members: ['user1', 'user2'],
    settings: {
      allowGuestAccess: false,
      defaultTaskPriority: 'medium',
      autoAssignTasks: true,
      requireTimeTracking: true
    },
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-02')
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ProjectCardComponent);
    component = fixture.componentInstance;
    component.project = mockProject;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display project name', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('Test Project');
  });

  it('should display project description', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('This is a test project description');
  });

  it('should emit select event when project is selected', () => {
    spyOn(component.select, 'emit');
    const event = new Event('click');

    component.onSelect(event);

    expect(component.select.emit).toHaveBeenCalledWith(mockProject.id);
  });

  it('should emit deselect event when project is deselected', () => {
    component.isSelected = true;
    spyOn(component.deselect, 'emit');
    const event = new Event('click');

    component.onSelect(event);

    expect(component.deselect.emit).toHaveBeenCalledWith(mockProject.id);
  });

  it('should emit edit event when edit is clicked', () => {
    spyOn(component.edit, 'emit');
    const event = new Event('click');

    component.onEdit(event);

    expect(component.edit.emit).toHaveBeenCalledWith(mockProject.id);
  });

  it('should emit delete event when delete is clicked', () => {
    spyOn(component.delete, 'emit');
    const event = new Event('click');

    component.onDelete(event);

    expect(component.delete.emit).toHaveBeenCalledWith(mockProject.id);
  });

  it('should format date correctly', () => {
    const date = new Date('2024-01-01');
    const formatted = component.getFormattedDate(date);

    expect(formatted).toBeTruthy();
    expect(typeof formatted).toBe('string');
  });

  it('should return correct members count', () => {
    const count = component.getMembersCount();
    expect(count).toBe(2);
  });

  it('should return truncated description for compact mode', () => {
    component.compact = true;
    const description = component.getDescription();

    expect(description.length).toBeLessThanOrEqual(63); // 60 + 3 for "..."
  });

  it('should return truncated name for compact mode', () => {
    component.compact = true;
    const name = component.getProjectName();

    expect(name.length).toBeLessThanOrEqual(23); // 20 + 3 for "..."
  });

  it('should track by project id', () => {
    const result = component.trackByFn(0, mockProject);
    expect(result).toBe(mockProject.id);
  });
});
