import { Gender, Status } from './enums';

export interface ClientCreateRequest {
  clientId: string;
  name: string;
  gender: Gender;
  age: number;
  identification: string;
  address?: string | null;
  phone?: string | null;
  password: string;
  status?: Status | null;
}

export interface ClientUpdateRequest {
  name?: string | null;
  gender?: Gender | null;
  age?: number | null;
  identification?: string | null;
  address?: string | null;
  phone?: string | null;
  password?: string | null;
  status?: Status | null;
}

export interface ClientResponse {
  clientId: string;
  name: string;
  gender: Gender;
  age: number;
  identification: string;
  address: string | null;
  phone: string | null;
  status: Status;
  createdAt: string;
  updatedAt: string;
}
