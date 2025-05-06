import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import HomePage from './pages/HomePage';
import CallbackPage from './pages/CallbackPage';
import ProfilePage from './pages/ProfilePage';
import { AuthProvider } from './context/AuthContext';
import TopBar from './components/TopBar';
import WordForm from './components/WordForm';
import WordList from './components/WordList';
import WordDetail from './components/WordDetail';
import SentenceForm from './components/SentenceForm';
import SentenceList from './components/SentenceList';
import { EnglishWord } from './types/EnglishWord';

// 英単語学習ページのコンポーネント
const EnglishStudyPage: React.FC = () => {
  const [refreshWordList, setRefreshWordList] = useState(false);
  const [refreshSentenceList, setRefreshSentenceList] = useState(0);
  const [activeTab, setActiveTab] = useState<'word' | 'sentence'>('word');

  const handleWordRegistered = (word: EnglishWord) => {
    // リストを更新するためにステートを切り替える
    setRefreshWordList(!refreshWordList);
  };

  const handleSentenceAdded = () => {
    // センテンスリストを更新
    setRefreshSentenceList(prev => prev + 1);
  };

  const styles = {
    container: {
      maxWidth: '1200px',
      margin: '0 auto',
      padding: '20px',
    },
    header: {
      marginBottom: '32px',
      textAlign: 'center' as const,
      color: '#333',
      fontWeight: 600 as const,
    },
    tabContainer: {
      display: 'flex' as const,
      borderBottom: '1px solid #e0e0e0',
      marginBottom: '24px',
    },
    tab: {
      padding: '12px 24px',
      cursor: 'pointer',
      borderRadius: '4px 4px 0 0',
      fontWeight: 500 as const,
      fontSize: '16px',
      transition: 'all 0.2s ease',
      userSelect: 'none' as const,
      marginRight: '8px',
    },
    activeTab: {
      backgroundColor: '#ffffff',
      borderTop: '2px solid #4a6cf7',
      borderLeft: '1px solid #e0e0e0',
      borderRight: '1px solid #e0e0e0',
      borderBottom: '1px solid #ffffff',
      marginBottom: '-1px',
      color: '#4a6cf7',
    },
    inactiveTab: {
      backgroundColor: '#f5f7fb',
      color: '#667085',
      border: '1px solid transparent',
    },
    formContainer: {
      marginBottom: '32px',
      backgroundColor: '#ffffff',
      borderRadius: '8px',
      boxShadow: '0 2px 10px rgba(0, 0, 0, 0.05)',
    },
    sectionHeader: {
      fontSize: '24px',
      fontWeight: 600 as const,
      color: '#333',
      margin: '32px 0 16px 0',
      borderBottom: '2px solid #f0f0f0',
      paddingBottom: '8px',
    },
    listSection: {
      marginTop: '24px',
    },
  };

  return (
    <div style={styles.container}>
      <h1 style={styles.header}>英単語学習アプリ</h1>

      {/* タブ切り替え */}
      <div style={styles.tabContainer}>
        <div 
          style={{
            ...styles.tab,
            ...(activeTab === 'word' ? styles.activeTab : styles.inactiveTab)
          }}
          onClick={() => setActiveTab('word')}
        >
          単語登録
        </div>
        <div 
          style={{
            ...styles.tab,
            ...(activeTab === 'sentence' ? styles.activeTab : styles.inactiveTab)
          }}
          onClick={() => setActiveTab('sentence')}
        >
          センテンス登録
        </div>
      </div>

      {/* フォーム表示 */}
      <div style={styles.formContainer}>
        {activeTab === 'word' ? (
          <WordForm onWordRegistered={handleWordRegistered} />
        ) : (
          <SentenceForm onSentenceAdded={handleSentenceAdded} />
        )}
      </div>

      {/* 単語リスト */}
      <h2 style={styles.sectionHeader}>単語リスト</h2>
      <div style={styles.listSection}>
        <WordList key={refreshWordList.toString()} />
      </div>

      {/* センテンスリスト */}
      <h2 style={styles.sectionHeader}>センテンスリスト</h2>
      <div style={styles.listSection}>
        <SentenceList refresh={refreshSentenceList} />
      </div>
    </div>
  );
};

const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <TopBar />
        <main className="app-content">
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/callback" element={<CallbackPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/english-study" element={<EnglishStudyPage />} />
            <Route path="/words/:wordId" element={<WordDetail />} />
          </Routes>
        </main>
      </Router>
    </AuthProvider>
  );
};

export default App;
