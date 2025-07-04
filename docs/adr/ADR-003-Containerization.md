# ADR-003: Dockerによるコンテナ化アーキテクチャの採用

## ステータス

採択 (2025-05-03)

## コンテキスト

アプリケーションの開発、テスト、デプロイメントを効率的に行うために、環境の一貫性と再現性が重要です。また、各コンポーネント（フロントエンド、バックエンド、データベース）の分離と連携を適切に管理する必要があります。以下の選択肢を検討しました：

1. **従来のサーバーセットアップ**: 物理/仮想サーバーに直接コンポーネントをインストール
2. **コンテナ化アーキテクチャ**: Dockerなどのコンテナ技術を使用
3. **サーバーレスアーキテクチャ**: クラウドプロバイダのサーバーレスサービスを活用

## 決定

Dockerを使用したコンテナ化アーキテクチャを採用し、Docker Composeでマルチコンテナアプリケーションを管理することに決定しました。

## 理由

Dockerによるコンテナ化を選択した主な理由：

1. **一貫性**: 開発、テスト、本番環境間で同一環境を維持可能
2. **分離**: 各コンポーネントが独立したコンテナで動作し、依存関係の競合を回避
3. **移植性**: さまざまな環境（ローカル開発、CI/CD、本番）で一貫して実行可能
4. **スケーラビリティ**: 個々のコンポーネントを独立してスケールアップ/ダウン可能
5. **リソース効率**: 仮想マシンよりも軽量でリソース効率が高い
6. **バージョン管理**: コンテナイメージをバージョン管理し、簡単にロールバック可能
7. **開発効率**: "自分のマシンでは動く"問題の解消

Docker Composeを選択した理由：

1. **宣言的設定**: YAMLファイルで環境構成を宣言的に定義可能
2. **シンプルなオーケストレーション**: 複数コンテナの連携を簡単に管理
3. **開発ワークフローの改善**: `docker-compose up`一つでアプリ全体を起動可能
4. **サービス間の依存関係管理**: コンテナ間の依存関係と順序を制御可能

## コンテナ設計

アプリケーションは以下のコンテナで構成されます：

1. **frontend**: Nginx+Reactアプリケーション
   - ベースイメージ: node:16-alpine (ビルド), nginx:alpine (実行)
   - 役割: 静的ファイルの配信、APIリクエストのプロキシ

2. **backend**: Spring Bootアプリケーション
   - ベースイメージ: gradle:8.7.0-jdk17 (ビルド), openjdk:17-jdk-slim (実行)
   - 役割: ビジネスロジックの実行、APIエンドポイントの提供

3. **db**: MySQLデータベース
   - イメージ: mysql:8.0
   - 役割: データの永続化、セッション管理

## 影響

この決定により、以下の影響があります：

### 肯定的な影響

1. **開発効率の向上**: 環境構築の簡易化と標準化
2. **CI/CD統合の容易さ**: コンテナ化されたアプリはCI/CDパイプラインと統合しやすい
3. **本番環境への移行の容易さ**: ローカルと同じコンテナが本番で動作
4. **マイクロサービスへの移行パス**: 将来的なマイクロサービスアーキテクチャへの移行が容易

### 懸念点

1. **学習曲線**: チームメンバーがDockerとコンテナの概念に慣れる必要がある
2. **デバッグの複雑さ**: コンテナ内のデバッグが従来の方法より複雑になる可能性
3. **ボリューム管理**: データの永続化とボリューム管理に注意が必要
4. **ホスト間の挙動の違い**: 異なるホストOSにおけるDocker挙動の微妙な違いの可能性

## 実装詳細

実装には以下の要素を含めます：

1. **マルチステージビルド**: 最小限のサイズのコンテナイメージを作成
2. **適切なベースイメージ**: セキュリティと効率性を考慮した最小限のベースイメージ
3. **環境変数**: 設定情報は環境変数として外部化
4. **ヘルスチェック**: コンテナの状態監視のためのヘルスチェック
5. **ボリューム**: データベースとログのための永続ボリューム

## 代替案

検討した代替案：

1. **Kubernetes**: 複雑性が高いため、現時点のプロジェクト規模では過剰
2. **サーバーレス**: 制約が多く、現在のアーキテクチャとの適合性が低い
3. **従来のデプロイメント**: 環境間の一貫性確保が難しく、デプロイが複雑

## 注意点

- セキュリティ対策（最小権限の原則、イメージスキャンなど）を実施
- 本番環境でのパフォーマンスチューニングを検討
- 将来的なオーケストレーションニーズを監視（Kubernetes移行の可能性）
- コンテナログの適切な管理方法の確立 