import React, { useState } from 'react';
import { wordService } from '../services/wordService';
import { EnglishWord } from '../types/EnglishWord';

interface WordFormProps {
  onWordRegistered?: (word: EnglishWord) => void;
}

const styles = {
  wordForm: {
    margin: '2rem 0',
    padding: '1.5rem',
    borderRadius: '8px',
    backgroundColor: '#f8f9fa',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)'
  },
  heading: {
    marginTop: 0,
    marginBottom: '1.5rem',
    color: '#333'
  },
  formGroup: {
    marginBottom: '1rem'
  },
  label: {
    display: 'block',
    marginBottom: '0.5rem',
    fontWeight: 'bold' as const
  },
  formControl: {
    width: '100%',
    padding: '0.75rem',
    fontSize: '1rem',
    border: '1px solid #ced4da',
    borderRadius: '4px'
  },
  errorMessage: {
    color: '#dc3545',
    margin: '0.5rem 0'
  },
  submitButton: {
    padding: '0.75rem 1.5rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    fontSize: '1rem',
    cursor: 'pointer',
    transition: 'background-color 0.2s'
  },
  submitButtonHover: {
    backgroundColor: '#0069d9'
  },
  submitButtonDisabled: {
    backgroundColor: '#6c757d',
    cursor: 'not-allowed'
  }
};

const WordForm: React.FC<WordFormProps> = ({ onWordRegistered }) => {
  const [word, setWord] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!word.trim()) {
      setError('単語を入力してください');
      return;
    }
    
    setLoading(true);
    setError(null);
    
    try {
      const registeredWord = await wordService.registerWord(word);
      setWord('');
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
    <div style={styles.wordForm}>
      <h2 style={styles.heading}>英単語を登録</h2>
      <form onSubmit={handleSubmit}>
        <div style={styles.formGroup}>
          <label htmlFor="word" style={styles.label}>英単語:</label>
          <input
            type="text"
            id="word"
            value={word}
            onChange={(e) => setWord(e.target.value)}
            disabled={loading}
            placeholder="登録したい英単語を入力"
            style={styles.formControl}
          />
        </div>
        
        {error && <div style={styles.errorMessage}>{error}</div>}
        
        <button 
          type="submit" 
          disabled={loading} 
          style={{
            ...styles.submitButton,
            ...(loading ? styles.submitButtonDisabled : {})
          }}
        >
          {loading ? '登録中...' : '登録'}
        </button>
      </form>
    </div>
  );
};

export default WordForm; 