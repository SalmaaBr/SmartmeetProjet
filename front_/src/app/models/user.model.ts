export interface User {
  userID?: number;
  username: string;
  email: string;
  password?: string;
  phoneNumber?: string;
  address?: string;
  enabled?: boolean;
  userRole: TypeUserRole[];
  interests?: string[];
}

export type TypeUserRole = 'USER' | 'ADMIN' | 'PARTICIPANT' | 'SPEAKER' | 'TRAINER' | 'SPONSOR' | 'COMPANY';
