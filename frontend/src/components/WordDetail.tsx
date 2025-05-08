import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { wordService } from '../services/wordService';
import { userWordService } from '../services/userWordService';
import { EnglishWord, WordExample } from '../types/EnglishWord';

const styles = {
  container: {
    maxWidth: '800px',
    margin: '0 auto',
    padding: '1rem'
  },
  header: {
    display: 'flex' as const,
    justifyContent: 'space-between' as const,
    alignItems: 'center' as const,
    marginBottom: '1.5rem'
  },
  word: {
    fontSize: '2rem',
    fontWeight: 'bold' as const,
    margin: '0'
  },
  meaning: {
    fontSize: '1.2rem',
    marginBottom: '2rem',
    padding: '0.75rem',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px'
  },
  sectionTitle: {
    fontSize: '1.5rem',
    marginBottom: '1rem',
    borderBottom: '2px solid #f0f0f0',
    paddingBottom: '0.5rem'
  },
  exampleItem: {
    backgroundColor: '#fff',
    padding: '1rem',
    borderRadius: '4px',
    boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
    marginBottom: '1rem'
  },
  exampleText: {
    fontSize: '1.1rem',
    marginBottom: '0.5rem'
  },
  exampleMeta: {
    fontSize: '0.85rem',
    color: '#6c757d',
    display: 'flex' as const,
    justifyContent: 'space-between' as const
  },
  buttonGroup: {
    marginTop: '0.5rem',
    display: 'flex' as const,
    gap: '0.5rem'
  },
  editButton: {
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '3px',
    padding: '4px 8px',
    cursor: 'pointer'
  },
  deleteButton: {
    backgroundColor: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '3px',
    padding: '4px 8px',
    cursor: 'pointer'
  },
  backButton: {
    marginRight: '1rem'
  },
  addExampleForm: {
    marginTop: '2rem',
    backgroundColor: '#f8f9fa',
    padding: '1rem',
    borderRadius: '4px',
    border: '1px solid #ced4da'
  },
  addExampleButton: {
    backgroundColor: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    padding: '0.5rem 1rem',
    fontSize: '1rem',
    cursor: 'pointer',
    marginTop: '1rem',
    display: 'flex',
    alignItems: 'center'
  },
  formGroup: {
    marginBottom: '1rem'
  },
  label: {
    display: 'block',
    marginBottom: '0.5rem',
    fontWeight: 'bold' as const
  },
  input: {
    width: '100%',
    padding: '0.5rem',
    borderRadius: '4px',
    border: '1px solid #ced4da',
    fontSize: '1rem'
  },
  textarea: {
    width: '100%',
    padding: '0.5rem',
    borderRadius: '4px',
    border: '1px solid #ced4da',
    fontSize: '1rem',
    minHeight: '100px'
  },
  submitButton: {
    backgroundColor: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    padding: '0.5rem 1rem',
    fontSize: '1rem',
    cursor: 'pointer'
  },
  loading: {
    textAlign: 'center' as const,
    margin: '2rem'
  },
  error: {
    backgroundColor: '#f8d7da',
    color: '#721c24',
    padding: '1rem',
    borderRadius: '4px',
    marginBottom: '1rem'
  },
  saveButton: {
    backgroundColor: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    padding: '0.5rem 1rem',
    fontSize: '1rem',
    cursor: 'pointer',
    marginLeft: '1rem'
  },
  removeButton: {
    backgroundColor: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    padding: '0.5rem 1rem',
    fontSize: '1rem',
    cursor: 'pointer',
    marginLeft: '1rem'
  }
};

interface ExampleFormData {
  example: string;
  translation: string;
  note: string;
  source: string;
}

const WordDetail: React.FC = () => {
  const { wordId } = useParams<{ wordId: string }>();
  const navigate = useNavigate();
  
  const [word, setWord] = useState<EnglishWord | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSaved, setIsSaved] = useState(false);
  const [savingLoading, setSavingLoading] = useState(false);
  const [showAddExampleForm, setShowAddExampleForm] = useState(false);
  
  const [newExample, setNewExample] = useState<ExampleFormData>({
    example: '',
    translation: '',
    note: '',
    source: ''
  });
  
  const [editingExampleId, setEditingExampleId] = useState<number | null>(null);
  const [editingExample, setEditingExample] = useState<ExampleFormData>({
    example: '',
    translation: '',
    note: '',
    source: ''
  });
  
  const fetchWordDetails = async () => {
    if (!wordId) return;
    
    setLoading(true);
    setError(null);
    
    try {
      const wordData = await wordService.getWordById(parseInt(wordId, 10));
      setWord(wordData);
      
      // 単語がユーザーのリストに保存されているか確認
      const isInUserList = await userWordService.checkWordInUserList(parseInt(wordId, 10));
      setIsSaved(isInUserList);
    } catch (err) {
      setError('単語の詳細情報の取得に失敗しました');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => {
    fetchWordDetails();
  }, [wordId]);
  
  const handleAddExample = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!wordId || !newExample.example.trim()) return;
    
    try {
      await wordService.addExample(
        parseInt(wordId, 10),
        newExample.example,
        newExample.translation || null,
        newExample.note || null,
        newExample.source || null
      );
      
      // フォームをリセット
      setNewExample({
        example: '',
        translation: '',
        note: '',
        source: ''
      });
      
      // 単語データを再取得
      await fetchWordDetails();
    } catch (err) {
      setError('例文の追加に失敗しました');
      console.error(err);
    }
  };
  
  const handleDeleteExample = async (exampleId: number) => {
    if (!wordId || !window.confirm('この例文を削除しますか？')) return;
    
    try {
      await wordService.deleteExample(parseInt(wordId, 10), exampleId);
      
      // 単語データを再取得
      await fetchWordDetails();
    } catch (err) {
      setError('例文の削除に失敗しました');
      console.error(err);
    }
  };
  
  const startEditingExample = (example: WordExample) => {
    setEditingExampleId(example.id);
    setEditingExample({
      example: example.example,
      translation: example.translation || '',
      note: example.note || '',
      source: example.source || ''
    });
  };
  
  const cancelEditingExample = () => {
    setEditingExampleId(null);
  };
  
  const handleUpdateExample = async (e: React.FormEvent, exampleId: number) => {
    e.preventDefault();
    
    if (!wordId || !editingExample.example.trim()) return;
    
    try {
      await wordService.updateExample(
        parseInt(wordId, 10),
        exampleId,
        editingExample.example,
        editingExample.translation || null,
        editingExample.note || null,
        editingExample.source || null
      );
      
      // 編集モードを終了
      setEditingExampleId(null);
      
      // 単語データを再取得
      await fetchWordDetails();
    } catch (err) {
      setError('例文の更新に失敗しました');
      console.error(err);
    }
  };
  
  const handleSaveWord = async () => {
    if (!wordId) return;
    
    setSavingLoading(true);
    try {
      const success = await userWordService.addWordToUserList(parseInt(wordId, 10));
      if (success) {
        setIsSaved(true);
      } else {
        setError('単語の保存に失敗しました');
      }
    } catch (err) {
      setError('単語の保存に失敗しました');
      console.error(err);
    } finally {
      setSavingLoading(false);
    }
  };
  
  const handleRemoveWord = async () => {
    if (!wordId || !window.confirm('この単語をマイリストから削除しますか？')) return;
    
    setSavingLoading(true);
    try {
      const success = await userWordService.removeWordFromUserList(parseInt(wordId, 10));
      if (success) {
        setIsSaved(false);
      } else {
        setError('単語の削除に失敗しました');
      }
    } catch (err) {
      setError('単語の削除に失敗しました');
      console.error(err);
    } finally {
      setSavingLoading(false);
    }
  };
  
  if (loading) {
    return <div style={styles.loading}>読み込み中...</div>;
  }
  
  if (error) {
    return <div style={styles.error}>{error}</div>;
  }
  
  if (!word) {
    return <div style={styles.error}>単語が見つかりませんでした</div>;
  }
  
  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <div>
          <button onClick={() => navigate(-1)} style={styles.backButton}>戻る</button>
          <h1 style={styles.word}>{word.word}</h1>
        </div>
        <div>
          {isSaved ? (
            <button
              onClick={handleRemoveWord}
              style={styles.removeButton}
              disabled={savingLoading}
            >
              {savingLoading ? '処理中...' : 'マイリストから削除'}
            </button>
          ) : (
            <button
              onClick={handleSaveWord}
              style={styles.saveButton}
              disabled={savingLoading}
            >
              {savingLoading ? '処理中...' : 'マイリストに保存'}
            </button>
          )}
        </div>
      </div>
      
      {word.meaning && (
        <div style={styles.meaning}>
          <strong>意味:</strong> {word.meaning}
        </div>
      )}
      
      <h2 style={styles.sectionTitle}>例文一覧</h2>
      
      {word.examples.length === 0 ? (
        <p>例文はまだありません</p>
      ) : (
        word.examples.map((example) => (
          <div key={example.id} style={styles.exampleItem}>
            {editingExampleId === example.id ? (
              <form onSubmit={(e) => handleUpdateExample(e, example.id)}>
                <div style={styles.formGroup}>
                  <label style={styles.label} htmlFor={`edit-example-${example.id}`}>例文:</label>
                  <textarea
                    id={`edit-example-${example.id}`}
                    style={styles.textarea}
                    value={editingExample.example}
                    onChange={(e) => setEditingExample({...editingExample, example: e.target.value})}
                    required
                  />
                </div>
                
                <div style={styles.formGroup}>
                  <label style={styles.label} htmlFor={`edit-translation-${example.id}`}>日本語訳:</label>
                  <textarea
                    id={`edit-translation-${example.id}`}
                    style={{...styles.textarea, minHeight: '80px'}}
                    value={editingExample.translation}
                    onChange={(e) => setEditingExample({...editingExample, translation: e.target.value})}
                  />
                </div>
                
                <div style={styles.formGroup}>
                  <label style={styles.label} htmlFor={`edit-note-${example.id}`}>メモ:</label>
                  <input
                    type="text"
                    id={`edit-note-${example.id}`}
                    style={styles.input}
                    value={editingExample.note}
                    onChange={(e) => setEditingExample({...editingExample, note: e.target.value})}
                  />
                </div>
                
                <div style={styles.formGroup}>
                  <label style={styles.label} htmlFor={`edit-source-${example.id}`}>出典:</label>
                  <input
                    type="text"
                    id={`edit-source-${example.id}`}
                    style={styles.input}
                    value={editingExample.source}
                    onChange={(e) => setEditingExample({...editingExample, source: e.target.value})}
                  />
                </div>
                
                <div style={styles.buttonGroup}>
                  <button type="submit" style={styles.editButton}>
                    保存
                  </button>
                  <button 
                    type="button" 
                    onClick={cancelEditingExample}
                    style={{...styles.deleteButton, backgroundColor: '#6c757d'}}
                  >
                    キャンセル
                  </button>
                </div>
              </form>
            ) : (
              <>
                <p style={styles.exampleText}>{example.example}</p>
                {example.translation && (
                  <p style={{...styles.exampleText, color: '#6c757d', fontStyle: 'italic'}}>
                    {example.translation}
                  </p>
                )}
                <div style={styles.exampleMeta}>
                  <span>
                    {example.source && <span>出典: {example.source}</span>}
                    {example.note && <span> | メモ: {example.note}</span>}
                  </span>
                  <div style={styles.buttonGroup}>
                    <button 
                      onClick={() => startEditingExample(example)}
                      style={styles.editButton}
                    >
                      編集
                    </button>
                    <button 
                      onClick={() => handleDeleteExample(example.id)}
                      style={styles.deleteButton}
                    >
                      削除
                    </button>
                  </div>
                </div>
              </>
            )}
          </div>
        ))
      )}

      <button 
        style={styles.addExampleButton} 
        onClick={() => setShowAddExampleForm(!showAddExampleForm)}
      >
        {showAddExampleForm ? '▲ 例文追加フォームを閉じる' : '▼ 例文を追加する'}
      </button>
      
      {showAddExampleForm && (
        <div style={styles.addExampleForm}>
          <form onSubmit={handleAddExample}>
            <div style={styles.formGroup}>
              <label style={styles.label} htmlFor="new-example">例文:</label>
              <textarea
                id="new-example"
                style={styles.textarea}
                value={newExample.example}
                onChange={(e) => setNewExample({...newExample, example: e.target.value})}
                placeholder="英語の例文を入力してください"
                required
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label} htmlFor="new-translation">日本語訳:</label>
              <textarea
                id="new-translation"
                style={{...styles.textarea, minHeight: '80px'}}
                value={newExample.translation}
                onChange={(e) => setNewExample({...newExample, translation: e.target.value})}
                placeholder="例文の日本語訳を入力してください"
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label} htmlFor="new-note">メモ:</label>
              <input
                type="text"
                id="new-note"
                style={styles.input}
                value={newExample.note}
                onChange={(e) => setNewExample({...newExample, note: e.target.value})}
              />
            </div>
            
            <div style={styles.formGroup}>
              <label style={styles.label} htmlFor="new-source">出典:</label>
              <input
                type="text"
                id="new-source"
                style={styles.input}
                value={newExample.source}
                onChange={(e) => setNewExample({...newExample, source: e.target.value})}
              />
            </div>
            
            <button type="submit" style={styles.submitButton}>
              例文を追加
            </button>
          </form>
        </div>
      )}
    </div>
  );
};

export default WordDetail; 