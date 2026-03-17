-- Add enterprise signature fields to electronic_contracts
ALTER TABLE electronic_contracts
  ADD COLUMN IF NOT EXISTS enterprise_signatory_name VARCHAR(255),
  ADD COLUMN IF NOT EXISTS enterprise_signed BOOLEAN DEFAULT false,
  ADD COLUMN IF NOT EXISTS enterprise_signed_at TIMESTAMP WITHOUT TIME ZONE;
