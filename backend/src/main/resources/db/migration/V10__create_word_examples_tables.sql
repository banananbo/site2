-- 単語の例文テーブル
CREATE TABLE IF NOT EXISTS word_examples (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    example TEXT NOT NULL,
    translation TEXT,
    note TEXT,
    source VARCHAR(255)
);

-- 単語と例文の関連テーブル
CREATE TABLE IF NOT EXISTS word_example_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word_id BIGINT NOT NULL,
    example_id BIGINT NOT NULL,
    FOREIGN KEY (word_id) REFERENCES english_words(id) ON DELETE CASCADE,
    FOREIGN KEY (example_id) REFERENCES word_examples(id) ON DELETE CASCADE,
    UNIQUE (word_id, example_id)
); 