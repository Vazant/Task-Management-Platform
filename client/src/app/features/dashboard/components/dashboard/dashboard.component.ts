import { Component, OnInit, inject, OnDestroy } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '@services';
import { User } from '@models';
import { Subscription } from 'rxjs';
import { LayoutComponent } from '../../../../shared/components/layout';
import { LucideAngularModule, ChartColumn, CheckSquare, Clock, CheckCircle, Plus, FileText, Play, TrendingUp, PartyPopper, ClipboardList } from 'lucide-angular';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  standalone: true,
  imports: [RouterLink, LayoutComponent, LucideAngularModule]
})
export class DashboardComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  currentUser: User | null = null;
  private userSubscription: Subscription | null = null;

  // Lucide icons
  readonly ChartColumn = ChartColumn;
  readonly CheckSquare = CheckSquare;
  readonly Clock = Clock;
  readonly CheckCircle = CheckCircle;
  readonly Plus = Plus;
  readonly FileText = FileText;
  readonly Play = Play;
  readonly TrendingUp = TrendingUp;
  readonly PartyPopper = PartyPopper;
  readonly ClipboardList = ClipboardList;





  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(
      user => this.currentUser = user
    );
  }

  ngOnDestroy(): void {
    if (this.userSubscription) {
      this.userSubscription.unsubscribe();
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  changePassword(): void {
    // Change password functionality will be implemented
  }
}
