CREATE DATABASE IF NOT EXISTS appdb;
USE appdb;
-- 初期ユーザーの作成（必要に応じて）
CREATE USER IF NOT EXISTS 'appuser'@'%' IDENTIFIED BY 'apppassword';
GRANT ALL PRIVILEGES ON appdb.* TO 'appuser'@'%';
FLUSH PRIVILEGES;
