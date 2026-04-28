import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { forkJoin } from 'rxjs';

import { AccountService } from '../../../core/services/account.service';
import { MovementService } from '../../../core/services/movement.service';
import { NotificationService } from '../../../core/services/notification.service';
import {
  AccountResponse,
  MovementRequest,
  MovementResponse,
} from '../../../core/models';
import { ConfirmDialog } from '../../../shared/components/confirm-dialog/confirm-dialog';
import { Modal } from '../../../shared/components/modal/modal';
import { PageHeader } from '../../../shared/components/page-header/page-header';
import { MovementForm } from '../movement-form/movement-form';

@Component({
  selector: 'app-movement-list',
  imports: [DatePipe, DecimalPipe, PageHeader, Modal, ConfirmDialog, MovementForm],
  templateUrl: './movement-list.html',
  styleUrl: './movement-list.css',
})
export class MovementList {
  private readonly movementService = inject(MovementService);
  private readonly accountService = inject(AccountService);
  private readonly notifier = inject(NotificationService);

  protected readonly movements = signal<MovementResponse[]>([]);
  protected readonly accounts = signal<AccountResponse[]>([]);
  protected readonly loading = signal(false);
  protected readonly submitting = signal(false);
  protected readonly searchQuery = signal('');
  protected readonly formOpen = signal(false);
  protected readonly deleteTarget = signal<MovementResponse | null>(null);

  protected readonly filteredMovements = computed(() => {
    const q = this.searchQuery().trim().toLowerCase();
    const list = this.movements();
    if (!q) return list;
    return list.filter((m) =>
      [m.accountNumber, m.movementType, m.value.toString(), m.balance.toString()]
        .some((v) => v.toLowerCase().includes(q))
    );
  });

  constructor() {
    this.load();
  }

  protected load(): void {
    this.loading.set(true);
    forkJoin({
      movements: this.movementService.findAll(),
      accounts: this.accountService.findAll(),
    }).subscribe({
      next: ({ movements, accounts }) => {
        this.movements.set([...movements].sort((a, b) => b.date.localeCompare(a.date)));
        this.accounts.set(accounts);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  protected onNew(): void {
    if (this.accounts().length === 0) {
      this.notifier.error('Create an account first before registering a movement');
      return;
    }
    this.formOpen.set(true);
  }

  protected onDelete(movement: MovementResponse): void {
    this.deleteTarget.set(movement);
  }

  protected onConfirmDelete(): void {
    const movement = this.deleteTarget();
    if (!movement) return;
    this.deleteTarget.set(null);
    this.movementService.delete(movement.id).subscribe({
      next: () => {
        this.notifier.success('Movement deleted');
        this.load();
      },
    });
  }

  protected onCancelDelete(): void {
    this.deleteTarget.set(null);
  }

  protected onSubmit(payload: MovementRequest): void {
    this.submitting.set(true);
    this.movementService.create(payload).subscribe({
      next: () => {
        this.notifier.success('Movement registered');
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
