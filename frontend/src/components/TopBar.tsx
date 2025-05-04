import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import LoginButton from './LoginButton';

const TopBar: React.FC = () => {
  const { isAuthenticated, logout, tokenInfo } = useAuth();

  return (
    <div className="top-bar">
      <div className="top-bar-container">
        <div className="top-bar-logo">
          <Link to="/">banananbo.com</Link>
        </div>
        <div className="top-bar-right">
          {isAuthenticated ? (
            <div className="user-menu">
              <span className="user-name">
                {tokenInfo?.name || 'ユーザー'}
              </span>
              <button onClick={logout} className="logout-button">
                ログアウト
              </button>
            </div>
          ) : (
            <LoginButton />
          )}
        </div>
      </div>
    </div>
  );
};

export default TopBar; 