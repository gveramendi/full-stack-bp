import { Component, computed, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { forkJoin } from 'rxjs';

import { AccountService } from '../../../core/services/account.service';
import { ClientService } from '../../../core/services/client.service';
import { NotificationService } from '../../../core/services/notification.service';
import {
  AccountCreateRequest,
  AccountResponse,
  AccountUpdateRequest,
  ClientResponse,
} from '../../../core/models';
import { ConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog';
import { Modal } from '../../../shared/components/modal/modal';
import { PageHeader } from '../../../shared/components/page-header/page-header';
import { AccountForm } from '../account-form/account-form';

@Component({
  selector: 'app-account-list',
  imports: [DecimalPipe, PageHeader, Modal, ConfirmDialog, AccountForm],
  templateUrl: './account-list.html',
  styleUrl: './account-list.css',
})
export class AccountList {
  private readonly accountService = inject(AccountService);
  private readonly clientService = inject(ClientService);
  private readonly notifier = inject(NotificationService);

  protected readonly accounts = signal<AccountResponse[]>([]);
  protected readonly clients = signal<ClientResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly submitting = signal(false);
  protected readonly searchQuery = signal('');
  protected readonly formOpen = signal(false);
  protected readonly editTarget = signal<AccountResponse | null>(null);
  protected readonly deleteTarget = signal<AccountResponse | null>(null);

  protected readonly filteredAccounts = computed(() => {
    const q = this.searchQuery().trim().toLowerCase();
    const list = this.accounts();
    if (!q) return list;
    return list.filter((a) =>
      [a.accountNumber, a.accountType, a.clientId, a.clientName, a.status]
        .filter((v): v is string => !!v)
        .some((v) => v.toLowerCase().includes(q))
    );
  });

  constructor() {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    forkJoin({
      accounts: this.accountService.findAll(),
      clients: this.clientService.findAll(),
    }).subscribe({
      next: ({ accounts, clients }) => {
        this.accounts.set(accounts);
        this.clients.set(clients);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  protected onNew(): void {
    if (this.clients().length === 0) {
      this.notifier.error('Create a client first before opening an account');
      return;
    }
    this.editTarget.set(null);
    this.formOpen.set(true);
  }

  protected onEdit(account: AccountResponse): void {
    this.editTarget.set(account);
    this.formOpen.set(true);
  }

  protected onDelete(account: AccountResponse): void {
    this.deleteTarget.set(account);
  }

  protected onConfirmDelete(): void {
    const account = this.deleteTarget();
    if (!account) return;
    this.deleteTarget.set(null);
    this.accountService.delete(account.id).subscribe({
      next: () => {
        this.notifier.success(`Account ${account.accountNumber} deleted`);
        this.load();
      },
    });
  }

  protected onCancelDelete(): void {
    this.deleteTarget.set(null);
  }

  protected onCreate(payload: AccountCreateRequest): void {
    this.submitting.set(true);
    this.accountService.create(payload).subscribe({
      next: () => {
        this.notifier.success(`Account ${payload.accountNumber} created`);
        this.submitting.set(false);
        this.formOpen.set(false);
        this.load();
      },
      error: () => this.submitting.set(false),
    });
  }

  protected onUpdate(event: { id: number; payload: AccountUpdateRequest }): void {
    this.submitting.set(true);
    this.accountService.update(event.id, event.payload).subscribe({
      next: () => {
        this.notifier.success('Account updated');
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
