import { Component, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { ClientService } from '../../../core/services/client.service';
import { ReportService } from '../../../core/services/report.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ClientResponse, ReportResponse } from '../../../core/models';

@Component({
  selector: 'app-report-view',
  imports: [ReactiveFormsModule, DatePipe, DecimalPipe],
  templateUrl: './report-view.html',
  styleUrl: './report-view.css',
})
export class ReportView {
  private readonly fb = inject(FormBuilder);
  private readonly clientService = inject(ClientService);
  private readonly reportService = inject(ReportService);
  private readonly notifier = inject(NotificationService);

  protected readonly clients = signal<ClientResponse[]>([]);
  protected readonly report = signal<ReportResponse | null>(null);
  protected readonly loading = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    clientId: ['', [Validators.required]],
    from: [this.firstDayOfMonth(), [Validators.required]],
    to: [this.todayIso(), [Validators.required]],
  });

  constructor() {
    this.clientService.findAll().subscribe({
      next: (list) => this.clients.set(list),
    });
  }

  protected onGenerate(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const { clientId, from, to } = this.form.getRawValue();
    if (from > to) {
      this.notifier.error('"From" date must be earlier than or equal to "To" date');
      return;
    }
    this.loading.set(true);
    this.reportService.generate(clientId, from, to).subscribe({
      next: (r) => {
        this.report.set(r);
        this.loading.set(false);
      },
      error: () => {
        this.report.set(null);
        this.loading.set(false);
      },
    });
  }

  protected onDownloadPdf(): void {
    const r = this.report();
    if (!r?.pdfBase64) return;
    const bytes = this.base64ToBytes(r.pdfBase64);
    const blob = new Blob([bytes as BlobPart], { type: 'application/pdf' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `statement-${r.clientId}-${r.from}-${r.to}.pdf`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  }

  private base64ToBytes(b64: string): Uint8Array {
    const binary = atob(b64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    return bytes;
  }

  private todayIso(): string {
    return new Date().toISOString().slice(0, 10);
  }

  private firstDayOfMonth(): string {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-01`;
  }
}
