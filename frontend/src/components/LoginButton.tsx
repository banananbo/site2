import React, { useState } from 'react';
import { redirectToAuth0 } from '../services/AuthService';

const LoginButton: React.FC = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async () => {
    setIsLoading(true);
    setError(null);
    
    try {
      await redirectToAuth0();
    } catch (err) {
      setError('ログイン処理中にエラーが発生しました');
      console.error('ログインエラー:', err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <button 
        onClick={handleLogin} 
        disabled={isLoading}
        className="login-button"
      >
        {isLoading ? 'ログイン中...' : 'ログイン'}
      </button>
      {error && <p className="error-message">{error}</p>}
    </div>
  );
};

export default LoginButton; 