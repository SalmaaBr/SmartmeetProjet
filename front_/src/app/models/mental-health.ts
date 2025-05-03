// src/app/models/mental-health.ts
export interface MentalHealth {
  idMentalHealth?: number; // Optionnel car généré par le backend
  responseMoment: 'Avant' | 'Pendant' | 'Apres'; // Correspond à ResponseMoment
  stressLevel: number;
  emotionalState: 'HAPPY' | 'SAD' | 'STRESSED' | 'RELAXED' | 'NEUTRAL'; // Correspond à TypeEmotionalState
  supportNeed: 'Oui' | 'Non'; // Correspond à SupportNeed
  submissionDate?: string; // ISO string (ex. "2025-03-29T12:00:00")
  user?: { userID: number, username: string }; // Relation ManyToOne simplifiée
}
