interface AuthUrlResponse {
  authUrl: string;
}

interface TokenResponse {
  idToken: string;
  accessToken: string;
  expiresIn: number;
  tokenType: string;
}

// APIからAuth0認証URLを取得
export const fetchAuthUrl = async (): Promise<string> => {
  try {
    const response = await fetch('/api/auth/login-url');
    if (!response.ok) {
      throw new Error('認証URLの取得に失敗しました');
    }
    
    const data: AuthUrlResponse = await response.json();
    return data.authUrl;
  } catch (error) {
    console.error('認証URL取得エラー:', error);
    throw error;
  }
};

// Auth0認証URLへリダイレクト
export const redirectToAuth0 = async (): Promise<void> => {
  try {
    const authUrl = await fetchAuthUrl();
    // URLに余分なスペースがある場合の対策
    window.location.href = authUrl.trim();
  } catch (error) {
    console.error('Auth0リダイレクトエラー:', error);
    throw error;
  }
};

// URLからAuth0コードを取得
export const getAuthCodeFromUrl = (): string | null => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('code');
};

// バックエンドAPIを通じてAuth0からトークンを取得
export const exchangeCodeForToken = async (code: string): Promise<TokenResponse> => {
  try {
    const response = await fetch('/api/auth/token', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ code }),
    });

    if (!response.ok) {
      throw new Error('トークンの取得に失敗しました');
    }

    return await response.json();
  } catch (error) {
    console.error('トークン取得エラー:', error);
    throw error;
  }
}; 