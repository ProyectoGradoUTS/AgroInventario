import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { MainLayoutComponent } from './shared/layout/main-layout/main-layout';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then((m) => m.AUTH_ROUTES),
  },
  {
    path: 'app',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./features/dashboard/dashboard.routes').then((m) => m.DASHBOARD_ROUTES),
      },
      {
        path: 'productos',
        loadChildren: () =>
          import('./features/productos/productos.routes').then((m) => m.PRODUCTOS_ROUTES),
      },
      {
        path: 'categorias',
        loadChildren: () =>
          import('./features/categorias/categorias.routes').then((m) => m.CATEGORIAS_ROUTES),
      },
      {
        path: 'inventario',
        loadChildren: () =>
          import('./features/inventario/inventario.routes').then((m) => m.INVENTARIO_ROUTES),
      },
      {
        path: 'movimientos',
        loadChildren: () =>
          import('./features/movimientos/movimientos.routes').then((m) => m.MOVIMIENTOS_ROUTES),
      },
      {
        path: 'alertas',
        loadChildren: () =>
          import('./features/alertas/alertas.routes').then((m) => m.ALERTAS_ROUTES),
      },
      {
        path: 'auditoria',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadChildren: () =>
          import('./features/auditoria/auditoria.routes').then((m) => m.AUDITORIA_ROUTES),
      },
      {
        path: 'usuarios',
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] },
        loadChildren: () =>
          import('./features/usuarios/usuarios.routes').then((m) => m.USUARIOS_ROUTES),
      },
      {
        path: 'reportes',
        loadChildren: () =>
          import('./features/reportes/reportes.routes').then((m) => m.REPORTES_ROUTES),
      },
      {
        path: 'perfil',
        loadChildren: () => import('./features/perfil/perfil.routes').then((m) => m.PERFIL_ROUTES),
      },
      {
        path: 'asistente-ia',
        loadChildren: () =>
          import('./features/asistenteIA/asistente-ia.routes').then((m) => m.ASISTENTE_IA_ROUTES),
      },
    ],
  },
  { path: '', pathMatch: 'full', redirectTo: 'app/dashboard' },
  { path: '**', redirectTo: 'app/dashboard' },
];
