import React, { useState } from 'react';
import { sentenceService } from '../services/sentenceService';
import { SentenceRequest } from '../types/Sentence';
import { useAuth } from '../context/AuthContext';

interface SentenceFormProps {
  onSentenceAdded?: () => void;
}

const styles = {
  container: {
    border: '1px solid #dee2e6',
    borderRadius: '6px',
    marginBottom: '24px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
  },
  header: {
    borderBottom: '1px solid #dee2e6',
    padding: '16px 20px',
    backgroundColor: '#f8f9fa',
    borderTopLeftRadius: '6px',
    borderTopRightRadius: '6px',
  },
  title: {
    margin: 0,
    fontSize: '18px',
    fontWeight: 600 as const,
    color: '#212529',
  },
  body: {
    padding: '20px',
  },
  success: {
    backgroundColor: '#d4edda',
    color: '#155724',
    padding: '12px 16px',
    borderRadius: '4px',
    marginBottom: '16px',
    border: '1px solid #c3e6cb',
  },
  error: {
    backgroundColor: '#f8d7da',
    color: '#721c24',
    padding: '12px 16px',
    borderRadius: '4px',
    marginBottom: '16px',
    border: '1px solid #f5c6cb',
  },
  formGroup: {
    marginBottom: '20px',
  },
  label: {
    display: 'block',
    marginBottom: '8px',
    fontWeight: 500 as const,
    color: '#212529',
  },
  textArea: {
    display: 'block',
    width: '100%',
    padding: '10px 12px',
    fontSize: '16px',
    lineHeight: 1.5,
    color: '#495057',
    backgroundColor: '#fff',
    backgroundClip: 'padding-box',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    transition: 'border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out',
    fontFamily: 'inherit',
  },
  input: {
    display: 'block',
    width: '100%',
    padding: '10px 12px',
    fontSize: '16px',
    lineHeight: 1.5,
    color: '#495057',
    backgroundColor: '#fff',
    backgroundClip: 'padding-box',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    transition: 'border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out',
  },
  focusedInput: {
    borderColor: '#80bdff',
    outline: 0,
    boxShadow: '0 0 0 0.2rem rgba(0, 123, 255, 0.25)',
  },
  hint: {
    display: 'block',
    marginTop: '5px',
    fontSize: '14px',
    color: '#6c757d',
  },
  button: {
    color: '#fff',
    backgroundColor: '#007bff',
    borderColor: '#007bff',
    padding: '10px 16px',
    fontSize: '16px',
    lineHeight: 1.5,
    borderRadius: '4px',
    cursor: 'pointer',
    border: '1px solid transparent',
    userSelect: 'none' as const,
    transition: 'color 0.15s ease-in-out, background-color 0.15s ease-in-out, border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out',
  },
  buttonHover: {
    backgroundColor: '#0069d9',
    borderColor: '#0062cc',
  },
  buttonDisabled: {
    backgroundColor: '#007bff',
    borderColor: '#007bff',
    opacity: 0.65,
    cursor: 'not-allowed' as const,
  },
  loginMessage: {
    textAlign: 'center' as const,
    padding: '20px',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px',
    margin: '20px',
  },
  loginButton: {
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '4px',
    fontSize: '16px',
    cursor: 'pointer',
    marginTop: '15px',
    transition: 'background-color 0.15s ease-in-out',
  },
};

const SentenceForm: React.FC<SentenceFormProps> = ({ onSentenceAdded }) => {
  const [text, setText] = useState('');
  const [source, setSource] = useState<string | null>('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isTextAreaFocused, setIsTextAreaFocused] = useState(false);
  const [isInputFocused, setIsInputFocused] = useState(false);
  const [isButtonHovered, setIsButtonHovered] = useState(false);
  const { isAuthenticated, login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!text.trim()) {
      setError('テキストを入力してください');
      return;
    }
    
    setIsSubmitting(true);
    setError(null);
    setSuccess(null);
    
    try {
      const sentenceRequest: SentenceRequest = {
        text: text.trim(),
        source: source || null
      };
      
      await sentenceService.registerSentence(sentenceRequest);
      
      // フォームをリセット
      setText('');
      setSource('');
      setSuccess('センテンスを登録しました。翻訳や要素の抽出、難易度の判定はバックグラウンドで自動的に行われます。リストで処理状況を確認できます。');
      
      // 親コンポーネントに通知
      if (onSentenceAdded) {
        onSentenceAdded();
      }
      
    } catch (err) {
      setError(err instanceof Error ? err.message : '登録中にエラーが発生しました');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <div style={styles.container}>
        <div style={styles.header}>
          <h5 style={styles.title}>新しいセンテンスを登録</h5>
        </div>
        <div style={styles.loginMessage}>
          <p>センテンスを登録するにはログインしてください。</p>
          <button 
            style={styles.loginButton} 
            onClick={login}
            onMouseEnter={() => {
              const button = document.querySelector('[data-login-button]') as HTMLButtonElement;
              if (button) {
                button.style.backgroundColor = styles.buttonHover.backgroundColor;
                button.style.borderColor = styles.buttonHover.borderColor;
              }
            }}
            onMouseLeave={() => {
              const button = document.querySelector('[data-login-button]') as HTMLButtonElement;
              if (button) {
                button.style.backgroundColor = styles.button.backgroundColor;
                button.style.borderColor = styles.button.borderColor;
              }
            }}
            data-login-button
          >
            ログイン
          </button>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h5 style={styles.title}>新しいセンテンスを登録</h5>
      </div>
      <div style={styles.body}>
        {error && (
          <div style={styles.error}>{error}</div>
        )}
        
        {success && (
          <div style={styles.success}>{success}</div>
        )}
        
        <form onSubmit={handleSubmit}>
          <div style={styles.formGroup}>
            <label htmlFor="text" style={styles.label}>テキスト</label>
            <textarea
              id="text"
              style={{
                ...styles.textArea,
                ...(isTextAreaFocused ? styles.focusedInput : {})
              }}
              value={text}
              onChange={(e) => setText(e.target.value)}
              onFocus={() => setIsTextAreaFocused(true)}
              onBlur={() => setIsTextAreaFocused(false)}
              placeholder="登録したい英文を入力してください"
              required
              rows={4}
            />
            <small style={styles.hint}>※翻訳や単語・イディオム・文法の抽出、難易度の判定は自動的に行われます</small>
          </div>
          
          <div style={styles.formGroup}>
            <label htmlFor="source" style={styles.label}>出典（任意）</label>
            <input
              type="text"
              id="source"
              style={{
                ...styles.input,
                ...(isInputFocused ? styles.focusedInput : {})
              }}
              value={source || ''}
              onChange={(e) => setSource(e.target.value)}
              onFocus={() => setIsInputFocused(true)}
              onBlur={() => setIsInputFocused(false)}
              placeholder="書籍名、ウェブサイト、映画など"
            />
          </div>
          
          <button
            type="submit"
            style={{
              ...styles.button,
              ...(isSubmitting ? styles.buttonDisabled : {}),
              ...(isButtonHovered && !isSubmitting ? styles.buttonHover : {})
            }}
            disabled={isSubmitting}
            onMouseEnter={() => setIsButtonHovered(true)}
            onMouseLeave={() => setIsButtonHovered(false)}
          >
            {isSubmitting ? '登録中...' : '登録する'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default SentenceForm; 