import { Injectable } from '@angular/core';
import {
  CanActivate,
  Router,
  ActivatedRouteSnapshot,
  UrlTree,
} from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable()
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    const expectedRole = route.data['expectedRole']; // ex: 'ROLE_ADMIN'
    const userRole = this.authService.getUserRole(); // implementado no AuthService

    if (userRole === expectedRole) {
      return true;
    }

    // Redireciona se a role for inválida
    return this.router.createUrlTree(['/login']);
  }
}
