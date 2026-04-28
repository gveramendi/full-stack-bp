import { Component, input, output } from '@angular/core';

import { Modal } from '../modal/modal';

@Component({
  selector: 'app-confirm-dialog',
  imports: [Modal],
  template: `
    <app-modal [title]="title()" (closed)="cancelled.emit()">
      <p class="confirm-dialog__message">{{ message() }}</p>
      <div class="confirm-dialog__actions">
        <button class="btn btn--secondary" type="button" (click)="cancelled.emit()">
          {{ cancelLabel() }}
        </button>
        <button class="btn btn--danger" type="button" (click)="confirmed.emit()">
          {{ confirmLabel() }}
        </button>
      </div>
    </app-modal>
  `,
  styles: `
    .confirm-dialog__message {
      margin: 0 0 var(--space-6);
      color: var(--color-text);
    }
    .confirm-dialog__actions {
      display: flex;
      justify-content: flex-end;
      gap: var(--space-2);
    }
  `,
})
export class ConfirmDialog {
  readonly title = input<string>('Confirm');
  readonly message = input.required<string>();
  readonly confirmLabel = input<string>('Delete');
  readonly cancelLabel = input<string>('Cancel');
  readonly confirmed = output<void>();
  readonly cancelled = output<void>();
}
