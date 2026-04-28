import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

import { Notification } from './shared/components/notification/notification';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Notification],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly navigation = [
    { path: '/clients', label: 'Clients' },
    { path: '/accounts', label: 'Accounts' },
    { path: '/movements', label: 'Movements' },
    { path: '/reports', label: 'Reports' },
  ];
}
