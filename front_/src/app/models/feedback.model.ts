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
  date: Date;
  feeling: TypeFeeling;
  eventTitle?: string; // Ajouté pour correspondre au backend
}
