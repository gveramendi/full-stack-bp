import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE } from '../api';
import { ClientCreateRequest, ClientResponse, ClientUpdateRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE}/clients`;

  findAll(): Observable<ClientResponse[]> {
    return this.http.get<ClientResponse[]>(this.url);
  }

  findById(clientId: string): Observable<ClientResponse> {
    return this.http.get<ClientResponse>(`${this.url}/${encodeURIComponent(clientId)}`);
  }

  create(payload: ClientCreateRequest): Observable<ClientResponse> {
    return this.http.post<ClientResponse>(this.url, payload);
  }

  update(clientId: string, payload: ClientUpdateRequest): Observable<ClientResponse> {
    return this.http.put<ClientResponse>(`${this.url}/${encodeURIComponent(clientId)}`, payload);
  }

  delete(clientId: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${encodeURIComponent(clientId)}`);
  }
}
