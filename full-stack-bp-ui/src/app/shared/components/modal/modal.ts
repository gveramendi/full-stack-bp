import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-modal',
  template: `
    <div class="modal" (click)="onBackdrop($event)">
      <div class="modal__panel" role="dialog" aria-modal="true">
        <header class="modal__header">
          <h2>{{ title() }}</h2>
          <button class="modal__close" type="button" (click)="closed.emit()">×</button>
        </header>
        <div class="modal__body">
          <ng-content />
        </div>
      </div>
    </div>
  `,
  styles: `
    .modal {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0.4);
      display: flex;
      align-items: flex-start;
      justify-content: center;
      padding-top: 80px;
      z-index: 900;
    }
    .modal__panel {
      background: white;
      width: 100%;
      max-width: 560px;
      border-radius: var(--radius-lg);
      box-shadow: var(--shadow-lg);
      max-height: calc(100vh - 120px);
      display: flex;
      flex-direction: column;
    }
    .modal__header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: var(--space-4) var(--space-6);
      border-bottom: 1px solid var(--color-border);
    }
    .modal__header h2 {
      margin: 0;
      font-size: 18px;
    }
    .modal__close {
      background: transparent;
      border: 0;
      font-size: 22px;
      line-height: 1;
      cursor: pointer;
      color: var(--color-muted);
    }
    .modal__close:hover {
      color: var(--color-text);
    }
    .modal__body {
      padding: var(--space-6);
      overflow: auto;
    }
  `,
})
export class Modal {
  readonly title = input.required<string>();
  readonly closed = output<void>();

  onBackdrop(event: MouseEvent): void {
    if (event.target === event.currentTarget) {
      this.closed.emit();
    }
  }
}
