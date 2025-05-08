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

echo "環境を完全にリセットしています..."

# クリーンアップ実行
./clean.sh

# 開発環境を起動
echo "開発環境を再起動しています..."
./start-dev.sh

echo "開発環境のリセットが完了しました。"
echo "重要: ブラウザのキャッシュを完全にクリアするか、シークレットモードで開いてください。"
echo ""
echo "フロントエンド: http://$HOST_DOMAIN"
echo "バックエンド API: http://$HOST_BACKEND"
echo "Redis Commander: http://$HOST_REDIS"
echo "Adminer: http://$HOST_ADMINER" 