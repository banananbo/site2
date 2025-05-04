import React from 'react';
import { useAuth } from '../context/AuthContext';
import LoginButton from '../components/LoginButton';
import { fetchHelloWorld } from '../services/HelloWorldService';
import { useState, useEffect } from 'react';

const HomePage: React.FC = () => {
  const { isAuthenticated, logout, tokenInfo } = useAuth();
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

  // JSONをフォーマットして表示する関数
  const formatJson = (json: any) => {
    return JSON.stringify(json, null, 2);
  };

  return (
    <div className="home-container">
      <header className="App-header">
        <h1>Auth0ログインサンプル</h1>
        
        {isAuthenticated ? (
          <div className="authenticated-container">
            <p>ログイン済みです！</p>
            <button onClick={logout} className="logout-button">ログアウト</button>
            
            {tokenInfo && (
              <div className="token-info">
                <h3>トークン情報</h3>
                <div className="token-details">
                  <p><strong>タイプ:</strong> {tokenInfo.tokenType}</p>
                  <p><strong>有効期限:</strong> {tokenInfo.expiresIn}秒</p>
                  <details>
                    <summary>ID Token</summary>
                    <pre className="token-pre">{tokenInfo.idToken}</pre>
                  </details>
                  <details>
                    <summary>Access Token</summary>
                    <pre className="token-pre">{tokenInfo.accessToken}</pre>
                  </details>
                </div>
              </div>
            )}
          </div>
        ) : (
          <div className="login-container">
            <p>ログインしてください</p>
            <LoginButton />
          </div>
        )}

        <div className="message-section">
          <h2>バックエンドからのメッセージ</h2>
          {error ? (
            <div className="error-message">{error}</div>
          ) : (
            <div className="message-container">
              <p className="message">{message}</p>
            </div>
          )}
        </div>
      </header>
    </div>
  );
};

export default HomePage; 