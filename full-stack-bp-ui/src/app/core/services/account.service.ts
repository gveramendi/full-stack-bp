import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE } from '../api';
import { AccountCreateRequest, AccountResponse, AccountUpdateRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE}/accounts`;

  findAll(clientId?: string): Observable<AccountResponse[]> {
    const params = clientId ? new HttpParams().set('clientId', clientId) : undefined;
    return this.http.get<AccountResponse[]>(this.url, { params });
  }

  findById(id: number): Observable<AccountResponse> {
    return this.http.get<AccountResponse>(`${this.url}/${id}`);
  }

  create(payload: AccountCreateRequest): Observable<AccountResponse> {
    return this.http.post<AccountResponse>(this.url, payload);
  }

  update(id: number, payload: AccountUpdateRequest): Observable<AccountResponse> {
    return this.http.put<AccountResponse>(`${this.url}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
