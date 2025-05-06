import React, { useState } from 'react';
import { sentenceService } from '../services/sentenceService';
import { SentenceRequest } from '../types/Sentence';

interface SentenceFormProps {
  onSentenceAdded?: () => void;
}

const SentenceForm: React.FC<SentenceFormProps> = ({ onSentenceAdded }) => {
  const [text, setText] = useState('');
  const [source, setSource] = useState<string | null>('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

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
      setSuccess('センテンスを登録しました。翻訳や要素の抽出、難易度の判定は自動的に行われます。');
      
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

  return (
    <div className="card mb-4">
      <div className="card-header">
        <h5 className="mb-0">新しいセンテンスを登録</h5>
      </div>
      <div className="card-body">
        {error && (
          <div className="alert alert-danger">{error}</div>
        )}
        
        {success && (
          <div className="alert alert-success">{success}</div>
        )}
        
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="text" className="form-label">テキスト</label>
            <textarea
              id="text"
              className="form-control"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="登録したい英文を入力してください"
              required
              rows={4}
            />
            <small className="text-muted">※翻訳や単語・イディオム・文法の抽出、難易度の判定は自動的に行われます</small>
          </div>
          
          <div className="mb-3">
            <label htmlFor="source" className="form-label">出典（任意）</label>
            <input
              type="text"
              id="source"
              className="form-control"
              value={source || ''}
              onChange={(e) => setSource(e.target.value)}
              placeholder="書籍名、ウェブサイト、映画など"
            />
          </div>
          
          <button
            type="submit"
            className="btn btn-primary"
            disabled={isSubmitting}
          >
            {isSubmitting ? '登録中...' : '登録する'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default SentenceForm; 