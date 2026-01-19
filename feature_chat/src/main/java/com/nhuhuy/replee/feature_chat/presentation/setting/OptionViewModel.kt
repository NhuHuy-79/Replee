package com.nhuhuy.replee.feature_chat.presentation.setting

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.feature_chat.presentation.setting.component.Option
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionAction
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionEvent
import com.nhuhuy.replee.feature_chat.presentation.setting.state.OptionState
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
    @Assisted("uid") private val otherUserId: String,
    @Assisted("name") private val otherUserName: String,
    @Assisted("email") private val otherUserEmail: String
) : BaseViewModel<OptionAction, OptionEvent, OptionState>(){
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
                is OptionAction.OnMainOptionSelect -> {
                    when (action.option) {
                        Option.MESSAGE -> {
                            TODO("Back to message")
                        }
                        Option.CALL -> {
                            TODO("Back to call")
                        }
                        Option.MUTE -> {
                            TODO("Mute user")
                        }
                    }
                }

                is OptionAction.OnSecondaryOptionSelect -> {}
            }
        }
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("uid") otherUserId: String,
            @Assisted("name") otherUserName: String,
            @Assisted("email") otherUserEmail: String
        ) : OptionViewModel
    }

}