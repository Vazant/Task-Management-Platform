import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Project } from '@models';

@Component({
  selector: 'app-project-overview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './project-overview.component.html',
  styleUrls: ['./project-overview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectOverviewComponent {
  @Input() project: Project | null = null;
}


