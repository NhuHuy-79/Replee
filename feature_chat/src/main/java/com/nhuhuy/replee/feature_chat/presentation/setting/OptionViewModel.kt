package com.nhuhuy.replee.feature_chat.presentation.setting

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.data.repository.AccountRepository
import com.nhuhuy.replee.core.common.utils.Validator
import com.nhuhuy.replee.core.design_system.component.DynamicInput
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
    @Assisted("uid") private val otherUserId: String,
    @Assisted("name") private val otherUserName: String,
    @Assisted("email") private val otherUserEmail: String,
    private val validator: Validator,
    private val accountRepository: AccountRepository,
    private val conversationRepository: ConversationRepository,
    private val conversationSettingRepository: ConversationSettingRepository
) : BaseViewModel<OptionAction, OptionEvent, OptionState>() {
    val conversation = conversationRepository.observeConversationById(conversationId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Conversation())

    val blocked = accountRepository.observeBlockStatus(conversationId, otherUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
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
                            //TODO("set nick name for user)
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
                    conversationSettingRepository.muteOtherUser(conversationId)
                }

                is OptionAction.OnPin -> {
                    conversationSettingRepository.pinConversation(conversationId)
                }

                OptionAction.OnConversationDelete -> {
                    conversationSettingRepository.deleteConversation(conversationId)
                    _state.reduce {
                        copy(overlay = OptionOverlay.NONE)
                    }
                }

                is OptionAction.OnNameSet -> {
                    //TODO("set nick name for user")
                    _state.reduce { copy(overlay = OptionOverlay.NONE) }
                }

                is OptionAction.OnNameChange -> {
                    _state.reduce {
                        copy(
                            nickName = DynamicInput(
                                text = action.name,
                                validateResult = validator.validateNickName(action.name)
                            )
                        )
                    }
                }

                OptionAction.OnDismiss -> {
                    _state.reduce { copy(overlay = OptionOverlay.NONE) }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("conversationId") conversationId: String,
            @Assisted("uid") otherUserId: String,
            @Assisted("name") otherUserName: String,
            @Assisted("email") otherUserEmail: String
        ): OptionViewModel
    }
}