import React, { useState, useEffect } from 'react';
import { Sentence, TranslationStatus } from '../types/Sentence';
import { sentenceService } from '../services/sentenceService';

interface SentenceListProps {
  refresh: number;
}

const styles = {
  container: {
    width: '100%',
  },
  loading: {
    textAlign: 'center' as const,
    padding: '20px',
    color: '#6c757d',
  },
  error: {
    backgroundColor: '#f8d7da',
    color: '#721c24',
    padding: '12px 16px',
    borderRadius: '4px',
    marginBottom: '16px',
    border: '1px solid #f5c6cb',
  },
  empty: {
    backgroundColor: '#d1ecf1',
    color: '#0c5460',
    padding: '12px 16px',
    borderRadius: '4px',
    marginBottom: '16px',
    border: '1px solid #bee5eb',
  },
  card: {
    border: '1px solid #dee2e6',
    borderRadius: '4px',
    marginBottom: '16px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
  },
  cardHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '12px 16px',
    backgroundColor: '#f8f9fa',
    borderBottom: '1px solid #dee2e6',
    borderTopLeftRadius: '4px',
    borderTopRightRadius: '4px',
  },
  cardTitle: {
    margin: 0,
    fontSize: '18px',
    fontWeight: 'bold' as const,
  },
  cardBody: {
    padding: '16px',
  },
  cardText: {
    margin: '0 0 16px 0',
    fontSize: '16px',
    lineHeight: 1.5,
  },
  translation: {
    marginTop: '8px',
    fontSize: '16px',
  },
  badge: {
    display: 'inline-block',
    padding: '6px 10px',
    borderRadius: '12px',
    fontSize: '12px',
    fontWeight: 'bold' as const,
    color: 'white',
    marginRight: '8px',
  },
  badgeSecondary: {
    backgroundColor: '#6c757d',
  },
  badgeInfo: {
    backgroundColor: '#17a2b8',
  },
  badgeSuccess: {
    backgroundColor: '#28a745',
  },
  badgeDanger: {
    backgroundColor: '#dc3545',
  },
  badgeWarning: {
    backgroundColor: '#ffc107',
    color: '#212529',
  },
  processing: {
    display: 'flex',
    alignItems: 'center',
    margin: '12px 0',
  },
  spinner: {
    width: '16px',
    height: '16px',
    border: '2px solid rgba(0, 123, 255, 0.3)',
    borderTop: '2px solid #007bff',
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
    marginRight: '8px',
  },
  buttonContainer: {
    display: 'flex',
    justifyContent: 'space-between',
    marginTop: '16px',
  },
  button: {
    padding: '8px 12px',
    borderRadius: '4px',
    border: 'none',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500' as const,
  },
  buttonOutlinePrimary: {
    backgroundColor: 'transparent',
    border: '1px solid #007bff',
    color: '#007bff',
  },
  buttonOutlineDanger: {
    backgroundColor: 'transparent',
    border: '1px solid #dc3545',
    color: '#dc3545',
  },
  detailsContainer: {
    marginTop: '16px',
    padding: '16px',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px',
  },
  fieldContainer: {
    marginBottom: '12px',
  },
  label: {
    fontWeight: 'bold' as const,
    marginRight: '6px',
  },
  row: {
    display: 'flex',
    flexWrap: 'wrap' as const,
    margin: '0 -8px',
  },
  col: {
    flex: '1 0 33%',
    padding: '0 8px',
    marginBottom: '16px',
  },
  sectionTitle: {
    fontSize: '16px',
    fontWeight: 'bold' as const,
    marginBottom: '8px',
  },
  list: {
    listStyle: 'none',
    padding: 0,
    margin: 0,
    border: '1px solid #dee2e6',
    borderRadius: '4px',
  },
  listItem: {
    padding: '8px 12px',
    borderBottom: '1px solid #dee2e6',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  textMuted: {
    color: '#6c757d',
    fontSize: '14px',
  },
  autoUpdate: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '8px',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px',
    marginBottom: '16px',
    fontSize: '14px',
    color: '#6c757d',
  },
  '@keyframes spin': {
    '0%': { transform: 'rotate(0deg)' },
    '100%': { transform: 'rotate(360deg)' }
  },
  processingCard: {
    border: '1px solid #b8daff',
    boxShadow: '0 2px 6px rgba(0, 123, 255, 0.15)',
  },
  pendingCard: {
    border: '1px solid #d6d8db',
    boxShadow: '0 2px 6px rgba(108, 117, 125, 0.15)',
  }
};

const SentenceList: React.FC<SentenceListProps> = ({ refresh }) => {
  const [sentences, setSentences] = useState<Sentence[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedSentence, setExpandedSentence] = useState<number | null>(null);

  // センテンス一覧を取得
  useEffect(() => {
    const fetchSentences = async () => {
      setLoading(true);
      try {
        const data = await sentenceService.getAllSentences();
        setSentences(data);
        setError(null);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'センテンスの取得に失敗しました');
      } finally {
        setLoading(false);
      }
    };

    fetchSentences();
  }, [refresh]);

  // 定期的に更新（5秒ごと）- 処理中のセンテンスがある場合のみ
  useEffect(() => {
    const hasPendingOrProcessing = sentences.some(
      s => s.translationStatus === TranslationStatus.PENDING || 
           s.translationStatus === TranslationStatus.PROCESSING
    );
    
    if (!hasPendingOrProcessing) return;
    
    const intervalId = setInterval(async () => {
      try {
        const data = await sentenceService.getAllSentences();
        setSentences(data);
      } catch (error) {
        console.error('自動更新中にエラーが発生しました:', error);
      }
    }, 5000);
    
    return () => clearInterval(intervalId);
  }, [sentences]);

  // センテンスの削除
  const handleDelete = async (id: number) => {
    if (window.confirm('このセンテンスを削除してもよろしいですか？')) {
      try {
        const success = await sentenceService.deleteSentence(id);
        if (success) {
          setSentences(sentences.filter(sentence => sentence.id !== id));
        } else {
          setError('センテンスの削除に失敗しました');
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : '削除中にエラーが発生しました');
      }
    }
  };

  // 詳細表示の切り替え
  const toggleDetails = (id: number) => {
    setExpandedSentence(expandedSentence === id ? null : id);
  };

  if (loading) return <div style={styles.loading}>読み込み中...</div>;
  if (error) return <div style={styles.error}>{error}</div>;
  if (sentences.length === 0) return <div style={styles.empty}>センテンスはまだ登録されていません</div>;

  // 処理中または待機中のセンテンスがあるかどうか
  const hasPendingOrProcessing = sentences.some(
    s => s.translationStatus === TranslationStatus.PENDING || 
         s.translationStatus === TranslationStatus.PROCESSING
  );

  return (
    <div style={styles.container}>
      {hasPendingOrProcessing && (
        <div style={styles.autoUpdate}>
          <div className="sentence-spinner"></div>
          センテンスの処理状況を自動更新中です...（5秒ごと）
        </div>
      )}
      
      {sentences.map(sentence => {
        // ステータスに基づいたカードスタイルを設定
        let cardStyle = {...styles.card};
        if (sentence.translationStatus === TranslationStatus.PROCESSING) {
          cardStyle = {...styles.card, ...styles.processingCard};
        } else if (sentence.translationStatus === TranslationStatus.PENDING) {
          cardStyle = {...styles.card, ...styles.pendingCard};
        }
        
        return (
          <div key={sentence.id} style={cardStyle}>
            <div style={styles.cardHeader}>
              <h5 style={styles.cardTitle}>センテンス #{sentence.id}</h5>
              <div>
                <span style={{
                  ...styles.badge,
                  ...getStatusStyle(sentence.translationStatus)
                }}>
                  {getStatusLabel(sentence.translationStatus)}
                </span>
                <span style={{
                  ...styles.badge,
                  ...getDifficultyStyle(sentence.difficulty)
                }}>
                  {getDifficultyLabel(sentence.difficulty)}
                </span>
              </div>
            </div>
            <div style={styles.cardBody}>
              <p style={styles.cardText}>{sentence.text}</p>
              
              {sentence.translationStatus === TranslationStatus.PROCESSING && (
                <div style={styles.processing}>
                  <div style={styles.spinner}></div>
                  <span style={styles.textMuted}>翻訳と要素抽出中...</span>
                </div>
              )}
              
              {sentence.translationStatus === TranslationStatus.PENDING && (
                <div style={styles.textMuted}>
                  翻訳と要素抽出の処理待ちです...
                </div>
              )}
              
              {sentence.translation && (
                <p style={styles.translation}>
                  <strong>翻訳:</strong> {sentence.translation}
                </p>
              )}
              
              <div style={styles.buttonContainer}>
                <button
                  style={{...styles.button, ...styles.buttonOutlinePrimary}}
                  onClick={() => toggleDetails(sentence.id)}
                >
                  {expandedSentence === sentence.id ? '詳細を隠す' : '詳細を表示'}
                </button>
                <button
                  style={{...styles.button, ...styles.buttonOutlineDanger}}
                  onClick={() => handleDelete(sentence.id)}
                >
                  削除
                </button>
              </div>
              
              {expandedSentence === sentence.id && (
                <div style={styles.detailsContainer}>
                  {sentence.note && (
                    <div style={styles.fieldContainer}>
                      <strong style={styles.label}>メモ:</strong> {sentence.note}
                    </div>
                  )}
                  {sentence.source && (
                    <div style={styles.fieldContainer}>
                      <strong style={styles.label}>出典:</strong> {sentence.source}
                    </div>
                  )}
                  
                  <div style={styles.fieldContainer}>
                    <strong style={styles.label}>作成日:</strong> {new Date(sentence.createdAt).toLocaleString()}
                  </div>
                  
                  <div style={styles.row}>
                    <div style={styles.col}>
                      <h6 style={styles.sectionTitle}>関連する単語</h6>
                      {sentence.words.length > 0 ? (
                        <ul style={styles.list}>
                          {sentence.words.map(word => (
                            <li key={word.id} style={styles.listItem}>
                              <strong>{word.word}</strong>
                              {word.meaning && <span style={styles.textMuted}>{word.meaning}</span>}
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <p style={styles.textMuted}>
                          {sentence.translationStatus === TranslationStatus.COMPLETED ? 
                            '抽出された単語はありません' : 
                            '処理中または未処理です'}
                        </p>
                      )}
                    </div>
                    
                    <div style={styles.col}>
                      <h6 style={styles.sectionTitle}>関連するイディオム</h6>
                      {sentence.idioms.length > 0 ? (
                        <ul style={styles.list}>
                          {sentence.idioms.map(idiom => (
                            <li key={idiom.id} style={styles.listItem}>
                              <strong>{idiom.phrase}</strong>
                              {idiom.meaning && <span style={styles.textMuted}>{idiom.meaning}</span>}
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <p style={styles.textMuted}>
                          {sentence.translationStatus === TranslationStatus.COMPLETED ? 
                            '抽出されたイディオムはありません' : 
                            '処理中または未処理です'}
                        </p>
                      )}
                    </div>
                    
                    <div style={styles.col}>
                      <h6 style={styles.sectionTitle}>関連する文法</h6>
                      {sentence.grammars.length > 0 ? (
                        <ul style={styles.list}>
                          {sentence.grammars.map(grammar => (
                            <li key={grammar.id} style={styles.listItem}>
                              <strong>{grammar.pattern}</strong>
                              {grammar.explanation && (
                                <p style={styles.textMuted}>{grammar.explanation}</p>
                              )}
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <p style={styles.textMuted}>
                          {sentence.translationStatus === TranslationStatus.COMPLETED ? 
                            '抽出された文法はありません' : 
                            '処理中または未処理です'}
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
};

// 難易度に応じたスタイルを取得
function getDifficultyStyle(difficulty: string): object {
  switch (difficulty) {
    case 'BEGINNER': return styles.badgeSuccess;
    case 'INTERMEDIATE': return styles.badgeInfo;
    case 'ADVANCED': return styles.badgeWarning;
    case 'NATIVE': return styles.badgeDanger;
    default: return styles.badgeSecondary;
  }
}

// 難易度の日本語表示を取得
function getDifficultyLabel(difficulty: string): string {
  switch (difficulty) {
    case 'BEGINNER': return '初級';
    case 'INTERMEDIATE': return '中級';
    case 'ADVANCED': return '上級';
    case 'NATIVE': return 'ネイティブ';
    default: return '不明';
  }
}

// 翻訳ステータスに応じたスタイルを取得
function getStatusStyle(status: string): object {
  switch (status) {
    case 'PENDING': return styles.badgeSecondary;
    case 'PROCESSING': return styles.badgeInfo;
    case 'COMPLETED': return styles.badgeSuccess;
    case 'ERROR': return styles.badgeDanger;
    default: return styles.badgeSecondary;
  }
}

// 翻訳ステータスの日本語表示を取得
function getStatusLabel(status: string): string {
  switch (status) {
    case 'PENDING': return '待機中';
    case 'PROCESSING': return '処理中';
    case 'COMPLETED': return '完了';
    case 'ERROR': return 'エラー';
    default: return '不明';
  }
}

// スタイルタグをヘッダーに追加
React.useEffect(() => {
  const styleElement = document.createElement('style');
  styleElement.innerHTML = `
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
    .sentence-spinner {
      width: 16px;
      height: 16px;
      border: 2px solid rgba(0, 123, 255, 0.3);
      border-top: 2px solid #007bff;
      border-radius: 50%;
      animation: spin 1s linear infinite;
      display: inline-block;
      margin-right: 8px;
    }
  `;
  document.head.appendChild(styleElement);
  
  return () => {
    document.head.removeChild(styleElement);
  };
}, []);

export default SentenceList; 