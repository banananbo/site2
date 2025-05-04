# Helloworld React App

シンプルなReactアプリケーションで、バックエンドAPIからHelloworld文言を取得して表示します。

## 機能

- バックエンドAPIからデータを取得して表示
- エラーハンドリング
- シンプルなUI

## 実行方法

### 開発サーバーの起動

```bash
npm start
```

アプリケーションは http://localhost:3000 で実行されます。

### バックエンドAPI

バックエンドAPIは http://localhost:8080/api/hello にデプロイされています。
APIは単純な文字列 "Hello, World!" を返します。

## ビルド

本番環境用にアプリケーションをビルドするには:

```bash
npm run build
```

buildフォルダにビルド済みファイルが生成されます。
