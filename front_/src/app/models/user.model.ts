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
  interests?: string[];
  meetings?: Meeting[]; // Ajout de la propriété meetings
  
}

export interface Meeting {
  id: number;
  meetingName: string;
  meetingLink: string;
  startTime: string;
  endTime: string;
}

export type TypeUserRole = 'USER' | 'ADMIN' | 'PARTICIPANT' | 'SPEAKER' | 'TRAINER' | 'SPONSOR' | 'COMPANY';
