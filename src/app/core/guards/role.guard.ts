import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree, ActivatedRouteSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable, map, take } from 'rxjs';

import * as AuthSelectors from '@features/auth/store/auth.selectors';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard implements CanActivate {
  constructor(
    private readonly store: Store,
    private readonly router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const requiredRoles = route.data['roles'] as string[];

    return this.store.select(AuthSelectors.selectUserRole).pipe(
      take(1),
      map(userRole => {
        if (!userRole) {
          return this.router.createUrlTree(['/auth/login']);
        }

        if (requiredRoles && requiredRoles.length > 0) {
          if (requiredRoles.includes(userRole)) {
            return true;
          } else {
            // Если у пользователя нет нужной роли, перенаправляем на главную
            return this.router.createUrlTree(['/dashboard']);
          }
        }

        return true;
      })
    );
  }
}
