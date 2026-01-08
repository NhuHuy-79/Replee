package com.nhuhuy.replee.feature_chat.presentation.information

import androidx.lifecycle.viewModelScope
import com.nhuhuy.replee.core.common.base.BaseViewModel
import com.nhuhuy.replee.feature_chat.presentation.information.component.Option
import com.nhuhuy.replee.feature_chat.presentation.information.state.InformationAction
import com.nhuhuy.replee.feature_chat.presentation.information.state.InformationEvent
import com.nhuhuy.replee.feature_chat.presentation.information.state.InformationState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = InformationViewModel.Factory::class)
class InformationViewModel @AssistedInject constructor(
    @Assisted("uid") private val otherUserId: String,
    @Assisted("name") private val otherUserName: String,
    @Assisted("email") private val otherUserEmail: String
) : BaseViewModel<InformationAction, InformationEvent, InformationState>(){
    private val _state = MutableStateFlow(
        InformationState(
            otherUserId = otherUserId,
            otherUserName = otherUserName
        )
    )
    override val state: StateFlow<InformationState>
        get() = _state.asStateFlow()

    override fun onAction(action: InformationAction) {
        viewModelScope.launch {
            when (action) {
                InformationAction.OnBackPressed -> {
                    onEvent(InformationEvent.NavigateBack)
                }
                is InformationAction.OnOptionSelect -> {
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
            }
        }
    }

    @AssistedFactory
    interface Factory{
        fun create(
            @Assisted("uid") otherUserId: String,
            @Assisted("name") otherUserName: String,
            @Assisted("email") otherUserEmail: String
        ) : InformationViewModel
    }

}