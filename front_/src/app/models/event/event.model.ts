export interface Event {
    id: number;
    typeevent: string;
    typetheme: string;
    title: string;
    description: string;
    location: string;
    typeweather: string;
    startTime: string;
    endTime: string;
    maxParticipants: number;
    filePath?: string;  // Ajout de la propriété filePath
    imageUrl?: string;  // Ajout de la propriété imageUrl
  }
  