-- Adds criteria metadata to system assessments so certificate ranking can be scoped
-- by knowledge area and difficulty. Columns are nullable to preserve existing rows.
ALTER TABLE assessments
    ADD COLUMN IF NOT EXISTS knowledge_area VARCHAR(64),
    ADD COLUMN IF NOT EXISTS difficulty_level VARCHAR(32);
