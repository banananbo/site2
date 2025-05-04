import React, { createContext, useContext, useState, ReactNode, useEffect } from 'react';

interface TokenInfo {
  idToken: string;
  accessToken: string;
  expiresIn: number;
  tokenType: string;
  name?: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  authCode: string | null;
  tokenInfo: TokenInfo | null;
  setIsAuthenticated: (isAuthenticated: boolean) => void;
  setAuthCode: (code: string | null) => void;
  setTokenInfo: (tokenInfo: TokenInfo | null) => void;
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
    tokenInfo: null
  };
};

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const initialState = getInitialAuthState();
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(initialState.isAuthenticated);
  const [authCode, setAuthCode] = useState<string | null>(initialState.authCode);
  const [tokenInfo, setTokenInfo] = useState<TokenInfo | null>(initialState.tokenInfo);

  // 認証状態が変化したらLocalStorageに保存
  useEffect(() => {
    localStorage.setItem('auth', JSON.stringify({
      isAuthenticated,
      authCode,
      tokenInfo
    }));
  }, [isAuthenticated, authCode, tokenInfo]);

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

  const logout = () => {
    setIsAuthenticated(false);
    setAuthCode(null);
    setTokenInfo(null);
    localStorage.removeItem('auth');
  };

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated,
        authCode,
        tokenInfo,
        setIsAuthenticated,
        setAuthCode,
        setTokenInfo,
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