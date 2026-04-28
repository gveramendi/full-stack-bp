import { AccountType, MovementType } from './enums';

export interface MovementEntry {
  date: string;
  movementType: MovementType;
  value: number;
  balance: number;
}

export interface AccountStatement {
  accountId: number;
  accountNumber: string;
  accountType: AccountType;
  initialBalance: number;
  currentBalance: number;
  totalCredits: number;
  totalDebits: number;
  movements: MovementEntry[];
}

export interface ReportResponse {
  clientId: string;
  clientName: string;
  from: string;
  to: string;
  accounts: AccountStatement[];
  totalCredits: number;
  totalDebits: number;
  pdfBase64: string | null;
}
