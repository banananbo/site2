import React, { useState } from 'react';
import SentenceForm from '../components/SentenceForm';
import SentenceList from '../components/SentenceList';
import WordForm from '../components/WordForm';
import { EnglishWord } from '../types/EnglishWord';

const SentencePage: React.FC = () => {
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const [activeTab, setActiveTab] = useState<'sentence' | 'word'>('sentence');

  const handleSentenceAdded = () => {
    // リストの再読み込みをトリガー
    setRefreshTrigger(prev => prev + 1);
  };

  const handleWordRegistered = (word: EnglishWord) => {
    // 単語登録後の処理（必要に応じて実装）
  };

  return (
    <div className="container mt-4">
      <div className="row">
        <div className="col-12">
          <h1 className="mb-4">学習管理</h1>
          
          <div className="row">
            <div className="col-lg-4">
              {/* タブナビゲーション */}
              <ul className="nav nav-tabs mb-3">
                <li className="nav-item">
                  <button 
                    className={`nav-link ${activeTab === 'sentence' ? 'active' : ''}`}
                    onClick={() => setActiveTab('sentence')}
                  >
                    センテンス登録
                  </button>
                </li>
                <li className="nav-item">
                  <button 
                    className={`nav-link ${activeTab === 'word' ? 'active' : ''}`}
                    onClick={() => setActiveTab('word')}
                  >
                    単語登録
                  </button>
                </li>
              </ul>
              
              {/* タブコンテンツ */}
              <div className="tab-content">
                {activeTab === 'sentence' && (
                  <SentenceForm onSentenceAdded={handleSentenceAdded} />
                )}
                
                {activeTab === 'word' && (
                  <div className="card mb-4">
                    <div className="card-header">
                      <h5 className="mb-0">新しい単語を登録</h5>
                    </div>
                    <div className="card-body">
                      <WordForm onWordRegistered={handleWordRegistered} />
                    </div>
                  </div>
                )}
              </div>
            </div>
            
            <div className="col-lg-8">
              <SentenceList refresh={refreshTrigger} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SentencePage; 