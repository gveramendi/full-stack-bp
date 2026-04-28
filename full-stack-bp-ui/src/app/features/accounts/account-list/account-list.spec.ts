import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { AccountResponse, ClientResponse } from '../../../core/models';
import { AccountList } from './account-list';

function buildAccount(partial: Partial<AccountResponse>): AccountResponse {
  return {
    id: 1,
    accountNumber: 'A-1',
    accountType: 'SAVINGS',
    initialBalance: 100,
    currentBalance: 100,
    status: 'ACTIVE',
    clientId: 'C-1',
    clientName: 'Jane Doe',
    createdAt: '',
    updatedAt: '',
    ...partial,
  };
}

describe('AccountList', () => {
  let httpController: HttpTestingController;
  let fixture: ReturnType<typeof TestBed.createComponent<AccountList>>;

  const seedAccounts: AccountResponse[] = [
    buildAccount({ id: 1, accountNumber: 'A-1' }),
    buildAccount({ id: 2, accountNumber: 'A-2', status: 'INACTIVE' }),
  ];
  const seedClients: ClientResponse[] = [];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AccountList],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpController = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(AccountList);
  });

  afterEach(() => httpController.verify());

  function flushInitialLoad(): void {
    fixture.detectChanges();
    httpController.expectOne('/api/accounts').flush(seedAccounts);
    httpController.expectOne('/api/clients').flush(seedClients);
    fixture.detectChanges();
  }

  function clickRowDelete(rowIndex: number): void {
    const row = fixture.nativeElement.querySelectorAll('tbody tr')[rowIndex] as HTMLElement;
    const buttons = row.querySelectorAll<HTMLButtonElement>('.data-table__actions button');
    buttons[1].click();
    fixture.detectChanges();
  }

  function dialog(): HTMLElement | null {
    return fixture.nativeElement.querySelector('app-confirm-dialog');
  }

  describe('delete flow', () => {
    beforeEach(() => flushInitialLoad());

    it('opens the confirm dialog with the account number in the message', () => {
      expect(dialog()).toBeNull();

      clickRowDelete(0);

      const message = dialog()?.querySelector('.confirm-dialog__message')?.textContent ?? '';
      expect(message).toContain('A-1');
    });

    it('cancel closes the dialog without firing DELETE', () => {
      clickRowDelete(0);

      (dialog()!.querySelector('.btn--secondary') as HTMLButtonElement).click();
      fixture.detectChanges();

      expect(dialog()).toBeNull();
    });

    it('confirm fires DELETE on /api/accounts/{id} and reloads', () => {
      clickRowDelete(0);

      (dialog()!.querySelector('.btn--danger') as HTMLButtonElement).click();
      fixture.detectChanges();

      const del = httpController.expectOne('/api/accounts/1');
      expect(del.request.method).toBe('DELETE');
      del.flush(null);

      httpController.expectOne('/api/accounts').flush(seedAccounts.slice(1));
      httpController.expectOne('/api/clients').flush(seedClients);
      fixture.detectChanges();

      expect(dialog()).toBeNull();
      expect(fixture.nativeElement.querySelectorAll('tbody tr').length).toBe(1);
    });
  });
});
