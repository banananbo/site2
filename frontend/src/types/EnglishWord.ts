export interface WordExample {
  id: number;
  example: string;
  translation: string | null;
  note: string | null;
  source: string | null;
}

export interface EnglishWord {
  id: number;
  word: string;
  meaning: string | null;
  examples: WordExample[];
  translationStatus: string;
} 