export interface Announcement {
    id?: number;
    title: string;
    description: string;
    datePosted?: string;
    status?: 'UNRESOLVED' | 'RESOLVED';
    eventId?: number;
  }