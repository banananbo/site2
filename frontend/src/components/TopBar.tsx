import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import LoginButton from './LoginButton';

const TopBar: React.FC = () => {
  const { isAuthenticated, logout } = useAuth();

  return (
    <div className="top-bar">
      <div className="top-bar-container">
        <div className="top-bar-logo">
          <Link to="/">banananbo.com</Link>
        </div>
        <div className="top-bar-menu">
          <Link to="/english-study" className="menu-link">英単語学習</Link>
        </div>
        <div className="top-bar-right">
          {isAuthenticated ? (
            <div className="user-menu">
              <Link to="/profile" className="profile-link">プロフィール</Link>
              <button className="logout-button" onClick={logout}>ログアウト</button>
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