FROM node:16-alpine as build

WORKDIR /app

COPY package*.json ./

RUN npm install

# 環境変数が注入される前にビルドするために、環境変数をARGとして受け取る
ARG REACT_APP_API_URL
ENV REACT_APP_API_URL=${REACT_APP_API_URL}

COPY . .

RUN npm run build

# 本番環境用のステージ
FROM nginx:alpine

# ビルドされたファイルをNginxのドキュメントルートにコピー
COPY --from=build /app/build /usr/share/nginx/html

# Nginxの設定ファイルテンプレートをコピー
COPY nginx/default.conf.template /etc/nginx/conf.d/default.conf.template

# 起動スクリプトを直接作成
RUN { \
    echo '#!/bin/sh'; \
    echo 'set -e'; \
    echo ''; \
    echo '# 環境変数のデフォルト値設定'; \
    echo ': "${HOST_FRONTEND:=localhost}"'; \
    echo ''; \
    echo '# 環境変数をnginx設定ファイルに展開'; \
    echo 'envsubst '\''${HOST_FRONTEND}'\'' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf'; \
    echo ''; \
    echo '# 標準のnginxエントリポイントコマンドを実行'; \
    echo 'exec "$@"'; \
} > /docker-entrypoint.sh && chmod +x /docker-entrypoint.sh

EXPOSE 80

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"] 