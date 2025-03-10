import { Resource } from "./resource.model";

export interface Reservation {
  reservationId?: number; // Optionnel pour l'ajout
  resourceId: number;
  user: string;
  startTime: string;
  endTime: string;
  resources?:Resource[]
}
