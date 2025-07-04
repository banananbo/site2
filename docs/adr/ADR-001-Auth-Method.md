# ADR-001: Auth0を使用した認証方式の採用

## ステータス

採択 (2025-05-01)

## コンテキスト

ウェブアプリケーションでは、ユーザーの認証と認可が不可欠です。多くの認証ソリューションがあり、それぞれにメリットとデメリットがあります。以下の選択肢を検討しました：

1. **セッションベースの独自認証システム**: 自前でユーザー認証システムを構築
2. **JWTベースの独自認証システム**: 自前でJWTベースの認証を実装
3. **OAuth/OIDCプロバイダ (Auth0)**: 外部サービスに認証を委任
4. **ソーシャルログイン**: Google, Facebook, Twitterなどのプラットフォームに認証を委任

## 決定

Auth0を主要な認証プロバイダとして採用し、OAuth 2.0のAuthorization Codeフローを実装することに決定しました。サーバーサイドセッションと組み合わせて使用します。

## 理由

Auth0を選択した主な理由：

1. **セキュリティ**: 認証の専門家によって開発・維持されている既存のソリューションを活用することで、セキュリティリスクを低減できる
2. **開発速度**: 自前で認証システムを構築する時間と労力を省略可能
3. **柔軟性**: 複数の認証方法（ユーザー名/パスワード、ソーシャルログイン、多要素認証）を簡単に追加可能
4. **スケーラビリティ**: 大規模なユーザーベースにも対応可能
5. **標準準拠**: OAuth 2.0 / OpenID Connectに準拠しており、業界標準のプロトコルを使用
6. **管理機能**: ユーザー管理、パスワードリセット、アカウントリンクなどの機能が組み込まれている

## サーバーサイドセッションを選択した理由：

1. **セキュリティ**: JWTだけに依存するよりもセキュリティが向上する
2. **即時ログアウト**: セッションを無効化することで即時ログアウトが可能
3. **状態管理**: セッションに追加データを保存可能
4. **トークンの更新**: セッション有効期間中にトークンを更新可能

## 影響

この決定により、以下の影響があります：

### 肯定的な影響

1. 強固なセキュリティ標準に依拠したシステムの構築
2. 認証周りの開発工数の削減
3. 将来的な認証機能の拡張が容易
4. セッション管理とJWTの両方のメリットを享受

### 懸念点

1. 外部サービスへの依存性
2. コスト（Auth0の料金体系）
3. ネットワーク遅延の可能性（外部APIへのリクエスト）
4. データベースへのセッション保存による若干のオーバーヘッド

## 代替案

検討した代替案：

1. **独自認証システム**: 完全なカスタマイズが可能だが、セキュリティリスクと開発負担が大きい
2. **Firebase Authentication**: 簡単なセットアップだが、Googleサービスへの依存度が増す
3. **純粋なJWTベースの認証**: スケーラビリティが高いが、トークン無効化の問題がある

## 注意点

- Auth0の無料枠のの制限を考慮（ユーザー数、リクエスト数など）
- バックアッププランとして代替認証方法への移行パスを検討しておく
- セッションデータベーステーブルの定期的なクリーンアップを実装 