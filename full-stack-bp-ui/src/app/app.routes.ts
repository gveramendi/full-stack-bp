import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'clients' },
  {
    path: 'clients',
    loadComponent: () =>
      import('./features/clients/client-list/client-list').then((m) => m.ClientList),
  },
  {
    path: 'accounts',
    loadComponent: () =>
      import('./features/accounts/account-list/account-list').then((m) => m.AccountList),
  },
  {
    path: 'movements',
    loadComponent: () =>
      import('./features/movements/movement-list/movement-list').then((m) => m.MovementList),
  },
  {
    path: 'reports',
    loadComponent: () =>
      import('./features/reports/report-view/report-view').then((m) => m.ReportView),
  },
  { path: '**', redirectTo: 'clients' },
];
