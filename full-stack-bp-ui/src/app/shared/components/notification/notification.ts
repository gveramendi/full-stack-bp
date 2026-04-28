import { Component, inject } from '@angular/core';

import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.html',
  styleUrl: './notification.css',
})
export class Notification {
  protected readonly notifier = inject(NotificationService);
}
