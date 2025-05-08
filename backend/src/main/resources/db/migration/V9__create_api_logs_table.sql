-- API呼び出しのログテーブル
CREATE TABLE IF NOT EXISTS  api_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name VARCHAR(255) NOT NULL,
    endpoint VARCHAR(255) NOT NULL,
    request_body VARCHAR(3000) NOT NULL,
    response_body VARCHAR(5000),
    successful BOOLEAN NOT NULL DEFAULT FALSE,
    error_message VARCHAR(1000),
    request_timestamp DATETIME(6) NOT NULL,
    response_timestamp DATETIME(6),
    execution_time_ms BIGINT,
    word_id BIGINT
); 