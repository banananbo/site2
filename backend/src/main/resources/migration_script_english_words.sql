-- english_wordsテーブルからexampleカラムを削除する前に外部キー制約をチェック
-- 以下のコメントアウトされたSQLを実行して制約を確認できます
/*
SELECT CONSTRAINT_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'english_words' 
  AND COLUMN_NAME = 'example'
  AND CONSTRAINT_NAME != 'PRIMARY';
*/

-- 外部キー制約がある場合は、先に削除する必要があります
-- ALTER TABLE some_table DROP FOREIGN KEY constraint_name;

-- english_wordsテーブルからexampleカラムを削除
ALTER TABLE english_words DROP COLUMN example; 