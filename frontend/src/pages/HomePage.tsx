import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import TopBar from '../components/TopBar';
import '../App.css';

interface Message {
  content: string;
}

const HomePage: React.FC = () => {
  const { isAuthenticated, tokenInfo } = useAuth();
  const [message, setMessage] = useState<Message | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchMessage = async () => {
      if (!isAuthenticated) return;
      
      setLoading(true);
      setError(null);
      
      try {
        const response = await fetch('/api/message', {
          headers: {
            'Authorization': `Bearer ${tokenInfo?.accessToken}`
          }
        });
        
        if (!response.ok) {
          throw new Error(`エラーが発生しました: ${response.status}`);
        }
        
        const data = await response.json();
        setMessage(data);
      } catch (err) {
        setError('メッセージの取得に失敗しました');
        console.error('メッセージ取得エラー:', err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchMessage();
  }, [isAuthenticated, tokenInfo]);

  return (
    <div>
      <TopBar />
      
      <div className="content-container">
        
        {!isAuthenticated ? (
          <div className="welcome-section">
            <p>ログインして、すべての機能にアクセスしてください。</p>
          </div>
        ) : (
          <div className="user-content">
            <h2>ようこそ、{tokenInfo?.name || 'ユーザー'}さん</h2>
            <p>ログインに成功しました。</p>
            
            {loading ? (
              <div className="loading">メッセージを読み込み中...</div>
            ) : error ? (
              <div className="error-message">{error}</div>
            ) : message ? (
              <div className="message-container">
                <h3>サーバーからのメッセージ:</h3>
                <p className="message">{message.content}</p>
              </div>
            ) : null}
          </div>
        )}
      </div>
    </div>
  );
};

export default HomePage; 