export enum TypeFeeling {
  EXCELLENT = 'EXCELLENT',
  GOOD = 'GOOD',
  AVERAGE = 'AVERAGE',
  BAD = 'BAD',
  TERRIBLE = 'TERRIBLE'
}

export interface Feedback {
  idFeedback?: number;
  message: string;
  feeling: string;
  date: string;
  sentiment?: string;
  eventTitle?: string; // ← AJOUTER CETTE LIGNE
 event?: { idEvent: number }; // Add event field to store event information
  user?: { userID: number , username: String}; // Add user field to store user information
}

export interface FeedbackStats {
  totalFeedbacks?: number;
  eventTitle?: string;
  sentimentCounts?: { [key: string]: number };
  sentimentPercentages?: { [key: string]: number };
  averageFeelingByEvent?: { [key: string]: number }; // Kept for backward compatibility
}
