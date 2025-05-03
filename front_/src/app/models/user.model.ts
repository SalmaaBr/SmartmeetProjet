export interface User {
  userID?: number;
  username: string;
  email: string;
  password?: string;
  phoneNumber?: string;
  address?: string;
  enabled?: boolean;

  userRole: TypeUserRole[];
  firstName?: string;
  lastName?: string;
}

export type TypeUserRole = 'USER' | 'ADMIN' | 'PARTICIPANT' | 'SPEAKER' | 'TRAINER' | 'SPONSOR' | 'COMPANY';
