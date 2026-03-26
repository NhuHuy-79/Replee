# Use cases for feature_chat

---

## Conversation

a. ListenToConversationUseCase: Listen data changes from real-time database.
b. LoadAllConversationsUsaCase: Load all conversations with limit (use Paging) from local database.
c. UpdateConversationDataUseCase: Update conversation metadata like Last message, Last SenderId.
d. SaveConversationListUseCase: Upsert and update conversation data from real-time database.
e. GetSearchHistoryUseCase: Load history of search result in conversation screen.

## Message

a. SendMessageUseCase: Send text message in conversation system.
b. SendImageUseCase: Send image in conversation system.
c. ListenToMessageUseCase: Listen data changes from real-time database.
d. UpdateMessageUseCase: Update message changes from real-time database.
e. LoadMessagesUseCase: Load all messages from local database.
f. ReadMessageUseCase: Mark all visible messages in screen read.
g. UpdateMessageUseCase: Update message changes from real-time database.

## Option

a. CheckIsBlockedUseCase: Check if owner was blocked!.
b. CheckUserIsBlock: Check if other user is blocked!.
c. BlockUseUseCase: Block other user in conversation.
d. MuteUserUseCase: Mute other user in conversation and not send notification.
e. PinConversationUseCase: Pin conversations in the top of list.
f. UpdateOtherUserNickNameUseCase: Update otherUser's nick name in conversation screen.
g. GetConversationUseCase: Get information of current conversation in Option Screen.
f. DeleteConversationUseCase: Delete all messages in conversation.

//Cần tách hẳn các hàm toggle trong ConversationRepository.
//Xem lại phần sync tin nhắn
//Xem lại WorK manager (sCheduler sẽ để tầng data)
//Xem lại các map lại đè nhau không
//Đè callback để cloudinary
