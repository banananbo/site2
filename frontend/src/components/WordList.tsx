import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { wordService } from '../services/wordService';
import { userWordService } from '../services/userWordService';
import { EnglishWord, WordExample } from '../types/EnglishWord';
import { useAuth } from '../context/AuthContext';

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
  deleteButton: {
    backgroundColor: '#ff4d4d',
    color: 'white',
    border: 'none',
    borderRadius: '3px',
    padding: '4px 8px',
    cursor: 'pointer'
  },
  exampleList: {
    listStyleType: 'none',
    padding: 0,
    margin: 0
  },
  exampleItem: {
    margin: '0.25rem 0',
    fontSize: '0.9rem'
  },
  exampleSource: {
    fontSize: '0.75rem',
    color: '#6c757d',
    marginLeft: '0.5rem'
  },
  noExamples: {
    color: '#6c757d',
    fontStyle: 'italic'
  },
  wordLink: {
    color: '#007bff',
    textDecoration: 'none',
    fontWeight: 'bold' as const,
    ':hover': {
      textDecoration: 'underline'
    }
  },
  actionButtonsContainer: {
    display: 'flex',
    gap: '5px',
    flexDirection: 'column' as const
  },
  addToListButton: {
    backgroundColor: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '3px',
    padding: '4px 8px',
    cursor: 'pointer',
    fontSize: '0.8rem'
  },
  inListButton: {
    backgroundColor: '#6c757d',
    color: 'white',
    border: 'none',
    borderRadius: '3px',
    padding: '4px 8px',
    cursor: 'default',
    fontSize: '0.8rem'
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

const ExamplesList: React.FC<{ examples: WordExample[] }> = ({ examples }) => {
  if (examples.length === 0) {
    return <span style={styles.noExamples}>-</span>;
  }
  
  return (
    <ul style={styles.exampleList}>
      {examples.map((example) => (
        <li key={example.id} style={styles.exampleItem}>
          <div>
            <span>{example.example}</span>
            {example.translation && (
              <div style={{ color: '#6c757d', fontStyle: 'italic', fontSize: '0.85rem', marginTop: '0.25rem' }}>
                {example.translation}
              </div>
            )}
          </div>
          {example.source && (
            <span style={styles.exampleSource}>
              （出典: {example.source}）
            </span>
          )}
        </li>
      ))}
    </ul>
  );
};

const WordList: React.FC = () => {
  const [words, setWords] = useState<EnglishWord[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleteLoading, setDeleteLoading] = useState<number | null>(null);
  const [addToListLoading, setAddToListLoading] = useState<number | null>(null);
  const [userWordIds, setUserWordIds] = useState<number[]>([]);
  const { isAuthenticated } = useAuth();
  
  const fetchWords = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const fetchedWords = await wordService.getAllWords();
      setWords(fetchedWords);
      
      // ログイン中の場合、ユーザーの単語リストも取得
      if (isAuthenticated) {
        try {
          const userWords = await userWordService.getUserWords();
          const userWordIdArray = userWords.map(word => word.id);
          setUserWordIds(userWordIdArray);
        } catch (err) {
          console.error('ユーザー単語リストの取得に失敗しました', err);
        }
      }
    } catch (err) {
      setError('単語リストの取得に失敗しました');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    fetchWords();
  }, [isAuthenticated]);
  
  const handleRefresh = () => {
    fetchWords();
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('本当にこの単語を削除しますか？')) {
      return;
    }
    
    setDeleteLoading(id);
    
    try {
      const success = await wordService.deleteWord(id);
      if (success) {
        // 削除成功時は単語リストから削除
        setWords(words.filter(word => word.id !== id));
      } else {
        setError('単語の削除に失敗しました');
      }
    } catch (err) {
      setError('単語の削除に失敗しました');
      console.error(err);
    } finally {
      setDeleteLoading(null);
    }
  };

  const handleAddToList = async (id: number) => {
    if (!isAuthenticated) return;
    
    setAddToListLoading(id);
    
    try {
      const success = await userWordService.addWordToUserList(id);
      if (success) {
        // 成功時はユーザー単語IDリストに追加
        setUserWordIds([...userWordIds, id]);
      } else {
        setError('単語をマイリストに追加できませんでした');
      }
    } catch (err) {
      setError('単語をマイリストに追加できませんでした');
      console.error(err);
    } finally {
      setAddToListLoading(null);
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
    return <p style={styles.noWords}>単語がまだ登録されていません</p>;
  }

  return (
    <div style={styles.container}>
      <div style={navStyles.wordListNav}>
        <h2 style={styles.heading}>英単語リスト</h2>
        {isAuthenticated && (
          <Link to="/english-study/my-words" style={navStyles.linkButton}>
            マイ単語リスト
          </Link>
        )}
      </div>
      <button onClick={handleRefresh}>更新</button>
      
      <table style={styles.table}>
        <thead>
          <tr>
            <th style={styles.th}>単語</th>
            <th style={styles.th}>意味</th>
            <th style={styles.th}>例文</th>
            <th style={styles.th}>状態</th>
            <th style={styles.th}>操作</th>
          </tr>
        </thead>
        <tbody>
          {words.map((word) => (
            <tr key={word.id}>
              <td style={styles.td}>
                <Link to={`/words/${word.id}`} style={styles.wordLink}>
                  {word.word}
                </Link>
              </td>
              <td style={styles.td}>{word.meaning || '-'}</td>
              <td style={styles.td}>
                <ExamplesList examples={word.examples} />
              </td>
              <td style={styles.td}>
                <span style={getStatusStyle(word.translationStatus)}>
                  {word.translationStatus === 'PENDING' && '処理待ち'}
                  {word.translationStatus === 'COMPLETED' && '完了'}
                  {word.translationStatus === 'ERROR' && 'エラー'}
                </span>
              </td>
              <td style={styles.td}>
                <div style={styles.actionButtonsContainer}>
                  <button 
                    onClick={() => handleDelete(word.id)}
                    disabled={deleteLoading === word.id}
                    style={styles.deleteButton}
                  >
                    {deleteLoading === word.id ? '削除中...' : '削除'}
                  </button>
                  
                  {isAuthenticated && (
                    userWordIds.includes(word.id) ? (
                      <button 
                        style={styles.inListButton}
                        disabled={true}
                      >
                        マイリスト登録済み
                      </button>
                    ) : (
                      <button 
                        onClick={() => handleAddToList(word.id)}
                        disabled={addToListLoading === word.id}
                        style={styles.addToListButton}
                      >
                        {addToListLoading === word.id ? '追加中...' : 'マイリストに追加'}
                      </button>
                    )
                  )}
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default WordList; 