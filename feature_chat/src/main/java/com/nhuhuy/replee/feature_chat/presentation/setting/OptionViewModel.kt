package com.nhuhuy.replee.feature_chat.presentation.setting

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.data.repository.AccountRepository
import com.nhuhuy.replee.core.common.error_handling.onFailure
import com.nhuhuy.replee.core.common.error_handling.onSuccess
import com.nhuhuy.replee.core.common.utils.Validator
import com.nhuhuy.replee.core.design_system.component.DynamicInput
import com.nhuhuy.replee.feature_chat.data.SyncManager
import com.nhuhuy.replee.feature_chat.domain.model.Conversation
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationRepository
import com.nhuhuy.replee.feature_chat.domain.repository.ConversationSettingRepository
import com.nhuhuy.replee.feature_chat.presentation.setting.component.SecondaryOption
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionAction
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionEvent
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionOverlay
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = OptionViewModel.Factory::class)
class OptionViewModel @AssistedInject constructor(
    @Assisted("conversationId") private val conversationId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("name") private val otherUserName: String,
    @Assisted("email") private val otherUserEmail: String,
    private val validator: Validator,
    private val syncManager: SyncManager,
    private val accountRepository: AccountRepository,
    private val conversationRepository: ConversationRepository,
    private val conversationSettingRepository: ConversationSettingRepository
) : BaseViewModel<OptionAction, OptionEvent, OptionState>() {
    val conversation = conversationRepository.observeConversationById(conversationId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Conversation())

    private val _state = MutableStateFlow(
        OptionState(
            otherUserId = otherUserId,
            otherUserName = otherUserName,
            otherUserEmail = otherUserEmail
        )
    )
    override val state: StateFlow<OptionState>
        get() = _state.asStateFlow()

    override fun onAction(action: OptionAction) {
        viewModelScope.launch {
            when (action) {
                OptionAction.OnBackPressed -> {
                    onEvent(OptionEvent.NavigateBack)
                }

                is OptionAction.OnSecondaryOptionSelect -> {
                    when (action.secondaryOption) {
                        SecondaryOption.SET_NICK -> {
                            _state.reduce { copy(overlay = OptionOverlay.SET_NICK_NAME) }
                        }

                        SecondaryOption.BLOCK -> {
                            accountRepository.updateBlockedUsers(otherUser = otherUserId)
                            onEvent(OptionEvent.NavigateBack)
                        }

                        SecondaryOption.SET_THEME -> {
                            //TODO("Set theme"
                        }

                        SecondaryOption.DELETE_CONVERSATION -> {
                            _state.reduce { copy(overlay = OptionOverlay.DELETE_CHAT) }
                        }
                    }
                }

                is OptionAction.OnMute -> {
                    conversationSettingRepository.muteOtherUser(
                        conversationId,
                        otherUserId,
                        action.enable
                    )
                        .onSuccess {
                            syncManager.updateConversationStatus(conversationId, synced = true)
                        }
                        .onFailure {
                            syncManager.updateConversationStatus(conversationId, synced = false)
                        }
                }

                is OptionAction.OnPin -> {
                    conversationSettingRepository.pinConversation(
                        conversationId,
                        otherUserId,
                        action.enable
                    )
                        .onSuccess {
                            syncManager.updateConversationStatus(conversationId, synced = true)
                        }
                        .onFailure {
                            syncManager.updateConversationStatus(conversationId, synced = false)
                        }
                }

                OptionAction.OnConversationDelete -> {
                    conversationSettingRepository.deleteConversation(conversationId)
                        .onSuccess {
                            syncManager.updateConversationStatus(conversationId, synced = true)
                        }
                        .onFailure {
                            syncManager.updateConversationStatus(conversationId, synced = false)
                        }
                    _state.reduce {
                        copy(overlay = OptionOverlay.NONE)
                    }
                }

                is OptionAction.OnOwnerNickNameChange -> {
                    _state.reduce {
                        copy(
                            ownerNickName = DynamicInput(
                                text = action.name,
                                validateResult = validator.validateNickName(action.name)
                            )
                        )
                    }
                }

                OptionAction.OnDismiss -> {
                    _state.reduce { copy(overlay = OptionOverlay.NONE) }
                }

                is OptionAction.OnOtherNickNameChange -> {
                    _state.reduce {
                        copy(
                            otherUserNickName = DynamicInput(
                                text = action.name,
                                validateResult = validator.validateNickName(action.name)
                            )
                        )
                    }
                }

                OptionAction.OnNickNameSet -> {
                    val ownerNickname = state.value.ownerNickName
                    val otherUserNickname = state.value.otherUserNickName
                    if (otherUserNickname.text.isNotEmpty()) {
                        conversationSettingRepository.updateOwnerNickname(
                            uid = otherUserId,
                            conversationId = conversationId,
                            nickName = otherUserNickname.text
                        )
                    }
                    if (ownerNickname.text.isNotEmpty()) {
                        conversationSettingRepository.updateOwnerNickname(
                            uid = currentUserId,
                            conversationId = conversationId,
                            nickName = ownerNickname.text
                        )
                    }
                    _state.reduce {
                        copy(
                            ownerNickName = DynamicInput(),
                            otherUserNickName = DynamicInput(),
                            overlay = OptionOverlay.NONE
                        )
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("conversationId") conversationId: String,
            @Assisted("currentUserId") currentUserId: String,
            @Assisted("otherUserId") otherUserId: String,
            @Assisted("name") otherUserName: String,
            @Assisted("email") otherUserEmail: String
        ): OptionViewModel
    }
}