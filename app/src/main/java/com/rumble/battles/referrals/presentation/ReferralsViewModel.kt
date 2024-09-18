package com.rumble.battles.referrals.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.ShareUseCase
import com.rumble.domain.referrals.domain.usecase.GetReferralLinkUseCase
import com.rumble.domain.referrals.domain.usecase.GetReferralsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ReferralsHandler {
    val state: StateFlow<ReferralsState>
    fun refresh()
    fun share(title: String, text: String)
}

private const val TAG = "ReferralsViewModel"

@HiltViewModel
class ReferralsViewModel @Inject constructor(
    private val getReferralsUseCase: GetReferralsUseCase,
    private val getReferralLinkUseCase: GetReferralLinkUseCase,
    private val shareUseCase: ShareUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), ReferralsHandler {

    override val state = MutableStateFlow(ReferralsState())

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    init {
        refresh()
    }

    override fun refresh() {
        viewModelScope.launch(errorHandler) {
            state.value = state.value.copy(loading = true)

            val referralLinkResult = getReferralLinkUseCase.invoke()
            if (referralLinkResult.isSuccess) {
                state.value = state.value.copy(referralUrl = referralLinkResult.getOrDefault(""))
            } else {
                // TODO Handle error
            }

            val result = getReferralsUseCase.invoke()
            val referralDetailsEntity = result.getOrNull()
            if (result.isSuccess && referralDetailsEntity != null) {
                state.value = state.value.copy(
                    loading = false,
                    referralDetailsEntity = referralDetailsEntity
                )
            } else {
                // TODO Handle Error
            }
        }
    }

    override fun share(title: String, text: String) {
        shareUseCase.invoke(text = text, title = title)
    }
}