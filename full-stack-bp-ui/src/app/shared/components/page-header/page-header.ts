import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-page-header',
  template: `
    <div class="page-header">
      <h1>{{ title() }}</h1>
      <div class="page-header__row">
        <input
          class="page-header__search"
          type="search"
          [placeholder]="searchPlaceholder()"
          [value]="searchValue()"
          (input)="searchChange.emit(($any($event.target)).value)"
        />
        @if (showNew()) {
          <button class="btn btn--primary" type="button" (click)="newClick.emit()">
            {{ newLabel() }}
          </button>
        }
      </div>
    </div>
  `,
  styles: `
    .page-header {
      margin-bottom: var(--space-6);
    }
    .page-header__row {
      display: flex;
      gap: var(--space-4);
      align-items: center;
      justify-content: space-between;
    }
    .page-header__search {
      flex: 1;
      max-width: 320px;
      padding: var(--space-2) var(--space-3);
      border: 1px solid var(--color-border);
      border-radius: var(--radius-md);
    }
    .page-header__search:focus {
      outline: none;
      border-color: var(--color-primary);
      box-shadow: 0 0 0 3px rgba(30, 64, 175, 0.1);
    }
  `,
})
export class PageHeader {
  readonly title = input.required<string>();
  readonly searchValue = input<string>('');
  readonly searchPlaceholder = input<string>('Search');
  readonly newLabel = input<string>('New');
  readonly showNew = input<boolean>(true);

  readonly searchChange = output<string>();
  readonly newClick = output<void>();
}
