import React, { useState, useEffect } from 'react';
import './App.css';
import { fetchHelloWorld } from './services/HelloWorldService';

function App() {
  const [message, setMessage] = useState<string>('読み込み中...');
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const getHelloWorld = async () => {
      try {
        const data = await fetchHelloWorld();
        setMessage(data.message);
      } catch (err) {
        setError('バックエンドからのデータ取得に失敗しました');
        console.error('エラー:', err);
      }
    };

    getHelloWorld();
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <h1>バックエンドからのメッセージ</h1>
        {error ? (
          <div className="error-message">{error}</div>
        ) : (
          <div className="message-container">
            <p className="message">{message}</p>
          </div>
        )}
      </header>
    </div>
  );
}

export default App;
