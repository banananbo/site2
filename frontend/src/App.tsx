import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import HomePage from './pages/HomePage';
import CallbackPage from './pages/CallbackPage';
import ProfilePage from './pages/ProfilePage';
import SentencePage from './pages/SentencePage';
import { AuthProvider } from './context/AuthContext';
import TopBar from './components/TopBar';
import WordForm from './components/WordForm';
import WordList from './components/WordList';
import WordDetail from './components/WordDetail';
import { EnglishWord } from './types/EnglishWord';

// 英単語学習ページのコンポーネント
const EnglishStudyPage: React.FC = () => {
  const [refreshList, setRefreshList] = useState(false);

  const handleWordRegistered = (word: EnglishWord) => {
    // リストを更新するためにステートを切り替える
    setRefreshList(!refreshList);
  };

  return (
    <div className="english-study-page">
      <h1>英単語学習アプリ</h1>
      <WordForm onWordRegistered={handleWordRegistered} />
      <WordList key={refreshList.toString()} />
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
            <Route path="/english-study/words/:wordId" element={<WordDetail />} />
            <Route path="/sentences" element={<SentencePage />} />
          </Routes>
        </main>
      </Router>
    </AuthProvider>
  );
};

export default App;
