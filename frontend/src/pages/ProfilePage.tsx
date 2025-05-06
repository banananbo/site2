import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import UserWordList from '../components/UserWordList';
import UserSentenceList from '../components/UserSentenceList';

const ProfilePage: React.FC = () => {
  const { isAuthenticated, user } = useAuth();
  const [activeTab, setActiveTab] = useState<'profile' | 'words' | 'sentences'>('profile');

  useEffect(() => {
    document.title = 'マイページ | banananbo.com';
  }, []);

  if (!isAuthenticated) {
    return (
      <div className="container">
        <div className="alert alert-warning">
          ログインしてください
        </div>
      </div>
    );
  }

  const styles = {
    container: {
      maxWidth: '1200px',
      margin: '0 auto',
      padding: '20px',
    },
    pageTitle: {
      marginBottom: '2rem',
      color: '#333',
      textAlign: 'center' as const,
    },
    tabs: {
      display: 'flex' as const,
      borderBottom: '1px solid #ddd',
      marginBottom: '2rem',
    },
    tab: {
      padding: '10px 20px',
      cursor: 'pointer',
      backgroundColor: 'transparent',
      border: 'none',
      borderBottom: '3px solid transparent',
      margin: '0 10px',
      fontWeight: 500 as const,
      fontSize: '16px',
      color: '#666',
    },
    activeTab: {
      color: '#4a6cf7',
      borderBottomColor: '#4a6cf7',
    },
    userInfo: {
      display: 'flex' as const,
      flexDirection: 'column' as const,
      alignItems: 'center',
    },
    avatar: {
      width: '120px',
      height: '120px',
      borderRadius: '50%',
      marginBottom: '20px',
      objectFit: 'cover' as const,
    },
    userName: {
      fontSize: '24px',
      fontWeight: 'bold' as const,
      marginBottom: '10px',
    },
    userDetail: {
      margin: '5px 0',
      color: '#666',
    },
    detailContainer: {
      marginTop: '30px',
      padding: '20px',
      backgroundColor: '#f9f9f9',
      borderRadius: '8px',
      width: '100%',
      maxWidth: '600px',
    },
    detailTitle: {
      fontSize: '18px',
      fontWeight: 'bold' as const,
      marginBottom: '15px',
      borderBottom: '1px solid #eee',
      paddingBottom: '5px',
    },
    detailRow: {
      display: 'flex' as const,
      justifyContent: 'space-between',
      marginBottom: '10px',
    },
    detailLabel: {
      fontWeight: 500 as const,
      color: '#555',
    },
    detailValue: {
      color: '#333',
    },
  };

  const renderTabContent = () => {
    switch(activeTab) {
      case 'profile':
        return (
          <div style={styles.userInfo}>
            {user?.picture && (
              <img 
                src={user.picture} 
                alt={user?.name || 'ユーザー'} 
                style={styles.avatar} 
              />
            )}
            <h2 style={styles.userName}>{user?.name || '名前なし'}</h2>
            <p style={styles.userDetail}>{user?.email || 'メールアドレスなし'}</p>
            
            <div style={styles.detailContainer}>
              <h3 style={styles.detailTitle}>ユーザー情報</h3>
              <div style={styles.detailRow}>
                <span style={styles.detailLabel}>ユーザーID:</span>
                <span style={styles.detailValue}>{user?.id || 'なし'}</span>
              </div>
              <div style={styles.detailRow}>
                <span style={styles.detailLabel}>名前:</span>
                <span style={styles.detailValue}>{user?.name || 'なし'}</span>
              </div>
              <div style={styles.detailRow}>
                <span style={styles.detailLabel}>メールアドレス:</span>
                <span style={styles.detailValue}>{user?.email || 'なし'}</span>
              </div>
            </div>
          </div>
        );
      case 'words':
        return <UserWordList />;
      case 'sentences':
        return <UserSentenceList />;
      default:
        return null;
    }
  };

  return (
    <div style={styles.container}>
      <h1 style={styles.pageTitle}>マイページ</h1>
      
      <div style={styles.tabs}>
        <button 
          style={{...styles.tab, ...(activeTab === 'profile' ? styles.activeTab : {})}} 
          onClick={() => setActiveTab('profile')}
        >
          プロフィール
        </button>
        <button 
          style={{...styles.tab, ...(activeTab === 'words' ? styles.activeTab : {})}} 
          onClick={() => setActiveTab('words')}
        >
          マイ単語リスト
        </button>
        <button 
          style={{...styles.tab, ...(activeTab === 'sentences' ? styles.activeTab : {})}} 
          onClick={() => setActiveTab('sentences')}
        >
          マイセンテンスリスト
        </button>
      </div>
      
      {renderTabContent()}
    </div>
  );
};

export default ProfilePage; 