import { Component, computed, effect, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import {
  ACCOUNT_TYPE_OPTIONS,
  AccountCreateRequest,
  AccountResponse,
  AccountUpdateRequest,
  ClientResponse,
  STATUS_OPTIONS,
} from '../../../core/models';

@Component({
  selector: 'app-account-form',
  imports: [ReactiveFormsModule],
  templateUrl: './account-form.html',
  styleUrl: './account-form.css',
})
export class AccountForm {
  private readonly fb = inject(FormBuilder);

  readonly account = input<AccountResponse | null>(null);
  readonly clients = input.required<ClientResponse[]>();
  readonly submitting = input<boolean>(false);

  readonly submitCreate = output<AccountCreateRequest>();
  readonly submitUpdate = output<{ id: number; payload: AccountUpdateRequest }>();
  readonly cancel = output<void>();

  protected readonly accountTypeOptions = ACCOUNT_TYPE_OPTIONS;
  protected readonly statusOptions = STATUS_OPTIONS;

  protected readonly form = this.fb.nonNullable.group({
    accountNumber: ['', [Validators.required]],
    accountType: ['SAVINGS', [Validators.required]],
    initialBalance: [0, [Validators.required, Validators.min(0)]],
    status: ['ACTIVE'],
    clientId: ['', [Validators.required]],
  });

  protected readonly isEdit = computed(() => this.account() !== null);

  constructor() {
    effect(() => {
      const a = this.account();
      if (a) {
        this.form.patchValue({
          accountNumber: a.accountNumber,
          accountType: a.accountType,
          initialBalance: a.initialBalance,
          status: a.status,
          clientId: a.clientId,
        });
        this.form.controls.initialBalance.disable();
        this.form.controls.clientId.disable();
      } else {
        this.form.controls.initialBalance.enable();
        this.form.controls.clientId.enable();
      }
    });
  }

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    if (this.isEdit()) {
      const payload: AccountUpdateRequest = {
        accountNumber: value.accountNumber,
        accountType: value.accountType as AccountUpdateRequest['accountType'],
        status: value.status as AccountUpdateRequest['status'],
      };
      this.submitUpdate.emit({ id: this.account()!.id, payload });
    } else {
      const payload: AccountCreateRequest = {
        accountNumber: value.accountNumber,
        accountType: value.accountType as AccountCreateRequest['accountType'],
        initialBalance: value.initialBalance,
        status: value.status as AccountCreateRequest['status'],
        clientId: value.clientId,
      };
      this.submitCreate.emit(payload);
    }
  }

  protected hasError(controlName: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.touched || control.dirty);
  }
}
