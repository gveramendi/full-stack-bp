import { Component, computed, effect, inject, input, output } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import {
  ClientCreateRequest,
  ClientResponse,
  ClientUpdateRequest,
  GENDER_OPTIONS,
  STATUS_OPTIONS,
} from '../../../core/models';

@Component({
  selector: 'app-client-form',
  imports: [ReactiveFormsModule],
  templateUrl: './client-form.html',
  styleUrl: './client-form.css',
})
export class ClientForm {
  private readonly fb = inject(FormBuilder);

  readonly client = input<ClientResponse | null>(null);
  readonly submitting = input<boolean>(false);

  readonly submitCreate = output<ClientCreateRequest>();
  readonly submitUpdate = output<{ clientId: string; payload: ClientUpdateRequest }>();
  readonly cancel = output<void>();

  protected readonly genderOptions = GENDER_OPTIONS;
  protected readonly statusOptions = STATUS_OPTIONS;

  protected readonly form = this.fb.nonNullable.group({
    clientId: ['', [Validators.required]],
    name: ['', [Validators.required]],
    gender: ['MALE', [Validators.required]],
    age: [18, [Validators.required, Validators.min(0), Validators.max(150)]],
    identification: ['', [Validators.required]],
    address: [''],
    phone: [''],
    password: ['', [Validators.required, Validators.minLength(4)]],
    status: ['ACTIVE'],
  });

  protected readonly isEdit = computed(() => this.client() !== null);

  constructor() {
    effect(() => {
      const c = this.client();
      if (c) {
        this.form.patchValue({
          clientId: c.clientId,
          name: c.name,
          gender: c.gender,
          age: c.age,
          identification: c.identification,
          address: c.address ?? '',
          phone: c.phone ?? '',
          password: '',
          status: c.status,
        });
        this.form.controls.clientId.disable();
        this.form.controls.password.clearValidators();
        this.form.controls.password.updateValueAndValidity();
      } else {
        this.form.controls.clientId.enable();
        this.form.controls.password.setValidators([Validators.required, Validators.minLength(4)]);
        this.form.controls.password.updateValueAndValidity();
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
      const payload: ClientUpdateRequest = {
        name: value.name,
        gender: value.gender as ClientUpdateRequest['gender'],
        age: value.age,
        identification: value.identification,
        address: value.address || null,
        phone: value.phone || null,
        password: value.password ? value.password : null,
        status: value.status as ClientUpdateRequest['status'],
      };
      this.submitUpdate.emit({ clientId: value.clientId, payload });
    } else {
      const payload: ClientCreateRequest = {
        clientId: value.clientId,
        name: value.name,
        gender: value.gender as ClientCreateRequest['gender'],
        age: value.age,
        identification: value.identification,
        address: value.address || null,
        phone: value.phone || null,
        password: value.password,
        status: value.status as ClientCreateRequest['status'],
      };
      this.submitCreate.emit(payload);
    }
  }

  protected hasError(controlName: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.touched || control.dirty);
  }
}
