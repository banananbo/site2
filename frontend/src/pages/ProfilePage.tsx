import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import TopBar from '../components/TopBar';
import '../App.css';

interface UserProfile {
  id: string;
  name: string;
  email: string;
  picture?: string;
}

const ProfilePage: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchUserProfile = async () => {
      if (!isAuthenticated) {
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const response = await fetch('/api/user/me', {
          credentials: 'include'
        });

        if (!response.ok) {
          throw new Error(`APIエラー: ${response.status}`);
        }

        const data = await response.json();
        setUser(data);
      } catch (err) {
        console.error('ユーザー情報の取得に失敗しました', err);
        setError('ユーザー情報の読み込み中にエラーが発生しました');
      } finally {
        setLoading(false);
      }
    };

    fetchUserProfile();
  }, [isAuthenticated]);

  return (
    <div>
      <TopBar />
      <div className="content-container">
        <div className="profile-page">
          <h1>プロフィール</h1>
          
          {loading && <p>読み込み中...</p>}
          
          {error && <p className="error-message">{error}</p>}
          
          {!isAuthenticated && !loading && (
            <p>プロフィールを表示するにはログインしてください。</p>
          )}
          
          {user && (
            <div className="profile-card">
              {user.picture && (
                <div className="profile-picture">
                  <img src={user.picture} alt={`${user.name}のプロフィール画像`} />
                </div>
              )}
              
              <div className="profile-details">
                <h2>{user.name}</h2>
                <p><strong>ID:</strong> {user.id}</p>
                <p><strong>メール:</strong> {user.email}</p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProfilePage; 