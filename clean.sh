#!/bin/bash

echo "すべてのコンテナ、ネットワーク、およびボリュームを削除しています..."

# すべてのコンテナを停止して削除
docker-compose down -v

# Traefikの設定ファイルをクリア
echo "Traefikの設定をリセットしています..."
rm -rf traefik/acme/acme.json
mkdir -p traefik/acme
touch traefik/acme/acme.json
chmod 600 traefik/acme/acme.json

# 動的設定を削除
echo "動的設定ファイルを削除しています..."
rm -rf traefik/dynamic
mkdir -p traefik/dynamic

# ブラウザキャッシュ対策のヒント
echo "ブラウザのキャッシュをクリアしてから、サイトにアクセスしてください。"
echo "または、シークレットモード/プライベートブラウジングでサイトを開いてください。"

# Docker関連の残りのリソースをクリーンアップ
echo "未使用のDockerリソースをクリーンアップしています..."
docker system prune -f

echo "クリーンアップが完了しました。" 