import { Routes } from '@angular/router';
import { guestGuard } from '../../core/guards/guest.guard';
import { LoginPage } from './login/login-page';

export const AUTH_ROUTES: Routes = [
  {
    path: 'login',
    canActivate: [guestGuard],
    component: LoginPage,
  },
  { path: '', pathMatch: 'full', redirectTo: 'login' },
];
