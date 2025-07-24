import { Injectable, inject } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '@services';

@Injectable({
  providedIn: 'root',
})
export class AdminGuard implements CanActivate {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  canActivate(): boolean {
    if (this.authService.isAdmin()) {
      return true;
    }

    this.router.navigate(['/dashboard']);
    return false;
  }
}
