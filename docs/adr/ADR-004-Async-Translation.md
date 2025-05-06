# ADR-004: 非同期翻訳処理の実装

## ステータス

承認済み (2025-05-06)

## コンテキスト

英語学習アプリケーションでは、ユーザーが英単語を登録した際に、その単語の意味と例文を自動的に提供する機能が必要です。この機能を実現するために、外部のAI APIを使用して翻訳処理を行う必要があります。

以下の課題が存在します：

1. API呼び出しには時間がかかり、ユーザー体験を低下させる可能性がある
2. APIには利用制限（レート制限）があり、同時に多くのリクエストを処理できない
3. APIコールの失敗に対処する仕組みが必要
4. APIコストを最小限に抑える必要がある

## 決定

非同期処理アーキテクチャを採用し、以下の技術スタックを使用して英単語の翻訳処理を実装することを決定しました：

1. **Redisキャッシュ**: 非同期処理のバックエンドとして使用
2. **Spring Batch**: バッチ処理フレームワークとして使用
3. **OpenAI API (gpt-3.5-turbo-instruct)**: 最も費用対効果の高いモデルを使用して翻訳を実行
4. **リトライメカニズム**: API障害やレート制限に対処

### 実装詳細

#### データモデル
```kotlin
data class EnglishWord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, unique = true)
    val word: String,
    
    @Column
    var meaning: String? = null,
    
    @Column
    var example: String? = null,
    
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Enumerated(EnumType.STRING)
    @Column(name = "translation_status")
    var translationStatus: TranslationStatus = TranslationStatus.PENDING
)

enum class TranslationStatus {
    PENDING, COMPLETED, ERROR
}
```

#### 処理フロー
1. ユーザーが単語を登録すると、ステータスを「PENDING」として保存
2. 2分ごとに実行されるバッチジョブが未処理（PENDING）の単語を検出
3. 各単語に対してOpenAI APIを呼び出し、意味と例文を取得
4. 取得した情報をデータベースに保存し、ステータスを「COMPLETED」に更新
5. エラーが発生した場合は、ステータスを「ERROR」に設定

#### エラー処理と回復戦略
- レート制限エラー（HTTP 429）の発生時にはバックオフ戦略を使用して自動リトライ
- 最大3回のリトライ後も失敗した場合はエラー状態に遷移
- APIリクエスト間に十分な間隔（3秒）を設けてレート制限を回避
- すべての処理は非同期で行い、エラーがあっても他の処理に影響しない構造

## 結果

この決定により、以下のメリットが得られます：

1. **ユーザー体験の向上**: ユーザーは翻訳を待つことなく単語を登録できる
2. **システムの堅牢性**: APIの障害やレート制限に対して回復力がある
3. **コスト効率**: 最も費用対効果の高いモデルを使用し、不要なAPI呼び出しを避ける
4. **スケーラビリティ**: 処理負荷を分散し、大量の単語登録にも対応できる

## デメリット

1. **即時性の欠如**: 翻訳結果がすぐに利用できない
2. **実装の複雑性**: 非同期処理とエラーハンドリングにより、実装が複雑になる
3. **インフラコスト**: Redisのような追加のサービスが必要になる

## 代替案

1. **同期的な処理**: ユーザーの操作時に直接APIを呼び出す方法
   - メリット: 即時に結果が表示される
   - デメリット: ユーザーの待ち時間が長くなる、APIの障害が直接ユーザーに影響する

2. **ローカルの辞書データを使用**: 事前に作成した辞書データを使用する方法
   - メリット: 外部依存がなく高速
   - デメリット: データが限定的、例文が少ない、更新が困難

## 注意点

- APIキーは適切に管理し、環境変数として設定する
- レート制限やコストを監視し、必要に応じて処理間隔やリトライ戦略を調整する
- 将来的にOpenAI APIの代替サービスへの切り替えが容易になるよう、抽象化を適切に行う 