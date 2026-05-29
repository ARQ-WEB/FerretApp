import { Routes } from '@angular/router';
import { authGuard, adminGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Redirige raíz al dashboard
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },

  // Login — sin layout
  {
    path: 'login',
    loadComponent: () =>
      import('./componentes/login/login').then(m => m.Login)
  },

  // Rutas con layout (sidebar)
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./componentes/layout/layout').then(m => m.Layout),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./componentes/dashboard/dashboard').then(m => m.Dashboard)
      },
      {
        path: 'inventario',
        loadComponent: () =>
          import('./componentes/inventario/inventario').then(m => m.Inventario)
      },
      {
        path: 'ventas',
        loadComponent: () =>
          import('./componentes/ventas/ventas').then(m => m.Ventas)
      },
      {
        path: 'proveedores',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./componentes/proveedores/proveedores').then(m => m.Proveedores)
      },
      {
        path: 'pedidos',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./componentes/pedidos/pedidos').then(m => m.Pedidos)
      },
      {
        path: 'reportes',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./componentes/reportes/reportes').then(m => m.Reportes)
      },
      {
        path: 'usuarios',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./componentes/usuarios/usuarios').then(m => m.Usuarios)
      },
      {
        path: 'auditoria',
        canActivate: [adminGuard],
        loadComponent: () =>
          import('./componentes/auditoria/auditoria').then(m => m.Auditoria)
      }
    ]
  },

  // Ruta no encontrada
  {
    path: '**',
    redirectTo: 'login'
  }
];