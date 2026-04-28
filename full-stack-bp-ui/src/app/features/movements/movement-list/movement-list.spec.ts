import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { AccountResponse, MovementResponse } from '../../../core/models';
import { MovementList } from './movement-list';

function buildMovement(partial: Partial<MovementResponse>): MovementResponse {
  return {
    id: 1,
    date: '2026-04-27T10:00:00',
    movementType: 'DEPOSIT',
    value: 100,
    balance: 200,
    accountId: 1,
    accountNumber: 'A-1',
    ...partial,
  };
}

describe('MovementList', () => {
  let httpController: HttpTestingController;
  let fixture: ReturnType<typeof TestBed.createComponent<MovementList>>;

  const seedMovements: MovementResponse[] = [
    buildMovement({ id: 10, date: '2026-04-27T10:00:00' }),
    buildMovement({ id: 11, date: '2026-04-26T10:00:00', movementType: 'WITHDRAWAL', value: -50 }),
  ];
  const seedAccounts: AccountResponse[] = [];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MovementList],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpController = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(MovementList);
  });

  afterEach(() => httpController.verify());

  function flushInitialLoad(): void {
    fixture.detectChanges();
    httpController.expectOne('/api/movements').flush(seedMovements);
    httpController.expectOne('/api/accounts').flush(seedAccounts);
    fixture.detectChanges();
  }

  function clickRowDelete(rowIndex: number): void {
    const row = fixture.nativeElement.querySelectorAll('tbody tr')[rowIndex] as HTMLElement;
    const button = row.querySelector('.data-table__actions button') as HTMLButtonElement;
    button.click();
    fixture.detectChanges();
  }

  function dialog(): HTMLElement | null {
    return fixture.nativeElement.querySelector('app-confirm-dialog');
  }

  describe('delete flow', () => {
    beforeEach(() => flushInitialLoad());

    it('opens the confirm dialog with the movement id and account in the message', () => {
      expect(dialog()).toBeNull();

      clickRowDelete(0);

      const message = dialog()?.querySelector('.confirm-dialog__message')?.textContent ?? '';
      expect(message).toContain('#10');
      expect(message).toContain('A-1');
    });

    it('cancel closes the dialog without firing DELETE', () => {
      clickRowDelete(0);

      (dialog()!.querySelector('.btn--secondary') as HTMLButtonElement).click();
      fixture.detectChanges();

      expect(dialog()).toBeNull();
    });

    it('confirm fires DELETE on /api/movements/{id} and reloads', () => {
      clickRowDelete(0);

      (dialog()!.querySelector('.btn--danger') as HTMLButtonElement).click();
      fixture.detectChanges();

      const del = httpController.expectOne('/api/movements/10');
      expect(del.request.method).toBe('DELETE');
      del.flush(null);

      httpController.expectOne('/api/movements').flush(seedMovements.slice(1));
      httpController.expectOne('/api/accounts').flush(seedAccounts);
      fixture.detectChanges();

      expect(dialog()).toBeNull();
      expect(fixture.nativeElement.querySelectorAll('tbody tr').length).toBe(1);
    });
  });
});
