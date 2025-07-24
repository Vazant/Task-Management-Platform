import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [RouterLink],
  standalone: true
})
export class DashboardComponent {
  // Dashboard logic will be added here
}
