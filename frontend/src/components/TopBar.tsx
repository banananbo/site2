import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import LoginButton from './LoginButton';

const TopBar: React.FC = () => {
  const { isAuthenticated, logout } = useAuth();
  const [showEnglishMenu, setShowEnglishMenu] = useState(false);

  const toggleEnglishMenu = () => {
    setShowEnglishMenu(!showEnglishMenu);
  };

  return (
    <div className="top-bar">
      <div className="top-bar-container">
        <div className="top-bar-logo">
          <Link to="/">banananbo.com</Link>
        </div>
        <div className="top-bar-menu">
          <div className="dropdown">
            <button className="menu-link dropdown-toggle" onClick={toggleEnglishMenu}>
              英単語学習
            </button>
            {showEnglishMenu && (
              <div className="dropdown-menu">
                <Link to="/english-study" className="dropdown-item" onClick={toggleEnglishMenu}>全単語リスト</Link>
                {isAuthenticated && (
                  <>
                    <Link to="/english-study/my-words" className="dropdown-item" onClick={toggleEnglishMenu}>マイ単語リスト</Link>
                    <Link to="/english-study/my-sentences" className="dropdown-item" onClick={toggleEnglishMenu}>マイセンテンスリスト</Link>
                  </>
                )}
              </div>
            )}
          </div>
          <Link to="/sentences" className="menu-link">センテンス</Link>
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