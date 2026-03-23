-- Drop the old unique constraints if they exist
-- Assuming the previous constraint might have been named something else, we use IF EXISTS. 
-- In V4, it was named unique_plot_campaign
ALTER TABLE collection_commitments DROP CONSTRAINT IF EXISTS unique_plot_campaign;

-- Also try to drop if there's any implicit constraint named differently
ALTER TABLE collection_commitments DROP CONSTRAINT IF EXISTS collection_commitments_campaign_id_farmer_id_key;

-- Add the correct unique constraint for campaign_id and farmer_id
ALTER TABLE collection_commitments ADD CONSTRAINT unique_campaign_farmer UNIQUE (campaign_id, farmer_id);
