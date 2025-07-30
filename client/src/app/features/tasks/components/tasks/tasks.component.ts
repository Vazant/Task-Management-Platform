import { Component, OnInit, inject, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '@services';
import { User } from '@models';
import { Subscription } from 'rxjs';
import { LayoutComponent } from '../../../../shared/components/layout';
import { LucideAngularModule, CheckSquare, Plus, Move, Calendar } from 'lucide-angular';

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.scss'],
  standalone: true,
  imports: [CommonModule, LayoutComponent, LucideAngularModule]
})
export class TasksComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  isAuthenticated = false;
  currentUser: User | null = null;
  private authSubscription?: Subscription;
  private userSubscription?: Subscription;



  // Mobile menu state
  isMobileMenuOpen = false;

  // Lucide icons
  readonly CheckSquare = CheckSquare;
  readonly Plus = Plus;
  readonly Move = Move;
  readonly Calendar = Calendar;

  ngOnInit(): void {
    this.authSubscription = this.authService.isAuthenticated$.subscribe(isAuth => {
      this.isAuthenticated = isAuth;
    });

    this.userSubscription = this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  ngOnDestroy(): void {
    this.authSubscription?.unsubscribe();
    this.userSubscription?.unsubscribe();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  changePassword(): void {
    // TODO: Implement change password functionality
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.nav') && this.isMobileMenuOpen) {
      this.isMobileMenuOpen = false;
    }
  }
}
