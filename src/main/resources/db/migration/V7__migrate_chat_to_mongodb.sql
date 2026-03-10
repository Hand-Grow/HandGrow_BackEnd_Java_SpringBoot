-- Drop Foreign Key from Electronic Contracts
ALTER TABLE IF EXISTS electronic_contracts DROP CONSTRAINT IF EXISTS electronic_contracts_room_id_fkey;

-- Change room_id column type to VARCHAR to store MongoDB ObjectId strings
ALTER TABLE IF EXISTS electronic_contracts ALTER COLUMN room_id TYPE VARCHAR(255);

-- Drop Chat Messages Table
DROP TABLE IF EXISTS chat_messages;

-- Drop Chat Rooms Table
DROP TABLE IF EXISTS chat_rooms;
