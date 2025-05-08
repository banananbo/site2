#!/bin/bash

# .envファイルを読み込む
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
  echo ".envファイルを読み込みました"
else
  echo "警告: .envファイルが見つかりません"
fi

# 環境変数を本番環境に設定
export APP_ENV=prod

# ドメイン設定を確認
if [ -z "$PROD_DOMAIN" ]; then
  echo "エラー: PROD_DOMAINが設定されていません。.envファイルを確認してください。"
  exit 1
fi

if [ -z "$PROD_EMAIL" ]; then
  echo "エラー: PROD_EMAILが設定されていません。.envファイルを確認してください。"
  exit 1
fi

echo "本番環境（$PROD_DOMAIN）で起動します..."

# イメージをビルド
echo "コンテナイメージをビルドしています..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml build

# サービスを起動
echo "サービスを起動しています..."
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

echo "サービスが起動しました。"
echo "フロントエンド: https://$PROD_DOMAIN"
echo "バックエンド API: https://api.$PROD_DOMAIN"
echo "Redis Commander: https://redis.$PROD_DOMAIN"
echo "Adminer: https://adminer.$PROD_DOMAIN" 