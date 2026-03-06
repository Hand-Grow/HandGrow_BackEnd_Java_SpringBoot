# 🌾 HANDGROW BACKEND - TÀI LIỆU TỔNG HỢP DỰ ÁN

## 📋 MỤC LỤC
1. [Tổng quan hệ thống](#tổng-quan-hệ-thống)
2. [Kiến trúc Database](#kiến-trúc-database)
3. [Authentication & Authorization](#authentication--authorization)
4. [Module 1: User Management](#module-1-user-management)
5. [Module 2: Join Request System](#module-2-join-request-system)
6. [Module 3: Voice Diary](#module-3-voice-diary)
7. [Module 4: Marketplace & Feed](#module-4-marketplace--feed)
8. [API Endpoints Summary](#api-endpoints-summary)
9. [Tech Stack](#tech-stack)

---

## 🎯 TỔNG QUAN HỆ THỐNG

### **Mục đích:**
Nền tảng kết nối **Nông dân - Hợp tác xã (HTX) - Doanh nghiệp** để:
- Quản lý thành viên HTX
- Ghi chép nhật ký canh tác bằng giọng nói (AI)
- Tổ chức thu gom nông sản
- Giao dịch B2B

### **3 Vai trò chính:**
1. **FARMER** (Nông dân) - Ghi nhật ký, tham gia HTX, cam kết sản lượng
2. **COOP** (HTX) - Quản lý thành viên, tổ chức thu gom, bán B2B
3. **ENTERPRISE** (Doanh nghiệp) - Tìm kiếm và mua nông sản số lượng lớn

---

## 🗄️ KIẾN TRÚC DATABASE

### **Core Tables:**

#### **1. Users & Authentication**
```sql
accounts (id, username, password_hash, role_id, is_active)
roles (id, name) -- FARMER, COOP, ENTERPRISE
farmers (id, account_id, full_name, phone_number, cooperative_id, commune, province, produce)
cooperatives (id, account_id, name, phone_number, commune, province, produce)
enterprises (id, account_id, company_name, phone_number, address)
```

#### **2. Join Request System**
```sql
join_requests (
  id, farmer_id, cooperative_id, 
  status, -- PENDING, APPROVED, REJECTED
  response_message, created_at
)
```

#### **3. Voice Diary & Farming**
```sql
plots (id, farmer_id, name, location, area, area_unit)

farming_diaries (
  id, plot_id, farmer_id, 
  activity_date, activity_type, -- FERTILIZING, PESTICIDE, PLANTING, HARVESTING, WATERING, WEEDING
  expense, 
  ai_extracted_data, -- JSONB: {product_name, quantity, unit, unit_price, revenue}
  audio_file_url, original_transcript
)
```

#### **4. Marketplace & Feed**
```sql
-- Thông báo nội bộ HTX
coop_announcements (id, coop_id, title, content, attachments, created_at)

-- Đợt thu gom nông sản
collection_campaigns (id, coop_id, product_name, expected_date, status) -- GATHERING, CLOSED

-- Cam kết sản lượng của nông dân
collection_commitments (id, campaign_id, farmer_id, plot_id, committed_quantity)

-- Bài đăng bán B2B
bulk_sales (id, coop_id, campaign_id, product_name, total_quantity, expected_price, status) -- OPEN, SOLD

-- Báo giá từ doanh nghiệp
sale_offers (id, bulk_sale_id, enterprise_id, offered_price, message, status) -- PENDING, ACCEPTED, REJECTED

-- Tương tác Feed
feed_likes (id, farmer_id, target_id, target_type) -- ANNOUNCEMENT, CAMPAIGN
feed_comments (id, farmer_id, target_id, target_type, content)
```

---

## 🔐 AUTHENTICATION & AUTHORIZATION

### **Flow đăng ký & đăng nhập:**

#### **1. Đăng ký (3 loại user):**
```
POST /api/v1/auth/register/farmer
Body: {username (email), password, fullName, phoneNumber, produce}
→ Tạo Account + Farmer → Trả về JWT token

POST /api/v1/auth/register/cooperative
Body: {username, password, name, phoneNumber, commune, province, produce}
→ Tạo Account + Cooperative → Trả về JWT token

POST /api/v1/auth/register/enterprise
Body: {username, password, companyName, phoneNumber, address}
→ Tạo Account + Enterprise → Trả về JWT token
```

#### **2. Đăng nhập:**
```
POST /api/v1/auth/login
Body: {username, password}
→ Validate → Generate JWT (chứa username + role) → Trả về token
```

#### **3. JWT Token Structure:**
```json
{
  "sub": "farmer@email.com",
  "role": "FARMER",
  "iat": 1234567890,
  "exp": 1234654290
}
```

#### **4. Authorization:**
- JWT Filter kiểm tra token trong header: `Authorization: Bearer {token}`
- Extract username → Load UserDetails → Set Authentication
- SecurityConfig: Tất cả endpoints cần authenticated (trừ /auth/**, /swagger-ui/**)

---

## 👤 MODULE 1: USER MANAGEMENT

### **1.1. Lấy Profile:**
```
GET /api/v1/user/profile
Authorization: Bearer {token}

Response (Farmer):
{
  "id": "uuid",
  "fullName": "Nguyễn Văn A",
  "username": "farmer@email.com",
  "role": "FARMER",
  "cooperativeId": "uuid",  // null nếu chưa join HTX
  "cooperativeName": "HTX ABC",
  "commune": "Xã A",
  "province": "Tỉnh B",
  "produce": "RICE"
}
```

### **1.2. Update Location (Farmer only):**
```
PUT /api/v1/user/location
Body: {commune: "Xã B", province: "Tỉnh C"}
→ Update farmer location
```

---

## 🤝 MODULE 2: JOIN REQUEST SYSTEM

### **Flow: Nông dân xin gia nhập HTX**

#### **Step 1: Farmer tìm HTX**
```
GET /api/v1/cooperatives/search?commune=XãA&province=TỉnhB&produce=RICE
→ Trả về danh sách HTX phù hợp
```

#### **Step 2: Farmer gửi yêu cầu**
```
POST /api/v1/join-requests
Body: {cooperativeId: "uuid"}
→ Tạo JoinRequest với status=PENDING
```

#### **Step 3: HTX xem danh sách yêu cầu**
```
GET /api/v1/join-requests/cooperative?status=PENDING
→ Danh sách farmer đang chờ duyệt
```

#### **Step 4: HTX duyệt/từ chối**
```
PUT /api/v1/join-requests/{id}/respond
Body: {action: "APPROVE", responseMessage: "Chào mừng bạn!"}
→ Update status=APPROVED + Set farmer.cooperative_id

PUT /api/v1/join-requests/{id}/respond
Body: {action: "REJECT", responseMessage: "Không đủ điều kiện"}
→ Update status=REJECTED
```

#### **Step 5: Farmer xem kết quả**
```
GET /api/v1/join-requests/farmer?status=APPROVED
→ Xem các yêu cầu đã được duyệt
```

---

## 🎙️ MODULE 3: VOICE DIARY

### **Flow: Ghi nhật ký canh tác bằng giọng nói**

#### **Step 1: Frontend upload audio lên CDN**
```javascript
// Frontend xử lý
const file = event.target.files[0]; // MP3 file
const formData = new FormData();
formData.append('audio', file);

// Upload lên ImgBB/Cloudinary (không gửi file đến Backend)
const response = await uploadToCDN(formData);
const audioUrl = response.data.url;
```

#### **Step 2: Gửi audio URL đến Backend**
```
POST /api/v1/voice-diary/upload
Body: multipart/form-data (audio file)

Backend:
1. Convert audio → Base64
2. Gửi đến Gemini AI API với prompt:
   "Nghe audio và trích xuất JSON theo activity_type:
    FERTILIZING: {product_name, quantity, unit, unit_price, expense}
    HARVESTING: {product, quantity, unit_price, revenue, buyer}
    ..."
3. Gemini trả về JSON
4. Parse và trả về cho Frontend

Response:
{
  "status": "ok",
  "message": "Đã xử lý file ghi âm thành công",
  "transcription": "{\"activity_type\": \"FERTILIZING\", \"plot_name\": \"Ruộng A\", ...}"
}
```

#### **Step 3: Frontend parse và tạo diary**
```
POST /api/v1/voice-diary
Body: {
  plotName: "Ruộng A",
  activityDate: "2024-01-15",
  activityType: "FERTILIZING",
  expense: 750000,
  aiExtractedData: "{...}",
  originalTranscript: "Hôm nay tôi bón 50kg đạm..."
}
→ Lưu vào farming_diaries
```

#### **Step 4: Xem lịch sử nhật ký**
```
GET /api/v1/voice-diary/plot/{plotId}?startDate=2024-01-01&endDate=2024-12-31
→ Danh sách nhật ký theo ruộng
```

#### **Step 5: Tính lợi nhuận**
```
GET /api/v1/voice-diary/plot/{plotId}/profit?startDate=2024-01-01&endDate=2024-12-31

Response:
{
  "totalRevenue": 10000000,  // SUM(revenue từ HARVESTING)
  "totalExpense": 3500000,   // SUM(expense từ tất cả activities)
  "profit": 6500000,
  "expenseBreakdown": {
    "FERTILIZING": 1500000,
    "PESTICIDE": 800000,
    "PLANTING": 1200000
  }
}
```

---

## 🏪 MODULE 4: MARKETPLACE & FEED

### **4.1. NEWSFEED (Nông dân xem thông báo & đợt thu gom)**

#### **Flow:**
```
1. HTX tạo Announcement:
POST /api/v1/coops/{coopId}/announcements
Body: {
  title: "Thông báo họp",
  content: "Ngày mai 8h họp",
  attachments: ["https://cdn.com/image1.jpg", "https://cdn.com/image2.jpg"]
}

2. HTX tạo Campaign:
POST /api/v1/coops/{coopId}/campaigns
Body: {productName: "Lúa ST25", expectedDate: "2024-12-31"}

3. Farmer xem Feed (Mix Announcements + Campaigns):
GET /api/v1/coops/{coopId}/feed?page=0&size=10

Response:
[
  {
    "id": "uuid",
    "type": "ANNOUNCEMENT",
    "title": "Thông báo họp",
    "content": "Ngày mai 8h...",
    "likeCount": 5,
    "commentCount": 3,
    "isLiked": false,
    "createdAt": "2024-01-15T10:30:00"
  },
  {
    "id": "uuid",
    "type": "CAMPAIGN",
    "title": "Lúa ST25",
    "content": "Ngày dự kiến: 2024-12-31",
    "likeCount": 10,
    "commentCount": 2,
    "isLiked": true,
    "createdAt": "2024-01-14T09:00:00"
  }
]

4. Farmer thả tim:
POST /api/v1/feed/announcement/{id}/likes
→ Toggle like (like/unlike)

5. Farmer comment:
POST /api/v1/feed/campaign/{id}/comments
Body: {content: "Tôi sẽ tham gia!"}

6. Xem comments:
GET /api/v1/feed/announcement/{id}/comments?page=0&size=20
```

### **4.2. COLLECTION CAMPAIGN (Thu gom nông sản)**

#### **Flow:**
```
1. Farmer cam kết sản lượng:
POST /api/v1/campaigns/{campaignId}/commitments
Body: {plotId: "uuid", committedQuantity: 500}
→ Lưu vào collection_commitments

2. HTX xem danh sách cam kết:
GET /api/v1/campaigns/{campaignId}/commitments?page=0&size=20

Response:
[
  {
    "id": "uuid",
    "farmerName": "Nguyễn Văn A",
    "plotName": "Ruộng A",
    "quantity": 500,
    "createdAt": "2024-01-15T10:00:00"
  }
]

3. HTX đóng đợt gom & tạo bài B2B (MAGIC BUTTON):
POST /api/v1/campaigns/{campaignId}/publish-to-b2b

Backend tự động:
- Set campaign.status = CLOSED
- SUM(committed_quantity) từ tất cả commitments
- Tạo BulkSale mới với total_quantity = SUM
- Set bulk_sale.status = OPEN

4. HTX bổ sung giá cho bài B2B:
PUT /api/v1/bulk-sales/{id}
Body: {expectedPrice: 18000}
```

### **4.3. B2B MARKETPLACE (Doanh nghiệp mua hàng)**

#### **Flow:**
```
1. Enterprise tìm lô hàng:
GET /api/v1/marketplace/bulk-sales?page=0&size=10

Response:
[
  {
    "id": "uuid",
    "productName": "Lúa ST25",
    "totalQuantity": 5000,
    "expectedPrice": 18000,
    "status": "OPEN",
    "coopName": "HTX ABC",
    "createdAt": "2024-01-15T10:00:00"
  }
]

2. Enterprise xem chi tiết:
GET /api/v1/marketplace/bulk-sales/{id}

3. Enterprise gửi báo giá:
POST /api/v1/marketplace/bulk-sales/{id}/offers
Body: {offeredPrice: 19000, message: "Chúng tôi muốn mua toàn bộ"}
→ Tạo SaleOffer với status=PENDING

4. HTX xem các báo giá:
GET /api/v1/bulk-sales/{id}/offers

Response:
[
  {
    "id": "uuid",
    "enterpriseName": "Công ty XYZ",
    "offeredPrice": 19000,
    "message": "Chúng tôi muốn mua...",
    "status": "PENDING",
    "createdAt": "2024-01-15T11:00:00"
  }
]

5. HTX chấp nhận 1 báo giá:
PUT /api/v1/offers/{offerId}/accept

Backend tự động:
- Set offer.status = ACCEPTED
- Set bulk_sale.status = SOLD
- Set tất cả offers khác = REJECTED

6. Enterprise xem lịch sử báo giá:
GET /api/v1/enterprises/me/offers?status=ACCEPTED
→ Xem các deal đã trúng
```

---

## 📡 API ENDPOINTS CHI TIẾT

### **1. AUTHENTICATION**

#### **1.1. Đăng ký Farmer**
```http
POST /api/v1/auth/register/farmer
Content-Type: application/json

Request:
{
  "username": "farmer@email.com",
  "password": "password123",
  "fullName": "Nguyễn Văn A",
  "phoneNumber": "0901234567",
  "produce": "RICE"
}

Response (200):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "FARMER"
}
```

#### **1.2. Đăng ký Cooperative**
```http
POST /api/v1/auth/register/cooperative
Content-Type: application/json

Request:
{
  "username": "coop@email.com",
  "password": "password123",
  "name": "HTX Nông Nghiệp ABC",
  "phoneNumber": "0901234567",
  "commune": "Xã A",
  "province": "Tỉnh B",
  "produce": "RICE"
}

Response (200):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "COOP"
}
```

#### **1.3. Đăng ký Enterprise**
```http
POST /api/v1/auth/register/enterprise
Content-Type: application/json

Request:
{
  "username": "enterprise@email.com",
  "password": "password123",
  "companyName": "Công ty TNHH XYZ",
  "phoneNumber": "0901234567",
  "address": "123 Đường ABC, TP.HCM"
}

Response (200):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "ENTERPRISE"
}
```

#### **1.4. Đăng nhập**
```http
POST /api/v1/auth/login
Content-Type: application/json

Request:
{
  "username": "farmer@email.com",
  "password": "password123"
}

Response (200):
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "role": "FARMER"
}

Error (401):
{
  "error": "Unauthorized",
  "message": "Invalid credentials"
}
```

---

### **2. USER MANAGEMENT**

#### **2.1. Lấy Profile**
```http
GET /api/v1/user/profile
Authorization: Bearer {token}

Response (200) - Farmer:
{
  "id": "uuid",
  "fullName": "Nguyễn Văn A",
  "username": "farmer@email.com",
  "phoneNumber": "0901234567",
  "role": "FARMER",
  "avatarUrl": null,
  "address": "123 Đường ABC",
  "commune": "Xã A",
  "province": "Tỉnh B",
  "produce": "RICE",
  "cooperativeId": "uuid",
  "cooperativeName": "HTX ABC"
}

Response (200) - Coop:
{
  "id": "uuid",
  "fullName": "HTX Nông Nghiệp ABC",
  "username": "coop@email.com",
  "phoneNumber": "0901234567",
  "role": "COOP",
  "commune": "Xã A",
  "province": "Tỉnh B",
  "produce": "RICE"
}
```

#### **2.2. Update Location (Farmer only)**
```http
PUT /api/v1/user/location
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "commune": "Xã B",
  "province": "Tỉnh C"
}

Response (200):
{
  "success": true,
  "message": "Location updated successfully"
}
```

---

### **3. COOPERATIVE SEARCH**

#### **3.1. Tìm kiếm HTX**
```http
GET /api/v1/cooperatives/search?commune=XãA&province=TỉnhB&produce=RICE
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "name": "HTX Nông Nghiệp ABC",
    "phoneNumber": "0901234567",
    "commune": "Xã A",
    "province": "Tỉnh B",
    "produce": "RICE"
  }
]
```

#### **3.2. Lấy tất cả HTX**
```http
GET /api/v1/cooperatives
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "name": "HTX ABC",
    "phoneNumber": "0901234567",
    "commune": "Xã A",
    "province": "Tỉnh B",
    "produce": "RICE"
  }
]
```

---

### **4. JOIN REQUESTS**

#### **4.1. Tạo yêu cầu gia nhập (Farmer)**
```http
POST /api/v1/join-requests
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "cooperativeId": "uuid"
}

Response (200):
{
  "success": true,
  "message": "Join request sent successfully"
}
```

#### **4.2. Xem yêu cầu của HTX (Coop)**
```http
GET /api/v1/join-requests/cooperative?status=PENDING
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "farmerName": "Nguyễn Văn A",
    "farmerPhone": "0901234567",
    "commune": "Xã A",
    "province": "Tỉnh B",
    "produce": "RICE",
    "status": "PENDING",
    "createdAt": "2024-01-15T10:00:00"
  }
]
```

#### **4.3. Xem yêu cầu của Farmer**
```http
GET /api/v1/join-requests/farmer?status=APPROVED
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "cooperativeName": "HTX ABC",
    "status": "APPROVED",
    "responseMessage": "Chào mừng bạn!",
    "createdAt": "2024-01-15T10:00:00"
  }
]
```

#### **4.4. Duyệt/Từ chối yêu cầu (Coop)**
```http
PUT /api/v1/join-requests/{id}/respond
Authorization: Bearer {token}
Content-Type: application/json

Request (Approve):
{
  "action": "APPROVE",
  "responseMessage": "Chào mừng bạn gia nhập HTX!"
}

Request (Reject):
{
  "action": "REJECT",
  "responseMessage": "Xin lỗi, bạn chưa đủ điều kiện"
}

Response (200):
{
  "success": true,
  "message": "Join request approved successfully"
}
```

#### **4.5. Xem tất cả yêu cầu (Admin)**
```http
GET /api/v1/join-requests/all
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "farmerName": "Nguyễn Văn A",
    "cooperativeName": "HTX ABC",
    "status": "PENDING",
    "createdAt": "2024-01-15T10:00:00"
  }
]
```

---

### **5. VOICE DIARY**

#### **5.1. Upload audio & AI processing**
```http
POST /api/v1/voice-diary/upload
Authorization: Bearer {token}
Content-Type: multipart/form-data

Request:
audio: [MP3 file]

Response (200):
{
  "status": "ok",
  "message": "Đã xử lý file ghi âm thành công",
  "transcription": "{\"activity_type\": \"FERTILIZING\", \"plot_name\": \"Ruộng A\", \"product_name\": \"Đạm\", \"quantity\": 50, \"unit\": \"KG\", \"expense\": 750000}"
}

Error (500):
{
  "status": "error",
  "message": "Lỗi xử lý file: ..."
}
```

#### **5.2. Tạo nhật ký**
```http
POST /api/v1/voice-diary
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "plotName": "Ruộng A",
  "activityDate": "2024-01-15",
  "activityType": "FERTILIZING",
  "expense": 750000,
  "aiExtractedData": "{\"product_name\": \"Đạm\", \"quantity\": 50}",
  "originalTranscript": "Hôm nay tôi bón 50kg đạm..."
}

Response (200):
{
  "id": "uuid",
  "plotName": "Ruộng A",
  "activityDate": "2024-01-15",
  "activityType": "FERTILIZING",
  "expense": 750000,
  "aiExtractedData": "{...}",
  "originalTranscript": "Hôm nay..."
}
```

#### **5.3. Xem lịch sử theo ruộng**
```http
GET /api/v1/voice-diary/plot/{plotId}?startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "plotName": "Ruộng A",
    "activityDate": "2024-01-15",
    "activityType": "FERTILIZING",
    "expense": 750000,
    "aiExtractedData": "{...}",
    "createdAt": "2024-01-15T10:00:00"
  }
]
```

#### **5.4. Xem chi tiết nhật ký**
```http
GET /api/v1/voice-diary/{diaryId}
Authorization: Bearer {token}

Response (200):
{
  "id": "uuid",
  "plotName": "Ruộng A",
  "activityDate": "2024-01-15",
  "activityType": "FERTILIZING",
  "expense": 750000,
  "aiExtractedData": "{...}",
  "originalTranscript": "Hôm nay..."
}
```

#### **5.5. Sửa nhật ký**
```http
PUT /api/v1/voice-diary/{diaryId}
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "plotName": "Ruộng A",
  "activityDate": "2024-01-15",
  "activityType": "FERTILIZING",
  "expense": 800000,
  "aiExtractedData": "{...}",
  "originalTranscript": "..."
}

Response (200):
{
  "id": "uuid",
  "plotName": "Ruộng A",
  "expense": 800000,
  ...
}
```

#### **5.6. Xóa nhật ký**
```http
DELETE /api/v1/voice-diary/{diaryId}
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "message": "Đã xóa nhật ký"
}
```

#### **5.7. Tính lợi nhuận**
```http
GET /api/v1/voice-diary/plot/{plotId}/profit?startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer {token}

Response (200):
{
  "totalRevenue": 10000000,
  "totalExpense": 3500000,
  "profit": 6500000,
  "expenseBreakdown": {
    "FERTILIZING": 1500000,
    "PESTICIDE": 800000,
    "PLANTING": 1200000,
    "HARVESTING": 0,
    "WATERING": 0,
    "WEEDING": 0
  }
}
```

---

### **6. FEED (FARMER)**

#### **6.1. Lấy Newsfeed**
```http
GET /api/v1/coops/{coopId}/feed?page=0&size=10
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "type": "ANNOUNCEMENT",
    "title": "Thông báo họp HTX",
    "content": "Ngày mai 8h sáng họp tại văn phòng",
    "likeCount": 5,
    "commentCount": 3,
    "isLiked": false,
    "createdAt": "2024-01-15T10:30:00"
  },
  {
    "id": "uuid",
    "type": "CAMPAIGN",
    "title": "Lúa ST25",
    "content": "Ngày dự kiến: 2024-12-31",
    "likeCount": 10,
    "commentCount": 2,
    "isLiked": true,
    "createdAt": "2024-01-14T09:00:00"
  }
]
```

#### **6.2. Thả tim**
```http
POST /api/v1/feed/{type}/{id}/likes
Authorization: Bearer {token}

type: announcement hoặc campaign
id: UUID của announcement/campaign

Response (200):
{
  "message": "Đã thả tim",
  "success": true
}
```

#### **6.3. Xem comments**
```http
GET /api/v1/feed/{type}/{id}/comments?page=0&size=20
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "farmerName": "Nguyễn Văn A",
    "content": "Tôi sẽ tham gia!",
    "createdAt": "2024-01-15T11:00:00"
  }
]
```

#### **6.4. Viết comment**
```http
POST /api/v1/feed/{type}/{id}/comments
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "content": "Tôi sẽ tham gia đợt thu gom này!"
}

Response (200):
{
  "id": "uuid",
  "farmerName": "Nguyễn Văn A",
  "content": "Tôi sẽ tham gia...",
  "createdAt": "2024-01-15T11:00:00"
}
```

---

### **7. CAMPAIGN MANAGEMENT (COOP ADMIN)**

#### **7.1. Tạo Announcement**
```http
POST /api/v1/coops/{coopId}/announcements
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "title": "Thông báo họp HTX",
  "content": "Ngày mai 8h sáng họp tại văn phòng HTX",
  "attachments": [
    "https://picsum.photos/400/300",
    "https://picsum.photos/400/301"
  ]
}

Response (200):
{
  "success": true,
  "message": "Đã tạo thông báo"
}
```

#### **7.2. Tạo Campaign**
```http
POST /api/v1/coops/{coopId}/campaigns
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "productName": "Lúa ST25",
  "expectedDate": "2024-12-31"
}

Response (200):
{
  "success": true,
  "message": "Đã tạo đợt thu gom"
}
```

#### **7.3. Farmer cam kết sản lượng**
```http
POST /api/v1/campaigns/{id}/commitments
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "plotId": "uuid",
  "committedQuantity": 500
}

Response (200):
{
  "success": true,
  "message": "Đã cam kết sản lượng"
}
```

#### **7.4. Xem danh sách cam kết**
```http
GET /api/v1/campaigns/{id}/commitments?page=0&size=20
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "farmerName": "Nguyễn Văn A",
    "plotName": "Ruộng A",
    "quantity": 500,
    "createdAt": "2024-01-15T10:00:00"
  }
]
```

#### **7.5. Publish to B2B (Magic Button)**
```http
POST /api/v1/campaigns/{id}/publish-to-b2b
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "message": "Đã đóng đợt gom và tạo bài B2B"
}

Backend tự động:
- Đóng campaign (status = CLOSED)
- Tính tổng SUM(committed_quantity)
- Tạo BulkSale mới với total_quantity
```

#### **7.6. Update giá BulkSale**
```http
PUT /api/v1/bulk-sales/{id}
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "expectedPrice": 18000
}

Response (200):
{
  "success": true,
  "message": "Đã cập nhật giá"
}
```

#### **7.7. Xem báo giá**
```http
GET /api/v1/bulk-sales/{id}/offers
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "enterpriseName": "Công ty XYZ",
    "offeredPrice": 19000,
    "message": "Chúng tôi muốn mua toàn bộ",
    "status": "PENDING",
    "createdAt": "2024-01-15T11:00:00"
  }
]
```

#### **7.8. Chấp nhận báo giá**
```http
PUT /api/v1/offers/{offerId}/accept
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "message": "Đã chấp nhận báo giá"
}

Backend tự động:
- Set offer.status = ACCEPTED
- Set bulk_sale.status = SOLD
- Reject tất cả offers khác
```

#### **7.9. Từ chối báo giá**
```http
PUT /api/v1/offers/{offerId}/reject
Authorization: Bearer {token}

Response (200):
{
  "success": true,
  "message": "Đã từ chối báo giá"
}
```

---

### **8. MARKETPLACE (ENTERPRISE)**

#### **8.1. Tìm lô hàng**
```http
GET /api/v1/marketplace/bulk-sales?page=0&size=10
Authorization: Bearer {token}

Response (200):
[
  {
    "id": "uuid",
    "productName": "Lúa ST25",
    "totalQuantity": 5000,
    "expectedPrice": 18000,
    "status": "OPEN",
    "coopName": "HTX Nông Nghiệp ABC",
    "createdAt": "2024-01-15T10:00:00"
  }
]
```

#### **8.2. Xem chi tiết lô hàng**
```http
GET /api/v1/marketplace/bulk-sales/{id}
Authorization: Bearer {token}

Response (200):
{
  "id": "uuid",
  "productName": "Lúa ST25",
  "totalQuantity": 5000,
  "expectedPrice": 18000,
  "status": "OPEN",
  "coopName": "HTX Nông Nghiệp ABC",
  "createdAt": "2024-01-15T10:00:00"
}
```

#### **8.3. Gửi báo giá**
```http
POST /api/v1/marketplace/bulk-sales/{id}/offers
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "offeredPrice": 19000,
  "message": "Chúng tôi muốn mua toàn bộ lô hàng này"
}

Response (200):
{
  "success": true,
  "message": "Đã gửi báo giá"
}
```

#### **8.4. Xem lịch sử báo giá**
```http
GET /api/v1/enterprises/me/offers?status=PENDING&page=0&size=10
Authorization: Bearer {token}

status: PENDING, ACCEPTED, REJECTED

Response (200):
[
  {
    "id": "uuid",
    "enterpriseName": "Công ty XYZ",
    "offeredPrice": 19000,
    "message": "Chúng tôi muốn mua...",
    "status": "ACCEPTED",
    "createdAt": "2024-01-15T11:00:00"
  }
]
```

---

## 🛠️ TECH STACK

### **Backend:**
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Hibernate)
- **PostgreSQL** (Database)
- **Lombok** (Reduce boilerplate)
- **Gradle** (Build tool)

### **External Services:**
- **Gemini AI API** - Speech-to-text & data extraction
- **CDN** (ImgBB/Cloudinary) - Image/audio storage

### **Deployment:**
- **Render.com** - Backend hosting
- **Supabase** - PostgreSQL database

### **Documentation:**
- **Swagger/OpenAPI 3.0** - API documentation

---

## 📝 LƯU Ý QUAN TRỌNG

### **1. JWT Token:**
- Expiration: 24 hours (86400000ms)
- Chứa: username + role
- Cần login lại khi hết hạn

### **2. File Upload:**
- **Frontend** upload ảnh/audio lên CDN
- **Backend** chỉ nhận URLs (không nhận file trực tiếp)
- Attachments lưu dạng `List<String>` (JSON array URLs)

### **3. Pageable:**
- Dùng query params: `?page=0&size=10&sort=createdAt,desc`
- KHÔNG dùng JSON body cho GET requests

### **4. JSONB Fields:**
- `ai_extracted_data` - Lưu data linh hoạt từ Gemini AI
- `attachments` - Lưu array URLs
- Dùng `StringListConverter` để auto convert

### **5. Lazy Loading:**
- Tất cả relationships dùng `FetchType.LAZY`
- Service methods có `@Transactional` để tránh LazyInitializationException

---

## 🚀 DEPLOYMENT

### **Environment Variables (.env):**
```bash
DB_URL=jdbc:postgresql://host:port/database
DB_USERNAME=postgres
DB_PASSWORD=password
JWT_SECRET_KEY=your-secret-key
GEMINI_API_KEY=your-gemini-key
```

### **Database Migration:**
- Hibernate `ddl-auto: update` (auto-create tables)
- Hoặc run manual SQL migrations

### **API Base URL:**
- Production: `https://handgrow-backend-java-springboot-1.onrender.com`
- Swagger UI: `https://handgrow-backend-java-springboot-1.onrender.com/swagger-ui.html`

---

**Tài liệu này tổng hợp toàn bộ flow và logic của dự án HandGrow Backend đến thời điểm hiện tại.** 🌾
