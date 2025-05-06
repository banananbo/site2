import { EnglishWord, WordExample } from '../types/EnglishWord';

const API_URL = '/api/words';

export const wordService = {
  registerWord: async (word: string): Promise<EnglishWord> => {
    const response = await fetch(API_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ word }),
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('Failed to register word');
    }
    
    return response.json();
  },
  
  getAllWords: async (): Promise<EnglishWord[]> => {
    const response = await fetch(API_URL, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch words');
    }
    
    return response.json();
  },
  
  getWordById: async (id: number): Promise<EnglishWord> => {
    const response = await fetch(`${API_URL}/${id}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch word');
    }
    
    return response.json();
  },
  
  findWordByText: async (word: string): Promise<EnglishWord> => {
    const response = await fetch(`${API_URL}/search?word=${encodeURIComponent(word)}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('Word not found');
    }
    
    return response.json();
  },
  
  deleteWord: async (id: number): Promise<boolean> => {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    return response.ok;
  },
  
  // 例文関連のAPI
  addExample: async (
    wordId: number, 
    example: string, 
    translation: string | null = null,
    note: string | null = null, 
    source: string | null = null
  ): Promise<WordExample> => {
    const response = await fetch(`${API_URL}/${wordId}/examples`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ example, translation, note, source }),
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('Failed to add example');
    }
    
    return response.json();
  },
  
  getExamples: async (wordId: number): Promise<WordExample[]> => {
    const response = await fetch(`${API_URL}/${wordId}/examples`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch examples');
    }
    
    return response.json();
  },
  
  updateExample: async (
    wordId: number,
    exampleId: number,
    example: string,
    translation: string | null = null,
    note: string | null = null,
    source: string | null = null
  ): Promise<WordExample> => {
    const response = await fetch(`${API_URL}/${wordId}/examples/${exampleId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ example, translation, note, source }),
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('Failed to update example');
    }
    
    return response.json();
  },
  
  deleteExample: async (wordId: number, exampleId: number): Promise<boolean> => {
    const response = await fetch(`${API_URL}/${wordId}/examples/${exampleId}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    return response.ok;
  }
}; 