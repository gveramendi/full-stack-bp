import { Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import {
  AccountResponse,
  MOVEMENT_TYPE_OPTIONS,
  MovementRequest,
} from '../../../core/models';

@Component({
  selector: 'app-movement-form',
  imports: [ReactiveFormsModule],
  templateUrl: './movement-form.html',
  styleUrl: './movement-form.css',
})
export class MovementForm {
  private readonly fb = inject(FormBuilder);

  readonly accounts = input.required<AccountResponse[]>();
  readonly submitting = input<boolean>(false);

  readonly submit = output<MovementRequest>();
  readonly cancel = output<void>();

  protected readonly movementTypeOptions = MOVEMENT_TYPE_OPTIONS;

  protected readonly form = this.fb.nonNullable.group({
    accountId: [0, [Validators.required, Validators.min(1)]],
    movementType: ['DEPOSIT', [Validators.required]],
    amount: [0, [Validators.required, Validators.min(0.01)]],
  });

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    this.submit.emit({
      accountId: Number(value.accountId),
      movementType: value.movementType as MovementRequest['movementType'],
      amount: value.amount,
    });
  }

  protected hasError(controlName: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.touched || control.dirty);
  }
}
