-- Create plots table
CREATE TABLE IF NOT EXISTS plots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    farmer_id UUID NOT NULL REFERENCES farmers(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    area DECIMAL(15, 2),
    area_unit VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(farmer_id, name)
);

-- Create activity_type enum
CREATE TYPE activity_type_enum AS ENUM (
    'WATERING',
    'FERTILIZING',
    'PESTICIDE',
    'PLANTING',
    'HARVESTING',
    'WEEDING'
);

-- Create farming_diaries table
CREATE TABLE IF NOT EXISTS farming_diaries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plot_id UUID NOT NULL REFERENCES plots(id) ON DELETE CASCADE,
    farmer_id UUID NOT NULL REFERENCES farmers(id) ON DELETE CASCADE,
    activity_date DATE NOT NULL,
    activity_type activity_type_enum NOT NULL,
    expense DECIMAL(15, 2) DEFAULT 0,
    ai_extracted_data JSONB,
    audio_file_url VARCHAR(255),
    original_transcript TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Create indexes
CREATE INDEX idx_diary_plot_date ON farming_diaries(plot_id, activity_date DESC);
CREATE INDEX idx_diary_farmer ON farming_diaries(farmer_id);
CREATE INDEX idx_diary_activity_type ON farming_diaries(activity_type);
CREATE INDEX idx_diary_json_data ON farming_diaries USING GIN (ai_extracted_data);
CREATE INDEX idx_plots_farmer ON plots(farmer_id);
