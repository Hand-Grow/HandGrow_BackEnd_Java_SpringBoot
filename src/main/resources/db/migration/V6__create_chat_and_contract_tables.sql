-- Create Chat Rooms Table
CREATE TABLE IF NOT EXISTS chat_rooms (
    id UUID PRIMARY KEY,
    bulk_sale_id UUID NOT NULL,
    coop_id UUID NOT NULL,
    enterprise_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bulk_sale_id) REFERENCES bulk_sales(id),
    FOREIGN KEY (coop_id) REFERENCES cooperatives(id),
    FOREIGN KEY (enterprise_id) REFERENCES enterprises(id)
);

-- Create Chat Messages Table
CREATE TABLE IF NOT EXISTS chat_messages (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL,
    sender_id UUID NOT NULL,
    sender_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id)
);

-- Create Electronic Contracts Table
CREATE TABLE IF NOT EXISTS electronic_contracts (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL UNIQUE,
    bulk_sale_id UUID NOT NULL,
    coop_id UUID NOT NULL,
    enterprise_id UUID NOT NULL,
    agreed_price NUMERIC(15, 2) NOT NULL,
    agreed_quantity NUMERIC(15, 2) NOT NULL,
    delivery_date DATE NOT NULL,
    terms TEXT,
    document_url VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (bulk_sale_id) REFERENCES bulk_sales(id),
    FOREIGN KEY (coop_id) REFERENCES cooperatives(id),
    FOREIGN KEY (enterprise_id) REFERENCES enterprises(id)
);

-- Alter collection_campaigns to add new fields
ALTER TABLE collection_campaigns ADD COLUMN IF NOT EXISTS title VARCHAR(255);
ALTER TABLE collection_campaigns ADD COLUMN IF NOT EXISTS content TEXT;
ALTER TABLE collection_campaigns ADD COLUMN IF NOT EXISTS attachments JSONB;
