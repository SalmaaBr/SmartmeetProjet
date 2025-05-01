import { Resource } from "./resource.model";
import { User } from "./user.model";

export interface Reservation {
  reservationId?: number; // Optional for creation
  resourceId: number;
  userId?: number;
  user?: User;
  startTime: string;
  endTime: string;
  resource?: Resource;
}
