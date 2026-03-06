-- Fix attachments column type
ALTER TABLE coop_announcements DROP COLUMN IF EXISTS attachments;
ALTER TABLE coop_announcements ADD COLUMN attachments JSONB;
