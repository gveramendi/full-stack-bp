import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE } from '../api';
import { MovementRequest, MovementResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class MovementService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE}/movements`;

  findAll(accountId?: number): Observable<MovementResponse[]> {
    const params = accountId != null ? new HttpParams().set('accountId', accountId) : undefined;
    return this.http.get<MovementResponse[]>(this.url, { params });
  }

  findById(id: number): Observable<MovementResponse> {
    return this.http.get<MovementResponse>(`${this.url}/${id}`);
  }

  create(payload: MovementRequest): Observable<MovementResponse> {
    return this.http.post<MovementResponse>(this.url, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
