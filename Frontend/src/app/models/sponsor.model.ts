export interface Sponsor {
    id?: number;
    name: string;
    logo: string;
    contributionAmount: number;
    partnershipType?: 'GOLD' | 'SILVER' | 'BRONZE';
    events?: number[];
  }