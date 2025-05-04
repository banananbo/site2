#!/bin/sh

# HTMLファイル内の環境変数のプレースホルダーを実際の値に置換するスクリプト
set -e

# 環境変数から設定を置換
echo "Replacing environment variables in JS files..."
for file in /usr/share/nginx/html/static/js/*.js
do
  if [ -f $file ]; then
    echo "Processing $file..."
    sed -i 's|REACT_APP_API_URL_PLACEHOLDER|'"${REACT_APP_API_URL}"'|g' $file
  fi
done

echo "Starting Nginx..."
exec "$@" 