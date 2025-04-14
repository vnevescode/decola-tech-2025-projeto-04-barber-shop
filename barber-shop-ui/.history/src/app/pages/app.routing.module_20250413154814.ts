import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { RoleAdminGuard } from './core/guards/role-admin.guard';
import { RoleUserGuard } from './core/guards/role-user.guard';

const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
  
    {
      path: 'login',
      loadChildren: () =>
        import('./pages/auth/auth.module').then(m => m.AuthModule),
    },
    {
      path: 'register',
      loadChildren: () =>
        import('./pages/auth/auth.module').then(m => m.AuthModule),
    },
    {
      path: 'onboarding',
      canActivate: [AuthGuard],
      loadChildren: () =>
        import('./pages/onboarding/onboarding.module').then(m => m.OnboardingModule),
    },
    {
      path: 'dashboard',
      canActivate: [AuthGuard, RoleUserGuard],
      loadChildren: () =>
        import('./pages/dashboard/dashboard.module').then(m => m.DashboardModule),
    },
    {
      path: 'admin',
      canActivate: [AuthGuard, RoleAdminGuard],
      loadChildren: () =>
        import('./pages/admin/admin.module').then(m => m.AdminModule),
    },
    { path: '**', redirectTo: 'login' }
  ];
  
  @NgModule({
    imports: [RouterModule.forRoot(routes, { bindToComponentInputs: true })],
    exports: [RouterModule],
  })
  export class AppRoutingModule {}