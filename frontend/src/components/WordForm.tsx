import React, { useState } from 'react';
import { wordService } from '../services/wordService';
import { userWordService } from '../services/userWordService';
import { EnglishWord } from '../types/EnglishWord';
import { useAuth } from '../context/AuthContext';

interface WordFormProps {
  onWordRegistered?: (word: EnglishWord) => void;
}

const styles = {
  form: {
    width: '100%',
  },
  alert: {
    padding: '12px 16px',
    borderRadius: '4px',
    marginBottom: '16px',
  },
  alertDanger: {
    backgroundColor: '#f8d7da',
    color: '#721c24',
    border: '1px solid #f5c6cb',
  },
  alertSuccess: {
    backgroundColor: '#d4edda',
    color: '#155724',
    border: '1px solid #c3e6cb',
  },
  formGroup: {
    marginBottom: '20px',
  },
  label: {
    display: 'block',
    marginBottom: '8px',
    fontWeight: 'bold' as const,
    color: '#495057',
  },
  input: {
    width: '100%',
    padding: '12px 16px',
    fontSize: '16px',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    transition: 'border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out',
    outline: 'none',
  },
  inputFocus: {
    borderColor: '#80bdff',
    boxShadow: '0 0 0 0.2rem rgba(0, 123, 255, 0.25)',
  },
  smallText: {
    fontSize: '14px',
    color: '#6c757d',
    marginTop: '6px',
    display: 'block',
  },
  button: {
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    padding: '12px 24px',
    borderRadius: '4px',
    fontSize: '16px',
    cursor: 'pointer',
    transition: 'background-color 0.15s ease-in-out',
  },
  buttonHover: {
    backgroundColor: '#0069d9',
  },
  buttonDisabled: {
    backgroundColor: '#6c757d',
    cursor: 'not-allowed',
  },
};

const WordForm: React.FC<WordFormProps> = ({ onWordRegistered }) => {
  const [word, setWord] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [inputFocused, setInputFocused] = useState(false);
  const { isAuthenticated } = useAuth();

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
      
      // ログイン中であれば自動的にマイリストに追加
      if (isAuthenticated && registeredWord.id) {
        try {
          await userWordService.addWordToUserList(registeredWord.id);
          setSuccess('単語を登録し、マイリストに追加しました。意味や例文は自動的に取得されます。');
        } catch (addError) {
          console.error('マイリストへの追加に失敗しました', addError);
          setSuccess('単語を登録しました。意味や例文は自動的に取得されます。（マイリストへの追加に失敗しました）');
        }
      } else {
        setSuccess('単語を登録しました。意味や例文は自動的に取得されます。');
      }
      
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
    <form onSubmit={handleSubmit} style={styles.form}>
      {error && (
        <div style={{ ...styles.alert, ...styles.alertDanger }}>
          {error}
        </div>
      )}
      
      {success && (
        <div style={{ ...styles.alert, ...styles.alertSuccess }}>
          {success}
        </div>
      )}
      
      <div style={styles.formGroup}>
        <label htmlFor="word" style={styles.label}>
          英単語
        </label>
        <input
          type="text"
          id="word"
          value={word}
          onChange={(e) => setWord(e.target.value)}
          disabled={loading}
          placeholder="登録したい英単語を入力"
          required
          style={{
            ...styles.input,
            ...(inputFocused ? styles.inputFocus : {}),
            ...(loading ? { backgroundColor: '#e9ecef', cursor: 'not-allowed' } : {})
          }}
          onFocus={() => setInputFocused(true)}
          onBlur={() => setInputFocused(false)}
        />
        <small style={styles.smallText}>
          ※意味や例文は自動的に取得されます{isAuthenticated && '（登録するとマイリストに自動追加されます）'}
        </small>
      </div>
      
      <button 
        type="submit" 
        disabled={loading}
        style={{
          ...styles.button,
          ...(loading ? styles.buttonDisabled : {})
        }}
        onMouseOver={(e) => {
          if (!loading) {
            e.currentTarget.style.backgroundColor = styles.buttonHover.backgroundColor;
          }
        }}
        onMouseOut={(e) => {
          if (!loading) {
            e.currentTarget.style.backgroundColor = styles.button.backgroundColor;
          }
        }}
      >
        {loading ? '登録中...' : '登録する'}
      </button>
    </form>
  );
};

export default WordForm; 