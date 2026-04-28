import { Component, computed, inject, signal } from '@angular/core';

import { ClientService } from '../../../core/services/client.service';
import { NotificationService } from '../../../core/services/notification.service';
import { ClientCreateRequest, ClientResponse, ClientUpdateRequest } from '../../../core/models';
import { ConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog';
import { Modal } from '../../../shared/components/modal/modal';
import { PageHeader } from '../../../shared/components/page-header/page-header';
import { ClientForm } from '../client-form/client-form';

@Component({
  selector: 'app-client-list',
  imports: [PageHeader, Modal, ConfirmDialog, ClientForm],
  templateUrl: './client-list.html',
  styleUrl: './client-list.css',
})
export class ClientList {
  private readonly clientService = inject(ClientService);
  private readonly notifier = inject(NotificationService);

  protected readonly clients = signal<ClientResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly submitting = signal(false);
  protected readonly searchQuery = signal('');
  protected readonly formOpen = signal(false);
  protected readonly editTarget = signal<ClientResponse | null>(null);
  protected readonly deleteTarget = signal<ClientResponse | null>(null);

  protected readonly filteredClients = computed(() => {
    const q = this.searchQuery().trim().toLowerCase();
    const list = this.clients();
    if (!q) return list;
    return list.filter((c) =>
      [c.clientId, c.name, c.identification, c.phone, c.address, c.gender, c.status]
        .filter((v): v is string => !!v)
        .some((v) => v.toLowerCase().includes(q))
    );
  });

  constructor() {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    this.clientService.findAll().subscribe({
      next: (list) => {
        this.clients.set(list);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  protected onNew(): void {
    this.editTarget.set(null);
    this.formOpen.set(true);
  }

  protected onEdit(client: ClientResponse): void {
    this.editTarget.set(client);
    this.formOpen.set(true);
  }

  protected onDelete(client: ClientResponse): void {
    this.deleteTarget.set(client);
  }

  protected onConfirmDelete(): void {
    const client = this.deleteTarget();
    if (!client) return;
    this.deleteTarget.set(null);
    this.clientService.delete(client.clientId).subscribe({
      next: () => {
        this.notifier.success(`Client ${client.clientId} deleted`);
        this.load();
      },
    });
  }

  protected onCancelDelete(): void {
    this.deleteTarget.set(null);
  }

  protected onCreate(payload: ClientCreateRequest): void {
    this.submitting.set(true);
    this.clientService.create(payload).subscribe({
      next: () => {
        this.notifier.success(`Client ${payload.clientId} created`);
        this.submitting.set(false);
        this.formOpen.set(false);
        this.load();
      },
      error: () => this.submitting.set(false),
    });
  }

  protected onUpdate(event: { clientId: string; payload: ClientUpdateRequest }): void {
    this.submitting.set(true);
    this.clientService.update(event.clientId, event.payload).subscribe({
      next: () => {
        this.notifier.success(`Client ${event.clientId} updated`);
        this.submitting.set(false);
        this.formOpen.set(false);
        this.load();
      },
      error: () => this.submitting.set(false),
    });
  }

  protected onCancel(): void {
    this.formOpen.set(false);
  }
}
