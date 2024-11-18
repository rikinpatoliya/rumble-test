package com.rumble.battles.profile.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.battles.R
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.profile.domain.GetCountriesUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.profile.domain.UpdateUserImageUseCase
import com.rumble.domain.profile.domain.UpdateUserProfileUseCase
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.settings.domain.domainmodel.UpdateUserProfileResult
import com.rumble.domain.validation.usecases.BirthdayValidationUseCase
import com.rumble.domain.validation.usecases.EmailValidationUseCase
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants
import com.rumble.utils.errors.InputValidationError
import com.rumble.utils.extension.toUtcLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

interface EditProfileHandler {
    val uiState: StateFlow<UserProfileUIState>
    val userNameFlow: Flow<String>
    val userPictureFlow: Flow<String>
    val vmEvents: Flow<EditProfileVmEvent>
    val countriesList: StateFlow<List<CountryEntity>>

    fun onFullNameChanged(value: String)
    fun onPhoneChanged(value: String)
    fun onAddressChanged(value: String)
    fun onCityChanged(value: String)
    fun onStateChanged(value: String)
    fun onPostalCodeChanged(value: String)
    fun onCountryChanged(countryEntity: CountryEntity)
    fun onSelectCountry()
    fun onSelectBirthday()
    fun onBirthdayChanged(value: LocalDate)
    fun onPaypalEmailChanged(value: String)
    fun onProfileImageChanged(uri: Uri?)
    fun onUpdateUserProfile()
    fun onGenderSelected(gender: Gender)
}

data class UserProfileUIState(
    val userProfileEntity: UserProfileEntity,
    val imageUri: Uri? = null,
    val initialFetch: Boolean = true,
    val loading: Boolean = false,
    val countryError: Boolean = false,
    val payPalEmailError: Boolean = false,

    val fullNameError: Boolean = false,
    val fullNameErrorMessage: String = "",
    val cityError: Boolean = false,
    val cityErrorMessage: String = "",
    val stateError: Boolean = false,
    val stateErrorMessage: String = "",
    val postalCodeError: Boolean = false,
    val postalCodeErrorMessage: String = "",
    val birthdayError: Pair<Boolean, InputValidationError> = Pair(
        false,
        InputValidationError.None
    )
)

sealed class EditProfileVmEvent {
    data object ShowCountrySelection : EditProfileVmEvent()
    data object ShowDateSelectionDialog : EditProfileVmEvent()
    data class Error(val errorMessage: String? = null) : EditProfileVmEvent()
    data class ProfileUpdateResult(val messageStringId: Int) : EditProfileVmEvent()
}

private const val TAG = "EditProfileViewModel"

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateUserImageUseCase: UpdateUserImageUseCase,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val emailValidationUseCase: EmailValidationUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val birthdayValidationUseCase: BirthdayValidationUseCase,
) : ViewModel(), EditProfileHandler {

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(EditProfileVmEvent.Error())
    }

    private var userProfileEntity: UserProfileEntity =
        UserProfileEntity(
            "",
            "",
            "",
            false,
            "",
            "",
            "",
            "",
            "",
            "",
            null,
            "",
            0,
            false,
            Gender.Unspecified,
            null,
             false,
             RumbleConstants.MINIMUM_AGE_REQUIREMENT
        )

    override val uiState = MutableStateFlow(UserProfileUIState(userProfileEntity))
    override val userNameFlow: Flow<String> = sessionManager.userNameFlow
    override val userPictureFlow: Flow<String> = sessionManager.userPictureFlow

    private val _vmEvents = Channel<EditProfileVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<EditProfileVmEvent> = _vmEvents.receiveAsFlow()

    override val countriesList = MutableStateFlow(emptyList<CountryEntity>())

    init {
        viewModelScope.launch(errorHandler) {
            uiState.update { it.copy(loading = true) }
            val userProfile = async {
                val getUserProfileResult = getUserProfileUseCase()
                if (getUserProfileResult.success && getUserProfileResult.userProfileEntity != null) {
                    getUserProfileResult.userProfileEntity?.let { userProfile ->
                        userProfileEntity = userProfile
                        uiState.update {
                            it.copy(
                                userProfileEntity = userProfile,
                            )
                        }
                    }
                } else {
                    uiState.update { it.copy(loading = false) }
                    emitVmEvent(EditProfileVmEvent.Error())
                }
            }
            val countryList = async {
                countriesList.emit(getCountriesUseCase())
            }
            try {
                userProfile.await()
                countryList.await()
            } catch (e: Exception) {
                unhandledErrorUseCase(TAG, e)
            } finally {
                uiState.update {
                    it.copy(
                        initialFetch = false,
                        loading = false
                    )
                }
            }

        }

    }

    override fun onFullNameChanged(value: String) {
        userProfileEntity = userProfileEntity.copy(
            fullName = value
        )
    }

    override fun onPhoneChanged(value: String) {
        userProfileEntity = userProfileEntity.copy(
            phone = value
        )
    }

    override fun onAddressChanged(value: String) {
        userProfileEntity = userProfileEntity.copy(
            address = value
        )
    }

    override fun onCityChanged(value: String) {
        userProfileEntity = userProfileEntity.copy(
            city = value
        )
    }

    override fun onStateChanged(value: String) {
        userProfileEntity = userProfileEntity.copy(
            state = value
        )
    }

    override fun onPostalCodeChanged(value: String) {
        userProfileEntity = userProfileEntity.copy(
            postalCode = value
        )
    }

    override fun onSelectCountry() {
        emitVmEvent(EditProfileVmEvent.ShowCountrySelection)
    }

    override fun onSelectBirthday() {
        emitVmEvent(EditProfileVmEvent.ShowDateSelectionDialog)
    }

    override fun onBirthdayChanged(value: LocalDate) {
        userProfileEntity = userProfileEntity.copy(
            birthday = value
        )
        uiState.update {
            it.copy(
                userProfileEntity = userProfileEntity,
                birthdayError = Pair(false, InputValidationError.None)
            )
        }
    }


    override fun onCountryChanged(countryEntity: CountryEntity) {
        userProfileEntity = userProfileEntity.copy(
            country = countryEntity
        )
        uiState.update {
            it.copy(
                userProfileEntity = userProfileEntity,
                countryError = false
            )
        }
    }

    override fun onPaypalEmailChanged(value: String) {
        userProfileEntity = userProfileEntity.copy(
            paypalEmail = value
        )
        uiState.update {
            it.copy(
                userProfileEntity = userProfileEntity,
                payPalEmailError = false
            )
        }
    }

    override fun onProfileImageChanged(uri: Uri?) {
        uri?.let { imageUri ->
            uiState.update { it.copy(imageUri = uri) }
            viewModelScope.launch(errorHandler) {
                updateUserImageUseCase(imageUri)
                emitVmEvent(EditProfileVmEvent.ProfileUpdateResult(R.string.profile_picture_update_success_message))
                uiState.update {
                    it.copy(
                        loading = false,
                        imageUri = uri
                    )
                }
            }
        }
    }

    override fun onUpdateUserProfile() {
        if (validInput(userProfileEntity)) {
            viewModelScope.launch(errorHandler) {
                uiState.update { clearFormErrors(it.copy(loading = true)) }

                val result = updateUserProfileUseCase(userProfileEntity)

                when (result) {
                    is UpdateUserProfileResult.Error -> {
                        emitVmEvent(EditProfileVmEvent.ProfileUpdateResult(R.string.generic_error_message_contact_support))
                    }

                    is UpdateUserProfileResult.FormError -> {
                        uiState.update {
                            it.copy(
                                fullNameError = result.fullNameError,
                                cityError = result.cityError,
                                stateError = result.stateError,
                                postalCodeError = result.postalCodeError,
                                fullNameErrorMessage = result.fullNameErrorMessage,
                                cityErrorMessage = result.cityErrorMessage,
                                stateErrorMessage = result.stateErrorMessage,
                                postalCodeErrorMessage = result.postalCodeErrorMessage,
                                birthdayError = if (result.birthdayErrorMessage.isBlank()) {
                                    Pair(false, InputValidationError.None)
                                } else {
                                    Pair(
                                        true,
                                        InputValidationError.Custom(result.birthdayErrorMessage)
                                    )
                                }
                            )
                        }
                    }

                    is UpdateUserProfileResult.Success -> {
                        if (result.requiresConfirmation) {
                            // email confirmation required, ask user to check email
                            emitVmEvent(EditProfileVmEvent.ProfileUpdateResult(R.string.check_email_to_confirm_details))
                        } else {
                            // profile updated and no email confirmation required
                            emitVmEvent(EditProfileVmEvent.ProfileUpdateResult(R.string.profile_details_update_success_message))
                        }
                    }
                }

                uiState.update {
                    it.copy(
                        loading = false
                    )
                }
            }
        }
    }

    override fun onGenderSelected(gender: Gender) {
        userProfileEntity = userProfileEntity.copy(
            gender = gender
        )
        uiState.update {
            it.copy(userProfileEntity = userProfileEntity)
        }
    }

    private fun clearFormErrors(userProfileUIState: UserProfileUIState): UserProfileUIState =
        userProfileUIState.copy(
            fullNameError = false,
            cityError = false,
            stateError = false,
            postalCodeError = false,
            fullNameErrorMessage = "",
            cityErrorMessage = "",
            stateErrorMessage = "",
            postalCodeErrorMessage = "",
            birthdayError = Pair(false, InputValidationError.None)
        )

    private fun validInput(userProfileEntity: UserProfileEntity): Boolean {
        var validInput = true
        if (userProfileEntity.country == null) {
            uiState.update {
                it.copy(
                    countryError = true
                )
            }
            validInput = false
        }
        if (userProfileEntity.paypalEmail.isNotEmpty() &&
            !emailValidationUseCase(userProfileEntity.paypalEmail)
        ) {
            uiState.update {
                it.copy(
                    payPalEmailError = true
                )
            }
            validInput = false
        }

        val birthdayError = birthdayValidationUseCase(
            userProfileEntity.birthday?.toUtcLong(),
            userProfileEntity.minEligibleAge
        )
        if (birthdayError.first) {
            uiState.update {
                it.copy(
                    birthdayError = birthdayError
                )
            }
            validInput = false
        }

        return validInput
    }

    private fun emitVmEvent(event: EditProfileVmEvent) {
        _vmEvents.trySend(event)
    }
}