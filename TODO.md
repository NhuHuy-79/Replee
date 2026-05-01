# 🛠️ Replee Project Refactor Roadmap

Tài liệu này theo dõi quá trình refactor dài hạn để quản lý sự phát triển của dự án, chuẩn hóa quy ước và tối ưu hóa kiến trúc.

## 1. 🏗️ Kiến trúc & Module hóa
- [x] **Trích xuất `:core:model`**: Di chuyển tất cả Domain Models (Conversation, Message, Account...) vào một module thuần Kotlin để tránh phụ thuộc vòng.
- [x] **Tạo `:core:sync`**: Cô lập toàn bộ logic đồng bộ, bao gồm `WorkerScheduler`, `SyncManager`, và xử lý `ChatAction`.

## 2. 💉 Chiến lược DI (Decentralized DI)
- [x] **Chuyển đổi DI về Module**: Di chuyển các Hilt Module từ `:app` về đúng module chứa logic.
- [x] **Chuẩn hóa Module DI**: Mỗi module sẽ có một package `di` riêng.

## 3. 🎨 Refactor core:design_system & core:presentation
- [x] **Phân tách trách nhiệm**: `:core:design_system` (Pure UI/Theme) vs `:core:presentation` (Logic UI/State Wrappers).
- [x] **Di chuyển components**: Chuyển `BoxContainer`, `VisibleLoadingScreen`, `ObserveEffect` sang `:core:presentation`.
- [x] **Di chuyển launchers**: Chuyển logic `ImagePicker`, `CameraPicker` sang `:core:presentation`.
- [x] **Update Imports**: Cập nhật lại toàn bộ import tại các module feature.

## 4. 📊 Tầng Data (Repository & DataSource)
- [x] **Consolidation**: Di chuyển toàn bộ Repository Implementation về `:core:data`.
- [ ] **Phân rã Repository**:
    - [ ] Chia `ConversationRepository` thành `ConversationQueryRepository` và `ConversationActionRepository`.
    - [ ] Chia `MessageRepository` tương tự.
- [ ] **Chiến lược Sync thống nhất**: Tạo một `MutationOrchestrator` trong `:core:sync`.
- [ ] **Room Auto-Migration**: Chuyển sang Auto-Migration API của Room.

## 5. 🏷️ Chuẩn hóa đặt tên (Naming Conventions)
- [/] **Bắt buộc hậu tố (Suffix)**: `DTO`, `Entity`, `Model`, `Mapper`.
- [ ] **Đặt tên hàm**: `observe...` (Flow), `get...` (suspend/once), `update...`/`delete...`/`add...` (mutation).

## 6. 🛠️ Cơ sở hạ tầng
- [ ] **AppLogger Wrapper**: Tạo wrapper cho `Timber`.

---
*Ghi chú: Giữ nguyên cấu trúc feature modules (không chia api/impl) và tầng Domain/ViewModel hiện tại theo yêu cầu.*
