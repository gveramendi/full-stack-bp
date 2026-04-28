import { TestBed } from '@angular/core/testing';

import { ConfirmDialog } from './confirm-dialog';

describe('ConfirmDialog', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [ConfirmDialog] });
  });

  it('renders title, message and default button labels', () => {
    const fixture = TestBed.createComponent(ConfirmDialog);
    fixture.componentRef.setInput('title', 'Delete client');
    fixture.componentRef.setInput('message', 'Delete client C-1?');
    fixture.detectChanges();

    const root = fixture.nativeElement as HTMLElement;
    expect(root.querySelector('h2')?.textContent).toContain('Delete client');
    expect(root.querySelector('.confirm-dialog__message')?.textContent).toContain('Delete client C-1?');

    const buttons = Array.from(root.querySelectorAll<HTMLButtonElement>('.confirm-dialog__actions button'));
    expect(buttons.map((b) => b.textContent?.trim())).toEqual(['Cancel', 'Delete']);
  });

  it('emits confirmed when the confirm button is clicked', () => {
    const fixture = TestBed.createComponent(ConfirmDialog);
    fixture.componentRef.setInput('message', 'sure?');
    fixture.detectChanges();

    let confirmed = 0;
    let cancelled = 0;
    fixture.componentInstance.confirmed.subscribe(() => confirmed++);
    fixture.componentInstance.cancelled.subscribe(() => cancelled++);

    const confirmBtn = fixture.nativeElement.querySelector('.btn--danger') as HTMLButtonElement;
    confirmBtn.click();

    expect(confirmed).toBe(1);
    expect(cancelled).toBe(0);
  });

  it('emits cancelled from the cancel button, the close icon and the backdrop', () => {
    const fixture = TestBed.createComponent(ConfirmDialog);
    fixture.componentRef.setInput('message', 'sure?');
    fixture.detectChanges();

    let cancelled = 0;
    fixture.componentInstance.cancelled.subscribe(() => cancelled++);

    const root = fixture.nativeElement as HTMLElement;
    (root.querySelector('.btn--secondary') as HTMLButtonElement).click();
    (root.querySelector('.modal__close') as HTMLButtonElement).click();
    const backdrop = root.querySelector('.modal') as HTMLElement;
    backdrop.dispatchEvent(new MouseEvent('click', { bubbles: true }));

    expect(cancelled).toBe(3);
  });

  it('honors custom button labels', () => {
    const fixture = TestBed.createComponent(ConfirmDialog);
    fixture.componentRef.setInput('message', 'm');
    fixture.componentRef.setInput('confirmLabel', 'Yes, remove');
    fixture.componentRef.setInput('cancelLabel', 'Keep it');
    fixture.detectChanges();

    const labels = Array.from(
      fixture.nativeElement.querySelectorAll<HTMLButtonElement>('.confirm-dialog__actions button')
    ).map((b) => b.textContent?.trim());
    expect(labels).toEqual(['Keep it', 'Yes, remove']);
  });
});
