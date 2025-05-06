import React, { useState } from 'react';
import { wordService } from '../services/wordService';
import { EnglishWord } from '../types/EnglishWord';

interface WordFormProps {
  onWordRegistered?: (word: EnglishWord) => void;
}

const WordForm: React.FC<WordFormProps> = ({ onWordRegistered }) => {
  const [word, setWord] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!word.trim()) {
      setError('単語を入力してください');
      return;
    }
    
    setLoading(true);
    setError(null);
    setSuccess(null);
    
    try {
      const registeredWord = await wordService.registerWord(word);
      setWord('');
      setSuccess('単語を登録しました。意味や例文は自動的に取得されます。');
      
      if (onWordRegistered) {
        onWordRegistered(registeredWord);
      }
    } catch (err) {
      setError('単語の登録に失敗しました');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && (
        <div className="alert alert-danger">{error}</div>
      )}
      
      {success && (
        <div className="alert alert-success">{success}</div>
      )}
      
      <div className="mb-3">
        <label htmlFor="word" className="form-label">英単語</label>
        <input
          type="text"
          id="word"
          className="form-control"
          value={word}
          onChange={(e) => setWord(e.target.value)}
          disabled={loading}
          placeholder="登録したい英単語を入力"
          required
        />
        <small className="text-muted">※意味や例文は自動的に取得されます</small>
      </div>
      
      <button 
        type="submit" 
        className="btn btn-primary"
        disabled={loading}
      >
        {loading ? '登録中...' : '登録する'}
      </button>
    </form>
  );
};

export default WordForm; 