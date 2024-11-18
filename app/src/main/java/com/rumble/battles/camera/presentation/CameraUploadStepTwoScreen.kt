package com.rumble.battles.camera.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.UploadForm2Tag
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.UploadCheckBoxView
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.camera.UploadScheduleOption
import com.rumble.domain.camera.UploadVisibility
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.AnnotatedTextAction
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h6Heavy
import com.rumble.theme.channelActionsButtonWidth
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.minUploadStepTwoRowHeight
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.rumbleGreen
import com.rumble.theme.wokeGreen
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.UPLOAD_DATE_PATTERN
import com.rumble.utils.RumbleConstants.UPLOAD_TIME_PATTERN
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.convertToDate

private const val TAG = "CameraUploadStepTwoScreen"

@Composable
fun CameraUploadStepTwoScreen(
    cameraUploadHandler: CameraUploadHandler,
    activityHandler: RumbleActivityHandler,
    onSelectLicense: () -> Unit,
    onSelectVisibility: () -> Unit,
    onSelectSchedule: () -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by cameraUploadHandler.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .testTag(UploadForm2Tag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.info),
            modifier = Modifier.fillMaxWidth(),
            onBackClick = onBackClick,
            extraContent = {
                ActionButton(
                    modifier = Modifier
                        .width(channelActionsButtonWidth)
                        .padding(end = paddingMedium),
                    text = stringResource(id = R.string.publish),
                    textModifier = Modifier.padding(
                        top = paddingXXXSmall,
                        bottom = paddingXXXSmall,
                    ),
                    textColor = enforcedDarkmo
                ) {
                    cameraUploadHandler.onPublishClicked(activityHandler::onNavigateToMyVideos)
                }
            }
        )
        UploadCheckBoxView(
            modifier = Modifier.padding(
                top = paddingMedium,
                start = paddingMedium,
                end = paddingMedium,
            ),
            text = stringResource(id = R.string.you_have_not_signed_exclusive_agreement),
            checked = uiState.exclusiveAgreementChecked,
            onToggleCheckedState = cameraUploadHandler::onExclusiveAgreementCheckedChanged,
            hasError = uiState.exclusiveAgreementError,
            errorMessage = stringResource(id = R.string.you_must_agree_you_own_content),
        )
        UploadCheckBoxView(
            modifier = Modifier.padding(
                top = paddingMedium,
                start = paddingMedium,
                end = paddingMedium,
            ),
            checked = uiState.termsOfServiceChecked,
            onToggleCheckedState = cameraUploadHandler::onTermsOfServiceCheckedChanged,
            hasError = uiState.termsOfServiceError,
            errorMessage = stringResource(id = R.string.you_must_agree_to_our_terms_of_services),
            annotatedTextWithActions = buildTermsAndConditionsStringWithActions(activityHandler),
            onAnnotatedTextClicked = activityHandler::onAnnotatedTextClicked
        )
        Text(
            text = stringResource(id = R.string.select_license).uppercase(),
            modifier = Modifier.padding(start = paddingMedium, top = paddingMedium),
            style = h6Heavy
        )
        UploadOptionField(
            title = stringResource(id = uiState.selectedUploadLicense.titleId),
            description = stringResource(id = uiState.selectedUploadLicense.subtitleId)
        ) { onSelectLicense() }
        Text(
            text = stringResource(id = R.string.visibility).uppercase(),
            modifier = Modifier.padding(start = paddingMedium, top = paddingMedium),
            style = h6Heavy
        )
        UploadOptionField(
            title = stringResource(id = uiState.selectedUploadVisibility.titleId),
            description = stringResource(id = uiState.selectedUploadVisibility.subtitleId)
        ) { onSelectVisibility() }
        Text(
            text = stringResource(id = R.string.schedule).uppercase(),
            modifier = Modifier.padding(start = paddingMedium, top = paddingMedium),
            style = h6Heavy
        )
        UploadOptionField(
            title = stringResource(id = uiState.selectedUploadSchedule.option.selectedTitleId),
            description = getScheduleDescription(uiState),
            enabled = uiState.selectedUploadVisibility == UploadVisibility.PUBLIC
        ) { onSelectSchedule() }
    }
}

@Composable
fun UploadOptionField(
    title: String,
    description: String,
    enabled: Boolean = true,
    onSelectOption: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(
                start = paddingMedium,
                end = paddingMedium,
                top = paddingSmall
            )
            .clip(RoundedCornerShape(radiusMedium))
            .background(MaterialTheme.colors.onSecondary)
            .conditional(enabled) {
                clickable { onSelectOption() }
            },
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = paddingMedium, vertical = paddingSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = minUploadStepTwoRowHeight),
                verticalArrangement = Arrangement.Center
            ) {
                if (enabled)
                    Text(
                        text = title,
                        color = MaterialTheme.colors.primary,
                        style = RumbleTypography.h4
                    )

                Text(
                    text = description,
                    color = MaterialTheme.colors.primaryVariant,
                    style = RumbleTypography.smallBody
                )
            }

            if (enabled)
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = stringResource(id = R.string.select),
                    tint = MaterialTheme.colors.primaryVariant
                )
        }
    }
}

@Composable
private fun getScheduleDescription(uiState: UserUploadUIState) =
    if (uiState.selectedUploadVisibility != UploadVisibility.PUBLIC) stringResource(
        id = R.string.available_for_public_videos_only
    ) else if (uiState.selectedUploadSchedule.option == UploadScheduleOption.NOW) stringResource(
        id = uiState.selectedUploadSchedule.option.selectedSubtitleId
    ) else "${uiState.selectedUploadSchedule.utcMillis.convertToDate(UPLOAD_DATE_PATTERN)}, ${
        uiState.selectedUploadSchedule.utcMillis.convertToDate(
            UPLOAD_TIME_PATTERN
        )
    }"

@Composable
private fun buildTermsAndConditionsStringWithActions(activityHandler: RumbleActivityHandler): AnnotatedStringWithActionsList {
    val actionList = mutableListOf<AnnotatedTextAction>()
    val text = buildAnnotatedString {
        pushStyle(SpanStyle(color = if (MaterialTheme.colors.isLight) enforcedDarkmo else enforcedWhite))
        append(stringResource(id = R.string.you_agree_to_our))
        append(" ")
        val urlColor = if (MaterialTheme.colors.isLight) wokeGreen else rumbleGreen
        withStyle(style = SpanStyle(color = urlColor)) {
            actionList.add(AnnotatedTextAction(RumbleConstants.TAG_URL) { uri ->
                activityHandler.onOpenWebView(uri)
            })
            val startIndexTerms = this.length
            append(stringResource(id = R.string.terms_of_service))
            val endIndexTerms = this.length
            addStringAnnotation(
                tag = RumbleConstants.TAG_URL,
                annotation = stringResource(id = R.string.rumble_terms_and_conditions_url),
                start = startIndexTerms,
                end = endIndexTerms
            )
        }
    }
    return AnnotatedStringWithActionsList(text, actionList)
}