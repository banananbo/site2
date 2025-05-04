import React from 'react';
import { useAuth } from '../context/AuthContext';

const LoginButton: React.FC = () => {
  const { login } = useAuth();

  return (
    <button onClick={login} className="login-button">
      ログイン
    </button>
  );
};

export default LoginButton; 