import { Sentence } from '../types/Sentence';

const API_URL = '/api/user/sentences';

export const userSentenceService = {
  // ユーザーのセンテンスリストを取得
  getUserSentences: async (): Promise<Sentence[]> => {
    const response = await fetch(API_URL, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch user sentences');
    }
    
    return response.json();
  },
  
  // センテンスをユーザーのリストに追加
  addSentenceToUserList: async (sentenceId: number): Promise<boolean> => {
    const response = await fetch(`${API_URL}/${sentenceId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include'
    });
    
    return response.ok;
  },
  
  // センテンスをユーザーのリストから削除
  removeSentenceFromUserList: async (sentenceId: number): Promise<boolean> => {
    const response = await fetch(`${API_URL}/${sentenceId}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    return response.ok;
  },
  
  // センテンスがユーザーのリストに存在するか確認
  checkSentenceInUserList: async (sentenceId: number): Promise<boolean> => {
    try {
      const response = await fetch(`${API_URL}/has/${sentenceId}`, {
        credentials: 'include'
      });
      
      if (!response.ok) {
        return false;
      }
      
      const result = await response.json();
      return result.hasSentence;
    } catch (error) {
      return false;
    }
  }
}; 