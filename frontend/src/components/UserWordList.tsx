import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { userWordService } from '../services/userWordService';
import { EnglishWord } from '../types/EnglishWord';

const styles = {
  container: {
    marginTop: '2rem'
  },
  heading: {
    marginBottom: '1rem',
    color: '#333'
  },
  loading: {
    color: '#6c757d',
    textAlign: 'center' as const,
    margin: '2rem 0'
  },
  errorContainer: {
    color: '#dc3545',
    margin: '1rem 0',
    padding: '1rem',
    backgroundColor: '#f8d7da',
    borderRadius: '4px'
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse' as const,
    marginTop: '1rem',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)'
  },
  th: {
    backgroundColor: '#f8f9fa',
    padding: '0.75rem',
    textAlign: 'left' as const,
    borderBottom: '2px solid #dee2e6'
  },
  td: {
    padding: '0.75rem',
    borderBottom: '1px solid #dee2e6',
    verticalAlign: 'top' as const
  },
  pending: {
    backgroundColor: '#fff3cd',
    color: '#856404',
    padding: '0.25rem 0.5rem',
    borderRadius: '4px',
    fontSize: '0.875rem'
  },
  completed: {
    backgroundColor: '#d4edda',
    color: '#155724',
    padding: '0.25rem 0.5rem',
    borderRadius: '4px',
    fontSize: '0.875rem'
  },
  errorStatus: {
    backgroundColor: '#f8d7da',
    color: '#721c24',
    padding: '0.25rem 0.5rem',
    borderRadius: '4px',
    fontSize: '0.875rem'
  },
  noWords: {
    textAlign: 'center' as const,
    margin: '2rem 0',
    color: '#6c757d'
  },
  removeButton: {
    backgroundColor: '#ff4d4d',
    color: 'white',
    border: 'none',
    borderRadius: '3px',
    padding: '4px 8px',
    cursor: 'pointer'
  },
  wordLink: {
    color: '#007bff',
    textDecoration: 'none',
    fontWeight: 'bold' as const,
    ':hover': {
      textDecoration: 'underline'
    }
  }
};

const navStyles = {
  wordListNav: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '1rem'
  },
  linkButton: {
    display: 'inline-block',
    backgroundColor: '#007bff',
    color: 'white',
    padding: '8px 16px',
    borderRadius: '4px',
    textDecoration: 'none',
    fontSize: '0.9rem'
  }
};

const getStatusStyle = (status: string) => {
  switch (status) {
    case 'PENDING':
      return styles.pending;
    case 'COMPLETED':
      return styles.completed;
    case 'ERROR':
      return styles.errorStatus;
    default:
      return {};
  }
};

const UserWordList: React.FC = () => {
  const [words, setWords] = useState<EnglishWord[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [removeLoading, setRemoveLoading] = useState<number | null>(null);
  
  const fetchUserWords = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const fetchedWords = await userWordService.getUserWords();
      setWords(fetchedWords);
    } catch (err) {
      setError('ユーザー単語リストの取得に失敗しました');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    fetchUserWords();
  }, []);
  
  const handleRefresh = () => {
    fetchUserWords();
  };

  const handleRemove = async (id: number) => {
    if (!window.confirm('本当にこの単語をリストから削除しますか？')) {
      return;
    }
    
    setRemoveLoading(id);
    
    try {
      const success = await userWordService.removeWordFromUserList(id);
      if (success) {
        // 削除成功時は単語リストから削除
        setWords(words.filter(word => word.id !== id));
      } else {
        setError('単語のリストからの削除に失敗しました');
      }
    } catch (err) {
      setError('単語のリストからの削除に失敗しました');
      console.error(err);
    } finally {
      setRemoveLoading(null);
    }
  };

  if (loading) {
    return <p style={styles.loading}>読み込み中...</p>;
  }

  if (error) {
    return (
      <div style={styles.errorContainer}>
        <p>{error}</p>
        <button onClick={handleRefresh}>再試行</button>
      </div>
    );
  }

  if (words.length === 0) {
    return <p style={styles.noWords}>保存した単語がありません</p>;
  }

  return (
    <div style={styles.container}>
      <div style={navStyles.wordListNav}>
        <h2 style={styles.heading}>マイ単語リスト</h2>
        <Link to="/english-study" style={navStyles.linkButton}>
          全単語リスト
        </Link>
      </div>
      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>単語</th>
            <th style={styles.th}>意味</th>
            <th style={styles.th}>翻訳状態</th>
            <th style={styles.th}>操作</th>
          </tr>
        </thead>
        <tbody>
          {words.map(word => (
            <tr key={word.id}>
              <td style={styles.td}>
                <Link to={`/words/${word.id}`} style={styles.wordLink}>
                  {word.word}
                </Link>
              </td>
              <td style={styles.td}>
                {word.meaning || '翻訳待ち'}
              </td>
              <td style={styles.td}>
                <span style={getStatusStyle(word.translationStatus)}>
                  {word.translationStatus === 'PENDING' ? '翻訳待ち' : 
                   word.translationStatus === 'COMPLETED' ? '翻訳済み' : 'エラー'}
                </span>
              </td>
              <td style={styles.td}>
                <button
                  style={styles.removeButton}
                  onClick={() => handleRemove(word.id)}
                  disabled={removeLoading === word.id}
                >
                  {removeLoading === word.id ? '処理中...' : 'リストから削除'}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default UserWordList; 