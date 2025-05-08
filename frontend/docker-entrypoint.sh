#!/bin/sh
set -e

# 環境変数のデフォルト値設定
: "${HOST_FRONTEND:=localhost}"

# 環境変数をnginx設定ファイルに展開
envsubst '${HOST_FRONTEND}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

# 標準のnginxエントリポイントコマンドを実行
exec "$@" 