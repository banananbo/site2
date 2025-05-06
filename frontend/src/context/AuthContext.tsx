import React, { createContext, useContext, useState, ReactNode, useEffect } from 'react';

interface TokenInfo {
  idToken: string;
  accessToken: string;
  expiresIn: number;
  tokenType: string;
  name?: string;
}

interface UserProfile {
  id: string;
  name: string;
  email: string;
  picture?: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  authCode: string | null;
  tokenInfo: TokenInfo | null;
  user: UserProfile | null;
  setIsAuthenticated: (isAuthenticated: boolean) => void;
  setAuthCode: (code: string | null) => void;
  setTokenInfo: (tokenInfo: TokenInfo | null) => void;
  setUser: (user: UserProfile | null) => void;
  logout: () => void;
  login: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// LocalStorageからの初期値の取得
const getInitialAuthState = () => {
  const storedAuth = localStorage.getItem('auth');
  if (storedAuth) {
    try {
      return JSON.parse(storedAuth);
    } catch (e) {
      console.error('LocalStorageからの認証情報の取得に失敗しました', e);
    }
  }
  return {
    isAuthenticated: false,
    authCode: null,
    tokenInfo: null,
    user: null
  };
};

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const initialState = getInitialAuthState();
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(initialState.isAuthenticated);
  const [authCode, setAuthCode] = useState<string | null>(initialState.authCode);
  const [tokenInfo, setTokenInfo] = useState<TokenInfo | null>(initialState.tokenInfo);
  const [user, setUser] = useState<UserProfile | null>(initialState.user);

  // 認証状態が変化したらLocalStorageに保存
  useEffect(() => {
    localStorage.setItem('auth', JSON.stringify({
      isAuthenticated,
      authCode,
      tokenInfo,
      user
    }));
  }, [isAuthenticated, authCode, tokenInfo, user]);

  // ユーザー情報の取得
  useEffect(() => {
    const fetchUserProfile = async () => {
      if (!isAuthenticated) return;

      try {
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
      }
    };

    if (isAuthenticated && !user) {
      fetchUserProfile();
    }
  }, [isAuthenticated, user]);

  const login = async () => {
    try {
      // バックエンドからログインURLを取得
      const response = await fetch('/api/auth/login-url');
      const data = await response.json();
      // Auth0のログインページにリダイレクト
      window.location.href = data.authUrl;
    } catch (error) {
      console.error('ログインURLの取得に失敗しました', error);
    }
  };

  const logout = async () => {
    try {
      // バックエンドのセッションをクリア
      await fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include'
      });
      
      // ローカルの認証状態をクリア
      localStorage.removeItem('auth');
      setIsAuthenticated(false);
      setAuthCode(null);
      setTokenInfo(null);
      setUser(null);
      
      // Auth0のログアウトURLを取得してリダイレクト
      const response = await fetch('/api/auth/logout-url');
      const data = await response.json();
      
      if (data.logoutUrl) {
        window.location.href = data.logoutUrl;
      }
    } catch (error) {
      console.error('ログアウトエラー:', error);
    }
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        authCode,
        tokenInfo,
        user,
        setIsAuthenticated,
        setAuthCode,
        setTokenInfo,
        setUser,
        logout,
        login
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}; 