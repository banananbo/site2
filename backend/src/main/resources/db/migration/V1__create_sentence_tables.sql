-- english_wordsテーブル
CREATE TABLE IF NOT EXISTS english_words (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    word VARCHAR(100) NOT NULL,
    meaning TEXT,
    example TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    translation_status ENUM('PENDING', 'COMPLETED', 'ERROR') DEFAULT 'PENDING',
    UNIQUE KEY (word)
);

-- センテンステーブル
CREATE TABLE sentences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text TEXT NOT NULL,
    translation TEXT,
    note TEXT,
    source VARCHAR(255),
    difficulty VARCHAR(20) NOT NULL DEFAULT 'INTERMEDIATE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- イディオムテーブル
CREATE TABLE idioms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phrase VARCHAR(255) NOT NULL UNIQUE,
    meaning TEXT,
    explanation TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 文法パターンテーブル
CREATE TABLE grammars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pattern VARCHAR(255) NOT NULL UNIQUE,
    explanation TEXT NOT NULL,
    level VARCHAR(20) NOT NULL DEFAULT 'INTERMEDIATE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- センテンスと単語の関連テーブル
CREATE TABLE sentence_word_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sentence_id BIGINT NOT NULL,
    word_id BIGINT NOT NULL,
    FOREIGN KEY (sentence_id) REFERENCES sentences(id) ON DELETE CASCADE,
    FOREIGN KEY (word_id) REFERENCES english_words(id) ON DELETE CASCADE,
    UNIQUE (sentence_id, word_id)
);

-- センテンスとイディオムの関連テーブル
CREATE TABLE sentence_idiom_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sentence_id BIGINT NOT NULL,
    idiom_id BIGINT NOT NULL,
    FOREIGN KEY (sentence_id) REFERENCES sentences(id) ON DELETE CASCADE,
    FOREIGN KEY (idiom_id) REFERENCES idioms(id) ON DELETE CASCADE,
    UNIQUE (sentence_id, idiom_id)
);

-- センテンスと文法パターンの関連テーブル
CREATE TABLE sentence_grammar_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sentence_id BIGINT NOT NULL,
    grammar_id BIGINT NOT NULL,
    FOREIGN KEY (sentence_id) REFERENCES sentences(id) ON DELETE CASCADE,
    FOREIGN KEY (grammar_id) REFERENCES grammars(id) ON DELETE CASCADE,
    UNIQUE (sentence_id, grammar_id)
);

-- messagesテーブル
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL DEFAULT 1
); 