import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleUserGuard } from './core/guards/role-user.guard';
import { RoleAdminGuard } from './core/guards/role-admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  {
    path: 'auth',
    loadChildren: () =>
      import('./pages/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  },
  {
    path: 'onboarding',
    canActivate: [AuthGuard],
    loadChildren: () =>
      import('./pages/onboarding/onboarding.routes').then(
        (m) => m.ONBOARDING_ROUTES
      ),
  },
  {
    path: 'dashboard',
    canActivate: [AuthGuard, RoleUserGuard],
    loadChildren: () =>
      import('./pages/dashboard/dashboard.routes').then(
        (m) => m.DASHBOARD_ROUTES
      ),
  },
  {
    path: 'admin',
    canActivate: [AuthGuard, RoleAdminGuard],
    loadChildren: () =>
      import('./pages/admin/admin.routes').then((m) => m.ADMIN_ROUTES),
  },
  { path: '**', redirectTo: 'auth/login' },
];
