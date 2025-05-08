# プロジェクト概要

英語学習のための単語・例文管理アプリケーション

## 環境構築手順

### 前提条件
- Docker と Docker Compose がインストール済みであること
- Git がインストール済みであること

### 環境変数の設定

1. プロジェクトルートディレクトリとフロントエンドディレクトリに.envファイルを作成します

```bash
# プロジェクトルートの.envファイルを作成
cp .env.example .env

# フロントエンドの.envファイルを作成
cp frontend/.env.example frontend/.env
```

2. 作成した.envファイルを編集して、必要な環境変数を設定します
   - Auth0の認証情報
   - データベース接続情報（必要に応じて）
   - APIキー（必要に応じて）

> 注意: .envファイルはGitリポジトリに含まれていません。これはセキュリティのためです。

### アプリケーションの起動

```bash
# Dockerコンテナを起動
docker compose up -d
```

### 開発環境の使用

```bash
# バックエンドログの確認
docker compose logs -f backend

# フロントエンドログの確認
docker compose logs -f frontend
```

## 機能一覧

- ユーザー認証（Auth0連携）
- 単語登録・管理
- 例文登録・管理
- ユーザーごとの学習進捗管理
- その他... 