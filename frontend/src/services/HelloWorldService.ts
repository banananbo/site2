interface HelloWorldResponse {
  message: string;
}

// REACT_APP_API_URL_PLACEHOLDERという文字列は、docker-entrypoint.shスクリプトで
// 実際の環境変数の値に置き換えられます
const API_URL = process.env.REACT_APP_API_URL || 'REACT_APP_API_URL_PLACEHOLDER';

export const fetchHelloWorld = async (): Promise<HelloWorldResponse> => {
  try {
    const response = await fetch(API_URL);
    if (!response.ok) {
      throw new Error('APIからのレスポンスが正常ではありません');
    }
    
    // バックエンドは単純な文字列を返すため、JSONではなくテキストとして取得
    const text = await response.text();
    
    // フロントエンド側で整形したオブジェクトを返す
    return { message: text };
  } catch (error) {
    console.error('HelloWorld取得エラー:', error);
    throw error;
  }
}; 