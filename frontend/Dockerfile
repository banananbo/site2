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

# Nginxの設定ファイルをコピー
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

# 起動スクリプトを追加
COPY ./docker-entrypoint.sh /
RUN chmod +x /docker-entrypoint.sh

EXPOSE 80

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"] 