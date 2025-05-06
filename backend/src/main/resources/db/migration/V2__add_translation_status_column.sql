-- sentencesテーブルにtranslation_statusカラムを追加
ALTER TABLE sentences ADD COLUMN translation_status VARCHAR(20) NOT NULL DEFAULT 'PENDING';

-- すでに翻訳が完了しているセンテンスのステータスを更新
UPDATE sentences SET translation_status = 'COMPLETED' WHERE translation IS NOT NULL; 