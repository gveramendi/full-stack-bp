import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ClientResponse } from '../../../core/models';
import { ClientList } from './client-list';

function buildClient(partial: Partial<ClientResponse>): ClientResponse {
  return {
    clientId: 'C-1',
    name: 'Jane',
    gender: 'FEMALE',
    age: 30,
    identification: 'X1',
    address: null,
    phone: null,
    status: 'ACTIVE',
    createdAt: '',
    updatedAt: '',
    ...partial,
  };
}

describe('ClientList', () => {
  let httpController: HttpTestingController;
  let fixture: ReturnType<typeof TestBed.createComponent<ClientList>>;

  const seedClients: ClientResponse[] = [
    buildClient({ clientId: 'C-1', name: 'Jane Doe', identification: '111' }),
    buildClient({ clientId: 'C-2', name: 'John Smith', identification: '222', status: 'INACTIVE' }),
    buildClient({ clientId: 'C-3', name: 'Alice Roe', identification: '333' }),
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ClientList],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpController = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(ClientList);
  });

  afterEach(() => httpController.verify());

  it('loads clients on init', () => {
    fixture.detectChanges();
    httpController.expectOne('/api/clients').flush(seedClients);
    fixture.detectChanges();

    const rows = fixture.nativeElement.querySelectorAll('tbody tr');
    expect(rows.length).toBe(3);
  });

  it('filters rows by search query (case-insensitive, matches across fields)', async () => {
    fixture.detectChanges();
    httpController.expectOne('/api/clients').flush(seedClients);
    await fixture.whenStable();
    fixture.detectChanges();

    const cmp = fixture.componentInstance as unknown as { searchQuery: { set: (v: string) => void } };
    cmp.searchQuery.set('jane');
    fixture.detectChanges();
    await fixture.whenStable();

    const rows = fixture.nativeElement.querySelectorAll('tbody tr');
    expect(rows.length).toBe(1);
    expect(rows[0].textContent).toContain('Jane Doe');
  });

  describe('delete flow', () => {
    function clickRowDelete(rowIndex: number): void {
      const row = fixture.nativeElement.querySelectorAll('tbody tr')[rowIndex] as HTMLElement;
      const buttons = row.querySelectorAll<HTMLButtonElement>('.data-table__actions button');
      buttons[1].click();
      fixture.detectChanges();
    }

    function dialog(): HTMLElement | null {
      return fixture.nativeElement.querySelector('app-confirm-dialog');
    }

    beforeEach(() => {
      fixture.detectChanges();
      httpController.expectOne('/api/clients').flush(seedClients);
      fixture.detectChanges();
    });

    it('opens the confirm dialog with the client identity in the message', () => {
      expect(dialog()).toBeNull();

      clickRowDelete(0);

      const message = dialog()?.querySelector('.confirm-dialog__message')?.textContent ?? '';
      expect(message).toContain('C-1');
      expect(message).toContain('Jane Doe');
    });

    it('cancel closes the dialog without firing DELETE', () => {
      clickRowDelete(0);

      (dialog()!.querySelector('.btn--secondary') as HTMLButtonElement).click();
      fixture.detectChanges();

      expect(dialog()).toBeNull();
      // afterEach httpController.verify() asserts no DELETE was issued
    });

    it('confirm fires DELETE and reloads the list', () => {
      clickRowDelete(0);

      (dialog()!.querySelector('.btn--danger') as HTMLButtonElement).click();
      fixture.detectChanges();

      const del = httpController.expectOne('/api/clients/C-1');
      expect(del.request.method).toBe('DELETE');
      del.flush(null);

      httpController.expectOne('/api/clients').flush(seedClients.slice(1));
      fixture.detectChanges();

      expect(dialog()).toBeNull();
      expect(fixture.nativeElement.querySelectorAll('tbody tr').length).toBe(2);
    });
  });
});
