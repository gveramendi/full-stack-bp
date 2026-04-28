import { MovementType } from './enums';

export interface MovementRequest {
  accountId: number;
  movementType: MovementType;
  amount: number;
}

export interface MovementResponse {
  id: number;
  date: string;
  movementType: MovementType;
  value: number;
  balance: number;
  accountId: number;
  accountNumber: string;
}
