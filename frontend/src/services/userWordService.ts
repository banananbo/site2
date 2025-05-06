import { EnglishWord } from '../types/EnglishWord';

const API_URL = '/api/user/words';

export const userWordService = {
  // ユーザーの単語リストを取得
  getUserWords: async (): Promise<EnglishWord[]> => {
    const response = await fetch(API_URL, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('Failed to fetch user words');
    }
    
    return response.json();
  },
  
  // 単語をユーザーのリストに追加
  addWordToUserList: async (wordId: number): Promise<boolean> => {
    const response = await fetch(`${API_URL}/${wordId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include'
    });
    
    return response.ok;
  },
  
  // 単語をユーザーのリストから削除
  removeWordFromUserList: async (wordId: number): Promise<boolean> => {
    const response = await fetch(`${API_URL}/${wordId}`, {
      method: 'DELETE',
      credentials: 'include'
    });
    
    return response.ok;
  },
  
  // 単語がユーザーのリストに存在するか確認
  checkWordInUserList: async (wordId: number): Promise<boolean> => {
    try {
      const response = await fetch(`${API_URL}/has/${wordId}`, {
        credentials: 'include'
      });
      
      if (!response.ok) {
        return false;
      }
      
      const result = await response.json();
      return result.hasWord;
    } catch (error) {
      return false;
    }
  }
}; 