package com.nhuhuy.replee.feature_chat.presentation.option

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.core.common.base.reduce
import com.nhuhuy.replee.core.common.utils.InputValidator
import com.nhuhuy.replee.core.design_system.component.ValidatableInput
import com.nhuhuy.replee.feature_chat.domain.usecase.block.BlockUserUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.option.DeleteConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.option.LoadConversationInformationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.option.MuteUserUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.option.PinConversationUseCase
import com.nhuhuy.replee.feature_chat.domain.usecase.option.UpdateOtherNickNameUseCase
import com.nhuhuy.replee.feature_chat.presentation.option.component.SecondaryOption
import com.nhuhuy.replee.feature_chat.presentation.option.state.OptionAction
import com.nhuhuy.replee.feature_chat.presentation.option.state.OptionEvent
import com.nhuhuy.replee.feature_chat.presentation.option.state.OptionOverlay
import com.nhuhuy.replee.feature_chat.presentation.option.state.OptionState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = OptionViewModel.Factory::class)
class OptionViewModel @AssistedInject constructor(
    @Assisted("conversationId") private val conversationId: String,
    @Assisted("currentUserId") private val currentUserId: String,
    @Assisted("otherUserId") private val otherUserId: String,
    @Assisted("name") private val otherUserName: String,
    @Assisted("email") private val otherUserEmail: String,
    @Assisted("otherUserImg") private val otherUserImg: String,
    private val loadConversationInformationUseCase: LoadConversationInformationUseCase,
    private val blockUserUseCase: BlockUserUseCase,
    private val deleteConversationUseCase: DeleteConversationUseCase,
    private val pinConversationUseCase: PinConversationUseCase,
    private val muteUserUseCase: MuteUserUseCase,
    private val updateOtherNickNameUseCase: UpdateOtherNickNameUseCase,
    private val inputValidator: InputValidator,
) : BaseViewModel<OptionAction, OptionEvent, OptionState>() {
    private val _state = MutableStateFlow(
        OptionState(
            otherUserImg = otherUserImg,
            otherUserId = otherUserId,
            otherUserName = otherUserName,
            otherUserEmail = otherUserEmail
        )
    )

    init {
        viewModelScope.launch {
            val conversation = loadConversationInformationUseCase(conversationId)
            _state.reduce {
                copy(
                    pinConversation = conversation.pinned,
                    muteConversation = conversation.muted
                )
            }
        }
    }

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
                            blockUserUseCase(otherUserId = otherUserId)
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
                    _state.reduce { copy(muteConversation = action.enable) }
                    muteUserUseCase(
                        conversationId = conversationId,
                        currentUserId = currentUserId,
                        muted = action.enable
                    )
                }

                is OptionAction.OnPin -> {
                    _state.reduce { copy(pinConversation = action.enable) }
                    pinConversationUseCase(
                        conversationId = conversationId,
                        currentUserId = currentUserId,
                        pinned = action.enable
                    )
                }

                OptionAction.OnConversationDelete -> {
                    deleteConversationUseCase(conversationId)
                    _state.reduce {
                        copy(overlay = OptionOverlay.NONE)
                    }
                }

                OptionAction.OnDismiss -> {
                    _state.reduce { copy(overlay = OptionOverlay.NONE) }
                }

                is OptionAction.OnOtherNickNameChange -> {
                    _state.reduce {
                        copy(
                            otherUserNickName = ValidatableInput(
                                text = action.name,
                                validateResult = inputValidator.validateNickName(action.name)
                            )
                        )
                    }
                }

                OptionAction.OnNickNameSet -> {
                    val otherUserNickname = state.value.otherUserNickName
                    _state.reduce {
                        copy(
                            otherUserNickName = ValidatableInput(),
                            overlay = OptionOverlay.NONE
                        )
                    }
                    if (otherUserNickname.text.isNotEmpty()) {
                        updateOtherNickNameUseCase(
                            uid = otherUserId,
                            conversationId = conversationId,
                            nickName = otherUserNickname.text
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
            @Assisted("email") otherUserEmail: String,
            @Assisted("otherUserImg") otherUserImg: String,
        ): OptionViewModel
    }
}