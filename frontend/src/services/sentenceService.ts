import { Sentence, SentenceRequest } from '../types/Sentence';

const API_URL = '/api/sentences';

export const sentenceService = {
  // センテンスを登録する
  registerSentence: async (sentenceRequest: SentenceRequest): Promise<Sentence> => {
    const response = await fetch(API_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(sentenceRequest),
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('センテンスの登録に失敗しました');
    }
    
    return response.json();
  },
  
  // 全てのセンテンスを取得する
  getAllSentences: async (): Promise<Sentence[]> => {
    const response = await fetch(API_URL, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('センテンスの取得に失敗しました');
    }
    
    return response.json();
  },
  
  // IDによるセンテンスの取得
  getSentenceById: async (id: number): Promise<Sentence> => {
    const response = await fetch(`${API_URL}/${id}`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('センテンスの取得に失敗しました');
    }
    
    return response.json();
  },
  
  // センテンスの更新
  updateSentence: async (id: number, sentenceRequest: SentenceRequest): Promise<Sentence> => {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(sentenceRequest),
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('センテンスの更新に失敗しました');
    }
    
    return response.json();
  },
  
  // センテンスの削除
  deleteSentence: async (id: number): Promise<boolean> => {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    return response.ok;
  },
  
  // テキスト検索によるセンテンスの取得
  searchSentences: async (query: string): Promise<Sentence[]> => {
    const response = await fetch(`${API_URL}/search?query=${encodeURIComponent(query)}`);
    
    if (!response.ok) {
      throw new Error('センテンスの検索に失敗しました');
    }
    
    return response.json();
  }
}; 