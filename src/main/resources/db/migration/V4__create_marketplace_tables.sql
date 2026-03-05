-- Module A: Core Posts Tables

-- 1. Coop Announcements
CREATE TABLE IF NOT EXISTS coop_announcements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coop_id UUID NOT NULL REFERENCES cooperatives(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    attachments JSONB,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 2. Collection Campaigns
CREATE TABLE IF NOT EXISTS collection_campaigns (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coop_id UUID NOT NULL REFERENCES cooperatives(id) ON DELETE CASCADE,
    product_name VARCHAR(255) NOT NULL,
    expected_date DATE NOT NULL,
    status VARCHAR(50) DEFAULT 'GATHERING',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 3. Bulk Sales
CREATE TABLE IF NOT EXISTS bulk_sales (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coop_id UUID NOT NULL REFERENCES cooperatives(id) ON DELETE CASCADE,
    campaign_id UUID REFERENCES collection_campaigns(id),
    product_name VARCHAR(255) NOT NULL,
    total_quantity DECIMAL(15,2) NOT NULL,
    expected_price DECIMAL(15,2),
    status VARCHAR(50) DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Module B: Interactions & Logic Tables

-- 4. Collection Commitments
CREATE TABLE IF NOT EXISTS collection_commitments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id UUID NOT NULL REFERENCES collection_campaigns(id) ON DELETE CASCADE,
    farmer_id UUID NOT NULL REFERENCES farmers(id) ON DELETE CASCADE,
    plot_id UUID NOT NULL REFERENCES plots(id) ON DELETE CASCADE,
    committed_quantity DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT unique_plot_campaign UNIQUE (campaign_id, plot_id)
);

-- 5. Sale Offers
CREATE TABLE IF NOT EXISTS sale_offers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bulk_sale_id UUID NOT NULL REFERENCES bulk_sales(id) ON DELETE CASCADE,
    enterprise_id UUID NOT NULL REFERENCES enterprises(id) ON DELETE CASCADE,
    offered_price DECIMAL(15,2) NOT NULL,
    message TEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 6. Feed Comments
CREATE TABLE IF NOT EXISTS feed_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id UUID NOT NULL REFERENCES farmers(id) ON DELETE CASCADE,
    target_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 7. Feed Likes
CREATE TABLE IF NOT EXISTS feed_likes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id UUID NOT NULL REFERENCES farmers(id) ON DELETE CASCADE,
    target_id UUID NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT unique_user_like UNIQUE(farmer_id, target_id, target_type)
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_announcements_coop ON coop_announcements(coop_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_campaigns_coop ON collection_campaigns(coop_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_campaigns_status ON collection_campaigns(status);
CREATE INDEX IF NOT EXISTS idx_bulk_sales_status ON bulk_sales(status);
CREATE INDEX IF NOT EXISTS idx_bulk_sales_coop ON bulk_sales(coop_id);
CREATE INDEX IF NOT EXISTS idx_commitments_campaign ON collection_commitments(campaign_id);
CREATE INDEX IF NOT EXISTS idx_commitments_farmer ON collection_commitments(farmer_id);
CREATE INDEX IF NOT EXISTS idx_offers_bulk_sale ON sale_offers(bulk_sale_id);
CREATE INDEX IF NOT EXISTS idx_offers_enterprise ON sale_offers(enterprise_id);
CREATE INDEX IF NOT EXISTS idx_feed_comments_target ON feed_comments(target_id, target_type);
CREATE INDEX IF NOT EXISTS idx_feed_likes_target ON feed_likes(target_id, target_type);
