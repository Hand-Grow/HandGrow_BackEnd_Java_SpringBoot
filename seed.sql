-- ============================================================================
-- SQL SEED DATA - EXPANDED VERSION
-- Project: HandGrow
-- Purpose: Realistic testing data with multiple users and entities
-- Default Password: password123 ($2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.)
-- ============================================================================

-- TRUNCATE existing data for a clean seed (OPTIONAL - UNCOMMENT IF NEEDED)
-- TRUNCATE TABLE farming_diaries, collection_commitments, sale_offers, bulk_sales, electronic_contracts, join_requests, plots, collection_campaigns, farmers, enterprises, cooperatives, accounts, roles CASCADE;

-- 1. ROLES
INSERT INTO roles (id, name, description, is_active, created_at, updated_at) VALUES
('11111111-1111-1111-1111-111111111111', 'ADMIN', 'System Administrator', true, NOW(), NOW()),
('22222222-2222-2222-2222-222222222222', 'COOP', 'Cooperative Manager', true, NOW(), NOW()),
('33333333-3333-3333-3333-333333333333', 'ENTERPRISE', 'Retail/Export Company', true, NOW(), NOW()),
('44444444-4444-4444-4444-444444444444', 'FARMER', 'Individual Farmer', true, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- 2. ACCOUNTS
INSERT INTO accounts (id, username, password_hash, role_id, is_active, created_at, updated_at) VALUES
-- Admin
('a0000000-0000-0000-0000-000000000000', 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '11111111-1111-1111-1111-111111111111', true, NOW(), NOW()),
-- Cooperatives
('a1111111-1111-1111-1111-111111111111', 'coop_soc_trang', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '22222222-2222-2222-2222-222222222222', true, NOW(), NOW()),
('a1111111-2222-2222-2222-222222222222', 'coop_dong_thap', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '22222222-2222-2222-2222-222222222222', true, NOW(), NOW()),
('a1111111-3333-3333-3333-333333333333', 'coop_lam_dong', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '22222222-2222-2222-2222-222222222222', true, NOW(), NOW()),
-- Enterprises
('a2222222-1111-1111-1111-111111111111', 'ent_winmart', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '33333333-3333-3333-3333-333333333333', true, NOW(), NOW()),
('a2222222-2222-2222-2222-222222222222', 'ent_greenfood', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '33333333-3333-3333-3333-333333333333', true, NOW(), NOW()),
('a2222222-3333-3333-3333-333333333333', 'ent_riceexport', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '33333333-3333-3333-3333-333333333333', true, NOW(), NOW()),
-- Farmers (Coop Sóc Trăng)
('a3333333-1111-1111-1111-111111111111', 'farmer_01', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '44444444-4444-4444-4444-444444444444', true, NOW(), NOW()),
('a3333333-1111-1111-1111-111111111112', 'farmer_02', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '44444444-4444-4444-4444-444444444444', true, NOW(), NOW()),
('a3333333-1111-1111-1111-111111111113', 'farmer_03', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '44444444-4444-4444-4444-444444444444', true, NOW(), NOW()),
-- Farmers (Coop Đồng Tháp)
('a3333333-2222-2222-2222-222222222221', 'farmer_04', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '44444444-4444-4444-4444-444444444444', true, NOW(), NOW()),
('a3333333-2222-2222-2222-222222222222', 'farmer_05', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '44444444-4444-4444-4444-444444444444', true, NOW(), NOW()),
-- Farmers (Coop Lâm Đồng)
('a3333333-3333-3333-3333-333333333331', 'farmer_06', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.zpyKy5.', '44444444-4444-4444-4444-444444444444', true, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 3. COOPERATIVES
INSERT INTO cooperatives (id, account_id, name, phone_number, address, commune, province, produce, representative_name, fund_balance, created_at, updated_at) VALUES
('c1111111-1111-1111-1111-111111111111', 'a1111111-1111-1111-1111-111111111111', 'HTX Nông Nghiệp Xanh Sóc Trăng', '0912345678', 'Mỹ Xuyên, Sóc Trăng', 'Mỹ Xuyên', 'Sóc Trăng', 'RICE', 'Nguyễn Văn Hùng', 500000000.00, NOW(), NOW()),
('c1111111-2222-2222-2222-222222222222', 'a1111111-2222-2222-2222-222222222222', 'HTX Sen Hồng Đồng Tháp', '0912345679', 'Cao Lãnh, Đồng Tháp', 'Cao Lãnh', 'Đồng Tháp', 'VEGETABLES', 'Lê Minh Tâm', 350000000.00, NOW(), NOW()),
('c1111111-3333-3333-3333-333333333333', 'a1111111-3333-3333-3333-333333333333', 'HTX Chè Cầu Đất Farm', '0912345680', 'Đà Lạt, Lâm Đồng', 'Đà Lạt', 'Lâm Đồng', 'TEA', 'Phạm Hoàng Nam', 800000000.00, NOW(), NOW())
ON CONFLICT (account_id) DO NOTHING;

-- 4. ENTERPRISES
INSERT INTO enterprises (id, account_id, company_name, phone_number, address, tax_code, name, representative_name, created_at, updated_at) VALUES
('e1111111-1111-1111-1111-111111111111', 'a2222222-1111-1111-1111-111111111111', 'Tập đoàn WinCommerce', '0281234567', 'Quận 1, TP.HCM', '010111222', 'WinMart Vietnam', 'Nguyễn Thị Win', NOW(), NOW()),
('e1111111-2222-2222-2222-222222222222', 'a2222222-2222-2222-2222-222222222222', 'Công ty Thực phẩm Xanh', '0282223334', 'Quận 7, TP.HCM', '010333444', 'GreenFood Co.', 'Trần Văn Thực', NOW(), NOW()),
('e1111111-3333-3333-3333-333333333333', 'a2222222-3333-3333-3333-333333333333', 'VinaRice Export', '0284445556', 'Quận Bình Thạnh, TP.HCM', '010555666', 'VinaRice', 'Lý Đại Phát', NOW(), NOW())
ON CONFLICT (account_id) DO NOTHING;

-- 5. FARMERS
INSERT INTO farmers (id, account_id, cooperative_id, full_name, phone_number, province, produce, created_at, updated_at) VALUES
-- Sóc Trăng
('f1111111-1111-1111-1111-111111111111', 'a3333333-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', 'Nguyễn Văn Tèo', '0901010101', 'Sóc Trăng', 'RICE', NOW(), NOW()),
('f1111111-1111-1111-1111-111111111112', 'a3333333-1111-1111-1111-111111111112', 'c1111111-1111-1111-1111-111111111111', 'Trần Văn Tý', '0901010102', 'Sóc Trăng', 'RICE', NOW(), NOW()),
('f1111111-1111-1111-1111-111111111113', 'a3333333-1111-1111-1111-111111111113', 'c1111111-1111-1111-1111-111111111111', 'Lê Văn Lượm', '0901010103', 'Sóc Trăng', 'RICE', NOW(), NOW()),
-- Đồng Tháp
('f1111111-2222-2222-2222-222222222221', 'a3333333-2222-2222-2222-222222222221', 'c1111111-2222-2222-2222-222222222222', 'Phạm Văn Sen', '0902020201', 'Đồng Tháp', 'VEGETABLES', NOW(), NOW()),
('f1111111-2222-2222-2222-222222222222', 'a3333333-2222-2222-2222-222222222222', 'c1111111-2222-2222-2222-222222222222', 'Ngô Văn Súng', '0902020202', 'Đồng Tháp', 'VEGETABLES', NOW(), NOW()),
-- Lâm Đồng
('f1111111-3333-3333-3333-333333333331', 'a3333333-3333-3333-3333-333333333331', 'c1111111-3333-3333-3333-333333333333', 'Hoàng Văn Trà', '0903030301', 'Lâm Đồng', 'TEA', NOW(), NOW())
ON CONFLICT (account_id) DO NOTHING;

-- 6. PLOTS
INSERT INTO plots (id, farmer_id, name, area, area_unit, created_at, updated_at) VALUES
-- Tèo
('p1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'Cánh đồng Thượng', 2.5, 'ha', NOW(), NOW()),
('p1111111-1111-1111-1111-111111111112', 'f1111111-1111-1111-1111-111111111111', 'Cánh đồng Hạ', 1.8, 'ha', NOW(), NOW()),
-- Tý
('p1111111-1111-1111-1111-111111111121', 'f1111111-1111-1111-1111-111111111112', 'Ruộng Gần Nhà', 3.0, 'ha', NOW(), NOW()),
-- Lượm
('p1111111-1111-1111-1111-111111111131', 'f1111111-1111-1111-1111-111111111113', 'Mảnh Vườn Nhỏ', 0.5, 'ha', NOW(), NOW()),
-- Sen
('p1111111-2222-2222-2222-222222222211', 'f1111111-2222-2222-2222-222222222221', 'Vườn Cà Chua', 1.2, 'ha', NOW(), NOW()),
-- Súng
('p1111111-2222-2222-2222-222222222221', 'f1111111-2222-2222-2222-222222222222', 'Vườn Cải Bẹ', 0.8, 'ha', NOW(), NOW()),
-- Trà
('p1111111-3333-3333-3333-333333333311', 'f1111111-3333-3333-3333-333333333331', 'Đồi Chè Oolong', 5.0, 'ha', NOW(), NOW());

-- 7. COLLECTION CAMPAIGNS
INSERT INTO collection_campaigns (id, coop_id, product_name, title, content, expected_date, status, created_at, updated_at) VALUES
('b1111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', 'Lúa ST25', 'Thu mua Lúa ST25 vụ Đông Xuân', 'HTX cần 1000 tấn gạo ST25 xuất khẩu EU', '2026-05-20', 'GATHERING', NOW(), NOW()),
('b1111111-2222-2222-2222-222222222222', 'c1111111-2222-2222-2222-222222222222', 'Cà Chua VietGAP', 'Chiến dịch Thu mua rau củ sạch', 'Cung ứng cho chuỗi siêu thị WinMart', '2026-04-10', 'GATHERING', NOW(), NOW());

-- 8. COLLECTION COMMITMENTS
INSERT INTO collection_commitments (id, campaign_id, farmer_id, plot_id, committed_quantity, created_at, updated_at) VALUES
-- Sóc Trăng
('m1111111-1111-1111-1111-111111111111', 'b1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111111', 15.0, NOW(), NOW()),
('m1111111-1111-1111-1111-111111111112', 'b1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111112', 'p1111111-1111-1111-1111-111111111121', 20.0, NOW(), NOW()),
-- Đồng Tháp
('m1111111-2222-2222-2222-222222222211', 'b1111111-2222-2222-2222-222222222222', 'f1111111-2222-2222-2222-222222222221', 'p1111111-2222-2222-2222-222222222211', 5.5, NOW(), NOW());

-- 9. BULK SALES
INSERT INTO bulk_sales (id, coop_id, campaign_id, product_name, total_quantity, expected_price, status, created_at, updated_at) VALUES
('s1111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', 'b1111111-1111-1111-1111-111111111111', 'Lúa ST25 Đặc Sản', 500.0, 10500.0, 'OPEN', NOW(), NOW()),
('s1111111-2222-2222-2222-222222222222', 'c1111111-1111-1111-1111-111111111111', NULL, 'Gạo Thơm Jasmine', 200.0, 8000.0, 'NEGOTIATING', NOW(), NOW());

-- 10. SALE OFFERS
INSERT INTO sale_offers (id, bulk_sale_id, enterprise_id, offered_price, message, status, created_at, updated_at) VALUES
('o1111111-1111-1111-1111-111111111111', 's1111111-1111-1111-1111-111111111111', 'e1111111-3333-3333-3333-333333333333', 10200.0, 'Chúng tôi trả giá 10.200đ cho lô hàng lúa ST25.', 'PENDING', NOW(), NOW()),
('o1111111-2222-2222-2222-222222222222', 's1111111-2222-2222-2222-222222222222', 'e1111111-1111-1111-1111-111111111111', 8200.0, 'Giá tốt cho gạo WinMart.', 'ACCEPTED', NOW(), NOW());

-- 11. JOIN REQUESTS
INSERT INTO join_requests (id, farmer_id, cooperative_id, status, response_message, created_at, updated_at) VALUES
('j1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111112', 'c1111111-1111-1111-1111-111111111111', 'APPROVED', 'Đã duyệt hồ sơ', NOW(), NOW()),
('j1111111-2222-2222-2222-222222222222', 'f1111111-3333-3333-3333-333333333331', 'c1111111-3333-3333-3333-333333333333', 'APPROVED', 'Chào mừng đồi chè mới', NOW(), NOW());

-- 12. FARMING DIARIES (Rich History for Farmer Tèo)
INSERT INTO farming_diaries (id, plot_id, farmer_id, activity_date, activity_type, expense, original_transcript, created_at, updated_at) VALUES
-- Tèo - Cánh đồng Thượng
('d0000001-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', '2026-02-01', 'PLANTING', 2000000, 'Bắt đầu xuống giống vụ lúa mới', NOW(), NOW()),
('d0000002-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', '2026-02-15', 'WATERING', 500000, 'Bơm nước mương vào ruộng', NOW(), NOW()),
('d0000003-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', '2026-03-01', 'FERTILIZING', 1500000, 'Bón phân đợt 1 dưỡng cây', NOW(), NOW()),
('d0000004-1111-1111-1111-111111111111', 'p1111111-1111-1111-1111-111111111111', 'f1111111-1111-1111-1111-111111111111', '2026-03-11', 'WEEDING', 300000, 'Làm cỏ thủ công quanh bờ', NOW(), NOW()),
-- Sen - Vườn Cà Chua
('d0000005-2222-2222-2222-222222222222', 'p1111111-2222-2222-2222-222222222211', 'f1111111-2222-2222-2222-222222222221', '2026-03-10', 'PESTICIDE', 800000, 'Xịt thuốc trừ sâu sinh học', NOW(), NOW());

-- 13. ELECTRONIC CONTRACTS
INSERT INTO electronic_contracts (id, room_id, bulk_sale_id, coop_id, enterprise_id, agreed_price, agreed_quantity, delivery_date, status, created_at, updated_at) VALUES
('ec111111-1111-1111-1111-111111111111', 'room_ Jasmine_001', 's1111111-2222-2222-2222-222222222222', 'c1111111-1111-1111-1111-111111111111', 'e1111111-1111-1111-1111-111111111111', 8200.0, 200.0, '2026-04-30', 'COMPLETED', NOW(), NOW());
