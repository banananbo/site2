#!/bin/bash

# まず.envファイルを読み込む
if [ -f .env ]; then
  set -a  # 自動的にエクスポートを有効化
  source .env
  set +a  # 自動的なエクスポートを無効化
  echo ".envファイルを読み込みました"
else
  echo "警告: .envファイルが見つかりません"
fi

# 環境変数を開発環境に設定
export APP_ENV=dev

echo "開発環境（$HOST_DOMAIN）で起動します..."

# イメージをビルド
echo "コンテナイメージをビルドしています..."
docker-compose -f docker-compose.yml -f docker-compose.dev.yml build

# サービスを起動
echo "サービスを起動しています..."
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

echo "サービスが起動しました。"
echo "フロントエンド: http://$HOST_DOMAIN"
echo "バックエンド API: http://$HOST_BACKEND"
echo "Redis Commander: http://$HOST_REDIS"
echo "Adminer: http://$HOST_ADMINER" 