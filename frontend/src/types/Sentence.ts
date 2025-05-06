import { EnglishWord } from './EnglishWord';

export enum DifficultyLevel {
  BEGINNER = 'BEGINNER',
  INTERMEDIATE = 'INTERMEDIATE',
  ADVANCED = 'ADVANCED',
  NATIVE = 'NATIVE'
}

export enum TranslationStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  ERROR = 'ERROR'
}

export interface Idiom {
  id: number;
  phrase: string;
  meaning: string | null;
  note: string | null;
}

export interface Grammar {
  id: number;
  pattern: string;
  explanation: string | null;
  note: string | null;
}

export interface Sentence {
  id: number;
  text: string;
  translation: string | null;
  note: string | null;
  source: string | null;
  difficulty: DifficultyLevel;
  translationStatus: TranslationStatus;
  createdAt: string;
  updatedAt: string;
  words: EnglishWord[];
  idioms: Idiom[];
  grammars: Grammar[];
}

export interface SentenceRequest {
  text: string;
  translation?: string | null;
  note?: string | null;
  source?: string | null;
  difficulty?: DifficultyLevel;
} 