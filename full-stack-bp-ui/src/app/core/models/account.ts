import { AccountType, Status } from './enums';

export interface AccountCreateRequest {
  accountNumber: string;
  accountType: AccountType;
  initialBalance: number;
  status?: Status | null;
  clientId: string;
}

export interface AccountUpdateRequest {
  accountNumber?: string | null;
  accountType?: AccountType | null;
  status?: Status | null;
}

export interface AccountResponse {
  id: number;
  accountNumber: string;
  accountType: AccountType;
  initialBalance: number;
  currentBalance: number;
  status: Status;
  clientId: string;
  clientName: string;
  createdAt: string;
  updatedAt: string;
}
