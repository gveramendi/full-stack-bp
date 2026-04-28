import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ClientCreateRequest, ClientResponse } from '../models';
import { ClientService } from './client.service';

describe('ClientService', () => {
  let service: ClientService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ClientService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify();
  });

  it('GET /api/clients returns the array of clients', () => {
    const fixture: ClientResponse[] = [
      {
        clientId: 'C-1',
        name: 'Jane',
        gender: 'FEMALE',
        age: 30,
        identification: 'X1',
        address: null,
        phone: null,
        status: 'ACTIVE',
        createdAt: '2026-01-01T00:00:00',
        updatedAt: '2026-01-01T00:00:00',
      },
    ];

    let received: ClientResponse[] | undefined;
    service.findAll().subscribe((list) => (received = list));

    const req = httpController.expectOne('/api/clients');
    expect(req.request.method).toBe('GET');
    req.flush(fixture);

    expect(received).toEqual(fixture);
  });

  it('POST /api/clients sends the create payload', () => {
    const payload: ClientCreateRequest = {
      clientId: 'C-2',
      name: 'John',
      gender: 'MALE',
      age: 40,
      identification: 'X2',
      password: 'secret',
      status: 'ACTIVE',
    };

    service.create(payload).subscribe();

    const req = httpController.expectOne('/api/clients');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);
    req.flush({ ...payload, address: null, phone: null, createdAt: '', updatedAt: '' });
  });

  it('DELETE /api/clients/{id} hits the right URL with encoded id', () => {
    service.delete('C/1').subscribe();
    const req = httpController.expectOne('/api/clients/C%2F1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
