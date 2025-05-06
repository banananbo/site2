# センテンス登録機能のアーキテクチャ設計

## ステータス

提案中 (2023-XX-XX)

## 背景

英語学習アプリケーションにおいて、ユーザーがセンテンス（文）を登録し、その中に含まれる単語、イディオム、文法を自動的に抽出して学習データとして活用する機能が必要とされています。これにより、コンテキストを持った学習体験を提供し、ユーザーの英語理解を深めることができます。

## 決定事項

センテンス登録機能を実装するにあたり、以下の設計を採用します：

### データモデル

1. **Sentence**（センテンス）エンティティ
   - id: Long (PK)
   - text: String (センテンスのテキスト)
   - translation: String? (翻訳、任意)
   - note: String? (メモ、任意)
   - source: String? (出典、任意)
   - difficulty: DifficultyLevel (難易度)
   - createdAt: LocalDateTime
   - updatedAt: LocalDateTime

2. **Idiom**（イディオム）エンティティ
   - id: Long (PK)
   - phrase: String (イディオム表現)
   - meaning: String? (意味)
   - explanation: String? (詳細説明)
   - createdAt: LocalDateTime
   - updatedAt: LocalDateTime

3. **Grammar**（文法）エンティティ
   - id: Long (PK)
   - pattern: String (文法パターン)
   - explanation: String (説明)
   - level: DifficultyLevel (難易度)
   - createdAt: LocalDateTime
   - updatedAt: LocalDateTime

4. **SentenceWordRelation**（センテンスと単語の関連）エンティティ
   - id: Long (PK)
   - sentenceId: Long (FK)
   - wordId: Long (FK)

5. **SentenceIdiomRelation**（センテンスとイディオムの関連）エンティティ
   - id: Long (PK)
   - sentenceId: Long (FK)
   - idiomId: Long (FK)

6. **SentenceGrammarRelation**（センテンスと文法の関連）エンティティ
   - id: Long (PK)
   - sentenceId: Long (FK)
   - grammarId: Long (FK)

### 難易度レベルの定義

```kotlin
enum class DifficultyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    NATIVE
}
```

### 処理フロー

1. **センテンス登録処理**
   - ユーザーがセンテンスを入力（テキスト、翻訳、メモ、出典、難易度など）
   - システムがセンテンスをデータベースに登録

2. **要素抽出処理**
   - 登録されたセンテンスから単語を抽出
     - すでに登録されている単語は重複登録しない
     - 新規単語は英単語テーブルに登録
     - センテンスと単語の関連をリレーションテーブルに登録
   - センテンスからイディオムを抽出
     - イディオム抽出ロジック（パターンマッチングまたはAI支援）を実行
     - 新規イディオムをイディオムテーブルに登録
     - センテンスとイディオムの関連をリレーションテーブルに登録
   - センテンスから文法パターンを抽出
     - 文法パターン抽出ロジック（パターンマッチングまたはAI支援）を実行
     - 新規文法パターンを文法テーブルに登録
     - センテンスと文法の関連をリレーションテーブルに登録

### 技術的アプローチ

1. **要素抽出のアプローチ**
   - 単語抽出: テキスト分割と形態素解析、またはOpenAI APIを使用
   - イディオム抽出: イディオムデータベースとのパターンマッチング、またはOpenAI APIを使用
   - 文法抽出: 文法パターンデータベースとのパターンマッチング、またはOpenAI APIを使用

2. **処理設計**
   - 非同期処理: センテンス登録後、要素抽出処理はバックグラウンドで実行
   - キャッシュ: 頻繁にアクセスされる単語・イディオム・文法データはRedisにキャッシュ

### API設計

1. **センテンス登録API**
   ```
   POST /api/sentences
   ```
   リクエストボディ:
   ```json
   {
     "text": "The early bird catches the worm.",
     "translation": "早起きは三文の得。",
     "note": "時間厳守の大切さを表すイディオム",
     "source": "英語の諺",
     "difficulty": "INTERMEDIATE"
   }
   ```

2. **センテンス取得API**
   ```
   GET /api/sentences/{id}
   ```
   レスポンス:
   ```json
   {
     "id": 1,
     "text": "The early bird catches the worm.",
     "translation": "早起きは三文の得。",
     "note": "時間厳守の大切さを表すイディオム",
     "source": "英語の諺",
     "difficulty": "INTERMEDIATE",
     "words": [
       { "id": 1, "word": "early", "meaning": "早い" },
       { "id": 2, "word": "bird", "meaning": "鳥" },
       // ...
     ],
     "idioms": [
       { 
         "id": 1, 
         "phrase": "The early bird catches the worm", 
         "meaning": "早起きは三文の得", 
         "explanation": "早く行動を起こす人が利益を得るという教え"
       }
     ],
     "grammars": [
       {
         "id": 1,
         "pattern": "Subject + Verb + Object",
         "explanation": "基本的な英語の文型（SVO）",
         "level": "BEGINNER"
       }
     ]
   }
   ```

## 検討した代替案

1. **要素抽出の代替手段**
   - 完全手動入力: ユーザーが単語、イディオム、文法を手動で指定
   - ルールベースのみの抽出: AIを使わず、パターンマッチングのみで抽出
   - 完全AIベース: すべての抽出をAIに任せる

2. **データモデルの代替案**
   - 単一テーブル設計: すべての関連を1つのJSONフィールドで管理
   - 非正規化設計: 冗長性を持たせた設計で参照効率を向上

## 影響と実装方針

### 影響

1. **データベース**
   - 新規テーブル: Sentence, Idiom, Grammar, および関連テーブルを追加
   - テーブル間のリレーションシップが増加

2. **パフォーマンス**
   - センテンス登録時の処理負荷が増加
   - 複雑なクエリが増加する可能性

3. **ユーザー体験**
   - より豊かな学習リソースを提供
   - コンテキストを持った英語学習が可能に

### 実装方針

1. **フェーズ1: 基本機能実装**
   - センテンスの登録・管理機能
   - 単語抽出と関連付け機能

2. **フェーズ2: 拡張機能実装**
   - イディオム抽出と関連付け機能
   - 文法パターン抽出と関連付け機能

3. **フェーズ3: AI連携と高度化**
   - OpenAI APIを活用した高度な抽出機能
   - 学習推奨機能の追加

## 決定理由

1. この設計は、英語学習における文脈理解の重要性に焦点を当てています。
2. 単語、イディオム、文法を有機的に結びつけることで、総合的な英語力向上を支援します。
3. 非同期処理とキャッシュ戦略により、システム負荷を適切に分散します。
4. 段階的な実装アプローチにより、リスクを最小化しながら機能を拡張できます。 