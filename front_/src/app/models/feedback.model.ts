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
  date: Date | string; // Accepter string pour la désérialisation  feeling: TypeFeeling;
  feeling: TypeFeeling;
  eventTitle?: string; // Ajouté pour correspondre au backend
}
export interface FeedbackStats {
  averageFeelingByEvent: { [key: string]: number };
}
