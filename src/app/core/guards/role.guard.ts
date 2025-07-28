import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { Store } from '@ngrx/store';
import { map, take } from 'rxjs';

import * as AuthSelectors from '@features/auth/store/auth.selectors';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(Store);
  const router = inject(Router);
  
  const requiredRoles = route.data['roles'] as string[];

  return store.select(AuthSelectors.selectUserRole).pipe(
    take(1),
    map(userRole => {
      if (!userRole) {
        return router.createUrlTree(['/auth/login']);
      }

      if (requiredRoles && requiredRoles.length > 0) {
        if (requiredRoles.includes(userRole)) {
          return true;
        } else {
          // Если у пользователя нет нужной роли, перенаправляем на главную
          return router.createUrlTree(['/dashboard']);
        }
      }

      return true;
    })
  );
};
