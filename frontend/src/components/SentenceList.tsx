import React, { useState, useEffect } from 'react';
import { Sentence, TranslationStatus } from '../types/Sentence';
import { sentenceService } from '../services/sentenceService';

interface SentenceListProps {
  refresh: number;
}

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

  if (loading) return <div className="text-center p-4">読み込み中...</div>;
  if (error) return <div className="alert alert-danger">{error}</div>;
  if (sentences.length === 0) return <div className="alert alert-info">センテンスはまだ登録されていません</div>;

  return (
    <div className="sentence-list">
      {sentences.map(sentence => (
        <div key={sentence.id} className="card mb-3">
          <div className="card-header d-flex justify-content-between align-items-center">
            <h5 className="mb-0">センテンス #{sentence.id}</h5>
            <div>
              <span className={`badge bg-${getStatusColor(sentence.translationStatus)} me-2`}>
                {getStatusLabel(sentence.translationStatus)}
              </span>
              <span className={`badge bg-${getDifficultyColor(sentence.difficulty)}`}>
                {getDifficultyLabel(sentence.difficulty)}
              </span>
            </div>
          </div>
          <div className="card-body">
            <p className="card-text">{sentence.text}</p>
            
            {sentence.translationStatus === TranslationStatus.PROCESSING && (
              <div className="d-flex align-items-center my-2">
                <div className="spinner-border spinner-border-sm text-primary me-2" role="status">
                  <span className="visually-hidden">処理中...</span>
                </div>
                <span className="text-muted">翻訳と要素抽出中...</span>
              </div>
            )}
            
            {sentence.translationStatus === TranslationStatus.PENDING && (
              <div className="text-muted my-2">
                翻訳と要素抽出の処理待ちです...
              </div>
            )}
            
            {sentence.translation && (
              <p className="card-text">
                <strong>翻訳:</strong> {sentence.translation}
              </p>
            )}
            
            <div className="d-flex justify-content-between mt-3">
              <button
                className="btn btn-outline-primary btn-sm"
                onClick={() => toggleDetails(sentence.id)}
              >
                {expandedSentence === sentence.id ? '詳細を隠す' : '詳細を表示'}
              </button>
              <button
                className="btn btn-outline-danger btn-sm"
                onClick={() => handleDelete(sentence.id)}
              >
                削除
              </button>
            </div>
            
            {expandedSentence === sentence.id && (
              <div className="mt-3">
                {sentence.note && (
                  <div className="mb-2">
                    <strong>メモ:</strong> {sentence.note}
                  </div>
                )}
                {sentence.source && (
                  <div className="mb-2">
                    <strong>出典:</strong> {sentence.source}
                  </div>
                )}
                
                <div className="mb-2">
                  <strong>作成日:</strong> {new Date(sentence.createdAt).toLocaleString()}
                </div>
                
                <div className="row mt-4">
                  <div className="col-md-4">
                    <h6>関連する単語</h6>
                    {sentence.words.length > 0 ? (
                      <ul className="list-group">
                        {sentence.words.map(word => (
                          <li key={word.id} className="list-group-item d-flex justify-content-between align-items-center">
                            {word.word}
                            {word.meaning && <span className="text-muted small">{word.meaning}</span>}
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-muted">
                        {sentence.translationStatus === TranslationStatus.COMPLETED ? 
                          '抽出された単語はありません' : 
                          '処理中または未処理です'}
                      </p>
                    )}
                  </div>
                  
                  <div className="col-md-4">
                    <h6>関連するイディオム</h6>
                    {sentence.idioms.length > 0 ? (
                      <ul className="list-group">
                        {sentence.idioms.map(idiom => (
                          <li key={idiom.id} className="list-group-item d-flex justify-content-between align-items-center">
                            {idiom.phrase}
                            {idiom.meaning && <span className="text-muted small">{idiom.meaning}</span>}
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-muted">
                        {sentence.translationStatus === TranslationStatus.COMPLETED ? 
                          '抽出されたイディオムはありません' : 
                          '処理中または未処理です'}
                      </p>
                    )}
                  </div>
                  
                  <div className="col-md-4">
                    <h6>関連する文法</h6>
                    {sentence.grammars.length > 0 ? (
                      <ul className="list-group">
                        {sentence.grammars.map(grammar => (
                          <li key={grammar.id} className="list-group-item">
                            {grammar.pattern}
                            {grammar.explanation && (
                              <p className="text-muted small mb-0">{grammar.explanation}</p>
                            )}
                          </li>
                        ))}
                      </ul>
                    ) : (
                      <p className="text-muted">
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
      ))}
    </div>
  );
};

// 難易度に応じた表示色を取得
function getDifficultyColor(difficulty: string): string {
  switch (difficulty) {
    case 'BEGINNER': return 'success';
    case 'INTERMEDIATE': return 'info';
    case 'ADVANCED': return 'warning';
    case 'NATIVE': return 'danger';
    default: return 'secondary';
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

// 翻訳ステータスに応じた表示色を取得
function getStatusColor(status: string): string {
  switch (status) {
    case 'PENDING': return 'secondary';
    case 'PROCESSING': return 'info';
    case 'COMPLETED': return 'success';
    case 'ERROR': return 'danger';
    default: return 'secondary';
  }
}

// 翻訳ステータスの日本語表示を取得
function getStatusLabel(status: string): string {
  switch (status) {
    case 'PENDING': return '未処理';
    case 'PROCESSING': return '処理中';
    case 'COMPLETED': return '完了';
    case 'ERROR': return 'エラー';
    default: return '不明';
  }
}

export default SentenceList; 