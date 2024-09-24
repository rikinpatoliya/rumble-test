@file:Suppress("DEPRECATION")

package com.rumble.battles.profile.presentation

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rumble.battles.CountryPickerTag
import com.rumble.battles.EditProfileImageTag
import com.rumble.battles.EditProfileTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.CheckMarkItem
import com.rumble.battles.commonViews.MainActionBottomCardView
import com.rumble.battles.commonViews.MenuSelectionItem
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleDropDownMenu
import com.rumble.battles.commonViews.RumbleInputFieldView
import com.rumble.battles.commonViews.RumbleInputSelectorFieldView
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.RumbleWheelDataPicker
import com.rumble.battles.commonViews.keyboardAsState
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedGray900
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXXLarge
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants.ACTIVITY_RESULT_CONTRACT_IMAGE_INPUT_TYPE
import com.rumble.utils.RumbleConstants.BIRTHDAY_DATE_PATTERN
import com.rumble.utils.RumbleConstants.PROFILE_IMAGE_BITMAP_MAX_WIDTH
import com.rumble.utils.errors.InputValidationError
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.convertToDate
import com.rumble.utils.extension.scaleToMaxWidth
import com.rumble.utils.extension.toUtcLocalDate
import com.rumble.utils.extension.toUtcLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val NEW_IMAGE_URI_KEY = "newImageUri"
private const val TAG = "EditProfileScreen"
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditProfileScreen(
    editProfileHandler: EditProfileHandler,
    onBackClick: (newImageUri: Uri?) -> Unit,
) {
    val state by editProfileHandler.uiState.collectAsStateWithLifecycle()
    val userName by editProfileHandler.userNameFlow.collectAsStateWithLifecycle(initialValue = "")
    val userPicture by editProfileHandler.userPictureFlow.collectAsStateWithLifecycle(initialValue = "")
    val sheetItems by editProfileHandler.countriesList.collectAsStateWithLifecycle(initialValue = emptyList())
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackBarHostState = remember { SnackbarHostState() }
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    var showDatePicker by remember { mutableStateOf(false) }

    BackHandler(bottomSheetState.isVisible) {
        coroutineScope.launch { bottomSheetState.hide() }
    }
    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        editProfileHandler.onProfileImageChanged(uri)
    }

    LaunchedEffect(key1 = context) {
        editProfileHandler.vmEvents.collect { event ->
            when (event) {
                is EditProfileVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }
                is EditProfileVmEvent.ProfileUpdateResult -> {
                    snackBarHostState.showRumbleSnackbar(message = context.getString(event.messageStringId))
                }
                is EditProfileVmEvent.ShowCountrySelection -> {
                    focusManager.clearFocus()
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }

                EditProfileVmEvent.ShowDateSelectionDialog -> {
                    showDatePicker = showDatePicker.not()
                    if (showDatePicker) focusManager.clearFocus()
                }
            }
        }
    }

    val modifier = Modifier
        .systemBarsPadding()
    if (state.initialFetch.not()) {
        RumbleModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetContent = {
                CountrySelectionBottomSheet(
                    state,
                    sheetItems,
                    coroutineScope,
                    bottomSheetState,
                    editProfileHandler
                )
            }) {
            Column(
                modifier = modifier
                    .testTag(EditProfileTag)
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                RumbleBasicTopAppBar(
                    title = stringResource(id = R.string.edit_profile),
                    modifier = Modifier
                        .fillMaxWidth(),
                    onBackClick = { onBackClick(state.imageUri) },
                )
                EditProfileContent(
                    modifier = Modifier
                        .weight(1F),
                    editProfileHandler = editProfileHandler,
                    state = state,
                    userName = userName,
                    userPicture = userPicture,
                    context = context,
                ) {
                    launcher.launch(ACTIVITY_RESULT_CONTRACT_IMAGE_INPUT_TYPE)
                }

                if (!keyboardAsState().value) {
                    MainActionBottomCardView(
                        modifier = Modifier,
                        title = stringResource(id = R.string.save),
                        onClick = editProfileHandler::onUpdateUserProfile
                    )
                }
                AnimatedVisibility(
                    visible = showDatePicker,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    RumbleWheelDataPicker(initialValue = state.userProfileEntity.birthday?.toUtcLong()
                        ?: 0L,
                        onChanged = { editProfileHandler.onBirthdayChanged(it.toUtcLocalDate()) })
                }
            }
        }
    }


    if (state.loading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    RumbleSnackbarHost(snackBarHostState = snackBarHostState)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CountrySelectionBottomSheet(
    state: UserProfileUIState,
    countries: List<CountryEntity>,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    editProfileHandler: EditProfileHandler
) {
    Box(
        modifier = Modifier
            .testTag(CountryPickerTag)
            .systemBarsPadding()
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(paddingMedium))
            countries.forEach { item ->
                CheckMarkItem(
                    title = item.countryName,
                    selected = item.countryID == state.userProfileEntity.country.countryID,
                    addSeparator = countries.last() != item,
                ) {
                    coroutineScope.launch {
                        editProfileHandler.onCountryChanged(item)
                        bottomSheetState.hide()
                    }
                }
            }
            Spacer(modifier = Modifier.height(paddingLarge))
        }
    }
}

@Composable
fun EditProfileImage(
    userName: String,
    userPicture: String,
    state: UserProfileUIState,
    context: Context,
    onImageClick: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(EditProfileImageTag),
    ) {
        val (profileImage, icon) = createRefs()
        state.imageUri?.let {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images
                    .Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source).scaleToMaxWidth(PROFILE_IMAGE_BITMAP_MAX_WIDTH)
            }
            AsyncImage(
                modifier = Modifier
                    .size(imageXXLarge)
                    .clip(CircleShape)
                    .constrainAs(profileImage) {
                        centerTo(parent)
                    }
                    .clickable { onImageClick() },
                model = bitmap, contentDescription = userName
            )
        } ?: kotlin.run {
            ProfileImageComponent(
                modifier = Modifier
                    .constrainAs(profileImage) {
                        centerTo(parent)
                    }
                    .clickableNoRipple { onImageClick() },
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXLargeStyle(),
                userName = userName,
                userPicture = state.userProfileEntity.userPicture.ifEmpty { userPicture }
            )
        }
        Box(
            modifier = Modifier
                .padding(start = paddingXSmall)
                .clip(CircleShape)
                .background(rumbleGreen)
                .constrainAs(icon) {
                    top.linkTo(profileImage.top)
                    end.linkTo(profileImage.end)
                }
                .clickable { onImageClick() },
        ) {
            Image(
                modifier = Modifier.padding(paddingXSmall),
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = stringResource(id = R.string.notifications),
                colorFilter = ColorFilter.tint(enforcedWhite)
            )
        }
    }
}

@Composable
private fun EditProfileContent(
    modifier: Modifier = Modifier,
    editProfileHandler: EditProfileHandler,
    state: UserProfileUIState,
    userName: String,
    userPicture: String,
    context: Context,
    onImageClick: () -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .padding(
                start = paddingMedium,
            )
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(
            Modifier
                .height(paddingMedium)
        )
        EditProfileImage(
            userName = userName,
            userPicture = userPicture,
            state = state,
            context = context,
            onImageClick = onImageClick,
        )
        Spacer(
            Modifier
                .height(paddingLarge)
        )
        RumbleInputFieldView(
            label = stringResource(id = R.string.name).uppercase(),
            initialValue = state.userProfileEntity.fullName,
            onValueChange = editProfileHandler::onFullNameChanged,
            hasError = state.fullNameError,
            errorMessage = state.fullNameErrorMessage
        )
        Spacer(
            Modifier
                .height(paddingMedium)
        )
        RumbleInputFieldView(
            label = stringResource(id = R.string.phone).uppercase(),
            initialValue = state.userProfileEntity.phone,
            onValueChange = editProfileHandler::onPhoneChanged,
            hasError = false,
            errorMessage = ""
        )
        Spacer(
            Modifier
                .height(paddingMedium)
        )
        RumbleInputFieldView(
            label = stringResource(id = R.string.address).uppercase(),
            initialValue = state.userProfileEntity.address,
            onValueChange = editProfileHandler::onAddressChanged,
            hasError = false,
            errorMessage = ""
        )
        Spacer(
            Modifier
                .height(paddingMedium)
        )
        RumbleInputFieldView(
            label = stringResource(id = R.string.city).uppercase(),
            initialValue = state.userProfileEntity.city,
            onValueChange = editProfileHandler::onCityChanged,
            hasError = state.cityError,
            errorMessage = state.cityErrorMessage
        )
        Spacer(
            Modifier
                .height(paddingMedium)
        )
        RumbleInputFieldView(
            label = stringResource(id = R.string.state).uppercase(),
            initialValue = state.userProfileEntity.state,
            onValueChange = editProfileHandler::onStateChanged,
            hasError = state.stateError,
            errorMessage = state.stateErrorMessage
        )
        Spacer(
            Modifier
                .height(paddingMedium)
        )
        RumbleInputFieldView(
            label = stringResource(id = R.string.zip_postal_code).uppercase(),
            initialValue = state.userProfileEntity.postalCode,
            onValueChange = editProfileHandler::onPostalCodeChanged,
            hasError = state.postalCodeError,
            errorMessage = state.postalCodeErrorMessage
        )
        Spacer(
            Modifier
                .height(paddingMedium)
        )
        RumbleInputSelectorFieldView(
            label = stringResource(id = R.string.country).uppercase(),
            labelColor = MaterialTheme.colors.primary,
            value = state.userProfileEntity.country.countryName,
            hasError = state.countryError,
            errorMessage = stringResource(id = R.string.please_select_your_country),
        ) { editProfileHandler.onSelectCountry() }
        Spacer(
            Modifier
                .height(paddingMedium)
        )

        RumbleInputSelectorFieldView(
            label = stringResource(id = R.string.birthday).uppercase(),
            labelColor = enforcedWhite,
            backgroundColor = enforcedGray900,
            textColor = enforcedWhite,
            errorMessageColor = enforcedBone,
            value = if (state.userProfileEntity.birthday == null) "" else state.userProfileEntity.birthday?.toUtcLong()
                ?.convertToDate(
                    pattern = BIRTHDAY_DATE_PATTERN, useUtc = true
                ) ?: "",
            hasError = state.birthdayError.first,
            errorMessage = when (state.birthdayError.second) {
                InputValidationError.Empty -> stringResource(id = R.string.birthday_empty_error_message)
                InputValidationError.MinCharacters -> stringResource(
                    id = R.string.birthday_at_least_13_error_message
                )

                else -> ""
            }
        ) {
            editProfileHandler.onSelectBirthday()
        }

        Spacer(
            Modifier
                .height(paddingMedium)
        )

        RumbleDropDownMenu(
            modifier = Modifier.fillMaxWidth(),
            placeHolder = stringResource(id = R.string.select_gender),
            label = stringResource(id = R.string.gender),
            backgroundColor = enforcedGray900,
            textColor = enforcedWhite,
            iconTint = enforcedWhite,
            labelColor = enforcedWhite,
            initialValue = buildGenderInitialSelection(gender = state.gender),
            items = listOf(
                MenuSelectionItem(
                    text = stringResource(id = R.string.gender_male),
                    action = { editProfileHandler.onGenderSelected(Gender.Mail) }
                ),
                MenuSelectionItem(
                    text = stringResource(id = R.string.gender_female),
                    action = { editProfileHandler.onGenderSelected(Gender.Female) }
                )
            ),
            onClearSelection = {
                editProfileHandler.onGenderSelected(Gender.Unspecified)
            }
        )

        Spacer(
            Modifier
                .height(paddingLarge)
        )


        Divider(
            color = MaterialTheme.colors.secondaryVariant
        )
        Spacer(
            Modifier
                .height(paddingLarge)
        )
        RumbleInputFieldView(
            label = stringResource(id = R.string.paypal_email_address).uppercase(),
            initialValue = state.userProfileEntity.paypalEmail,
            onValueChange = editProfileHandler::onPaypalEmailChanged,
            hasError = state.payPalEmailError,
            errorMessage = stringResource(id = R.string.please_provide_valid_paypal),
        )
        Spacer(
            Modifier
                .height(paddingXSmall)
        )
        Text(
            text = stringResource(id = R.string.your_payouts_sent_here),
            color = MaterialTheme.colors.secondary,
            style = RumbleTypography.h5
        )
        Spacer(
            Modifier
                .height(paddingMedium)
        )
    }
}

@Composable
private fun buildGenderInitialSelection(gender: Gender): MenuSelectionItem? =
    when (gender) {
        Gender.Mail -> MenuSelectionItem(text = stringResource(id = R.string.gender_male))
        Gender.Female -> MenuSelectionItem(text = stringResource(id = R.string.gender_female))
        Gender.Unspecified -> null
    }

