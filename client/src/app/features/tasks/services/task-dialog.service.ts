import { Injectable, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { Task, TaskStatus } from '@models';
import { TaskDialogComponent, TaskDialogData } from '../components/task-dialog/task-dialog.component';

@Injectable({
  providedIn: 'root'
})
export class TaskDialogService {
  private readonly dialog = inject(MatDialog);

  openCreateDialog(initialStatus?: TaskStatus): Observable<boolean> {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      width: '600px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      disableClose: true,
      data: {
        mode: 'create',
        initialStatus
      } as TaskDialogData
    });

    return dialogRef.afterClosed();
  }

  openEditDialog(task: Task): Observable<boolean> {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      width: '600px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      disableClose: true,
      data: {
        mode: 'edit',
        task
      } as TaskDialogData
    });

    return dialogRef.afterClosed();
  }

  openQuickCreateDialog(status: TaskStatus): Observable<boolean> {
    const dialogRef = this.dialog.open(TaskDialogComponent, {
      width: '500px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      disableClose: true,
      data: {
        mode: 'create',
        initialStatus: status
      } as TaskDialogData
    });

    return dialogRef.afterClosed();
  }
}
