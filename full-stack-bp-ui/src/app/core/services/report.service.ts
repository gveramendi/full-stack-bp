import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_BASE } from '../api';
import { ReportResponse } from '../models';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly http = inject(HttpClient);
  private readonly url = `${API_BASE}/reports`;

  generate(clientId: string, from: string, to: string): Observable<ReportResponse> {
    const params = new HttpParams()
      .set('clientId', clientId)
      .set('from', from)
      .set('to', to);
    return this.http.get<ReportResponse>(this.url, { params });
  }
}
