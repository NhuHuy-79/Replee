//TODO list for building new feature, refactoring, testing or ...

### 🏗️ KIẾN TRÚC & CLEAN CODE

- [ ] **Redundant Code (ConversationMapper.kt)**:
  - Hàm `toUpdatePatch(uid: String)` dường như không còn được sử dụng và có logic mapping thủ công (
    hardcode string keys). Cân nhắc xóa bỏ để tránh nhầm lẫn.
- [ ] **Redundant Code (ConversationRepositoryImp.kt)**:
  - `fetchConversations()` và `saveConversations()`: Các hàm này hiện đang tồn tại song song với cơ
    chế `listen` (Realtime). Nếu bạn đã chuyển hẳn sang Realtime sync qua `SyncManager`, nên loại bỏ
    các hàm fetch/save thủ công này để tránh xung đột dữ liệu.
- [ ] **Error Handling Consistency**:
  - Thống nhất việc sử dụng `NetworkResult` (đã có `execute` wrapper). Một số chỗ trong repository
    vẫn cần rà soát lại để đảm bảo không lọt Exception.
- [ ] **Naming Convention**:
  - Trong `ConversationDTO`, các field `user1`, `user2` nên được đặt tên rõ ràng hơn (như `owner`,
    `otherUser`) để code mapping dễ đọc hơn.
- [ ] **Log Cleanup**:
  - Xóa các `Timber.d` debug trong `ConversationRepositoryImp` và `ConversationMapper` trước khi
    release.

### 🐛 LỖI TIỀM ẨN & TỐI ƯU

- [ ] **getOrCreateConversation Safety**:
  - Hàm này gọi `entity.createConversationDTO()` mà không kiểm tra `entity` có null hay không. Nếu
    chưa có conversation local, nó sẽ crash.
- [ ] **SyncManager Optimization**:
  - Kiểm tra xem `SyncManager` có đang listen quá nhiều hay không. Cần đảm bảo `close` flow khi
    không cần thiết.
- [ ] **MessageRemoteMediator**:
  - Rà soát lại logic `loadKey`. Đảm bảo phân trang hoạt động đúng khi có tin nhắn mới chen ngang.

### ✨ TÍNH NĂNG ĐANG DANG DỞ (Found via Grep)

- [ ] **OptionViewModel**: Cài đặt theme (`//TODO("Set theme")`)
- [ ] **ChatScreen**: Xử lý click Camera (`//TODO("camera clicked")`) và chuyển màn Search (
  `//TODO("navigate to Search screen")`)
- [ ] **UploadFileWorker**: Cần check tin nhắn tồn tại trước khi upload (
  `//TODO("Need check message is Exist or not")`)
- [ ] **ProfileScreen**: Xử lý Storage và Edit Password.
- [ ] **UpdateMessageChangeUseCase**: Chưa có logic xử lý (`//TODO`).

---
*Cập nhật ngày: 23/10/2023 - Bạn ngủ ngon nhé!*
