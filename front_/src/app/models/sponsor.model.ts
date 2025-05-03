import { User } from './user.model';

export interface Sponsor {
    idSponsor: number;
    nom: string;
    description: string;
    image: string;
    niveau: 'BRONZE' | 'ARGENT' | 'OR' | 'PLATINE';
    statut: boolean;
    siteWeb: string;
    user?: User;
    responsibleUser?: User;
    userId?: number;
    responsibleUserId?: number;
} 