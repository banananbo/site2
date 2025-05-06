-- 外部キー制約を先に削除
ALTER TABLE word_examples DROP FOREIGN KEY FKgjang3nul0sif1udjvpd3re03;

-- word_examplesテーブルからenglish_word_idカラムを削除
ALTER TABLE word_examples DROP COLUMN english_word_id; 