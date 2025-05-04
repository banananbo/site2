import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAuthCodeFromUrl, exchangeCodeForToken } from '../services/AuthService';
import { useAuth } from '../context/AuthContext';

const CallbackPage: React.FC = () => {
  const [error, setError] = useState<string | null>(null);
  const [isProcessing, setIsProcessing] = useState<boolean>(true);
  const navigate = useNavigate();
  const { setIsAuthenticated, setAuthCode, setTokenInfo } = useAuth();

  useEffect(() => {
    const handleCallback = async () => {
      try {
        setIsProcessing(true);
        const code = getAuthCodeFromUrl();
        
        if (!code) {
          setError('認証コードが見つかりません');
          setIsProcessing(false);
          return;
        }

        // 認証コードを保存
        setAuthCode(code);
        
        // バックエンドAPIを通じてトークンを取得
        const tokenResponse = await exchangeCodeForToken(code);
        
        // トークン情報とログイン状態を保存
        setTokenInfo(tokenResponse);
        setIsAuthenticated(true);
        
        // 処理完了
        setIsProcessing(false);
        
        // ホームページにリダイレクト
        navigate('/');
      } catch (err) {
        setError('認証処理中にエラーが発生しました');
        setIsProcessing(false);
        console.error('コールバックエラー:', err);
      }
    };

    handleCallback();
  }, [navigate, setAuthCode, setIsAuthenticated, setTokenInfo]);

  if (error) {
    return (
      <div className="callback-container">
        <h2>認証エラー</h2>
        <p className="error-message">{error}</p>
        <button onClick={() => navigate('/')}>ホームに戻る</button>
      </div>
    );
  }

  return (
    <div className="callback-container">
      <h2>認証処理中...</h2>
      <p>しばらくお待ちください...</p>
      {isProcessing && (
        <div className="loading-spinner">
          <div className="spinner"></div>
        </div>
      )}
    </div>
  );
};

export default CallbackPage; 