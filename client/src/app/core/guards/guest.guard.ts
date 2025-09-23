import { Injectable, inject } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '@services';



@Injectable({
  providedIn: 'root',
})
export class GuestGuard implements CanActivate {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  canActivate(): boolean {
    // Если пользователь аутентифицирован, перенаправляем на dashboard
    if (this.authService.isAuthenticated && this.authService.checkTokenValidity()) {
      this.router.navigate(['/dashboard']);
      return false;
    }

    return true;
  }
}
