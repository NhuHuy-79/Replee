# 🛠️ Replee Project Refactor Roadmap

Tài liệu này theo dõi quá trình refactor dài hạn để quản lý sự phát triển của dự án, chuẩn hóa quy
ước và tối ưu hóa kiến trúc.

## 1. 🏗️ Kiến trúc & Module hóa

- [ ] **Trích xuất `:core:model`**: Di chuyển tất cả Domain Models (Conversation, Message,
  Account...) vào một module thuần Kotlin để tránh phụ thuộc vòng.
- [ ] **Tạo `:core:sync`**: Cô lập toàn bộ logic đồng bộ, bao gồm `WorkerScheduler`, `SyncManager`,
  và xử lý `ChatAction`.

## 2. 💉 Chiến lược DI (Decentralized DI)

- [x] **Chuyển đổi DI về Module**: Di chuyển các Hilt Module từ `:app` về đúng module chứa logic.
- [x] **Chuẩn hóa Module DI**: Mỗi module sẽ có một package `di` riêng.

## 3. 🎨 Refactor core:design_system & core:presentation

- [ ] **Phân tách trách nhiệm**: `:core:design_system` (Pure UI/Theme) vs `:core:presentation` (
  Logic UI/State Wrappers).
- [ ] **Di chuyển components**: Chuyển `BoxContainer`, `VisibleLoadingScreen`, `ObserveEffect` sang
  `:core:presentation`.
- [ ] **Di chuyển launchers**: Chuyển logic `ImagePicker`, `CameraPicker` sang `:core:presentation`.
- [ ] **Update Imports**: Cập nhật lại toàn bộ import tại các module feature.

## 4. 📊 Tầng Data (Repository & DataSource)

- [ ] **Phân rã Repository**:
  - [ ] Chia `ConversationRepository` thành `ConversationQueryRepository` (Chỉ đọc/Stream) và
    `ConversationActionRepository` (Chỉ ghi).
  - [ ] Chia `MessageRepository` tương tự.
- [/] **Chiến lược Sync thống nhất**: Tạo một `MutationOrchestrator` để tự động xử lý logic
  Offline-first. (Đã áp dụng mẫu cho Delete Conversation).
- [ ] **Room Auto-Migration**: Chuyển từ viết SQL migration thủ công sang Auto-Migration API của
  Room.

## 4. 🏷️ Chuẩn hóa đặt tên (Naming Conventions)

- [/] **Bắt buộc hậu tố (Suffix)**: `DTO`, `Entity`, `Model`, `Mapper`. (Đã refactor ChatAction và
  các class liên quan).
- [/] **Đặt tên hàm**: `observe...` (Flow), `get...` (suspend/once), `update...`/`delete...`/
  `add...` (mutation).

## 5. 🛠️ Cơ sở hạ tầng

- [ ] **AppLogger Wrapper**: Tạo wrapper cho `Timber` để có thể dễ dàng tích hợp thêm các công cụ
  như Crashlytics.

---
*Ghi chú: Giữ nguyên cấu trúc feature modules (không chia api/impl) và tầng Domain/ViewModel hiện
tại theo yêu cầu.*
