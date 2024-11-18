package com.rumble.battles.camera.presentation

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rumble.battles.R
import com.rumble.battles.UploadForm1Tag
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.ErrorMessageView
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h6Heavy
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.borderSmall
import com.rumble.theme.channelActionsButtonWidth
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.fierceRed
import com.rumble.theme.imageHeightXXLarge
import com.rumble.theme.imageMedium
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXSmall10
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.radiusXXXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.uploadSelectedThumbnailHeight
import com.rumble.theme.uploadSelectedThumbnailWidth
import com.rumble.theme.uploadThumbnailHeight
import com.rumble.theme.uploadThumbnailWidth
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.MAX_CHARACTERS_UPLOAD_DESCRIPTION
import com.rumble.utils.RumbleConstants.MAX_CHARACTERS_UPLOAD_TITLE
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.dashedBorder

@Composable
fun CameraUploadStepOneScreen(
    cameraUploadHandler: CameraUploadHandler,
    onSelectChannel: () -> Unit,
    onSelectCategory: (isPrimary: Boolean) -> Unit,
    onNextStep: () -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by cameraUploadHandler.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        cameraUploadHandler.onUploadImageChanged(uri)
    }

    LaunchedEffect(Unit) {
        if (uiState.generateUIThumbs)
            cameraUploadHandler.generateUIThumbnails()
    }

    Column(
        modifier = Modifier
            .testTag(UploadForm1Tag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
            .imePadding()
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.info),
            modifier = Modifier.fillMaxWidth(),
            onBackClick = { cameraUploadHandler.onBackClicked(onBackClick) },
            extraContent = {
                ActionButton(
                    modifier = Modifier
                        .width(channelActionsButtonWidth)
                        .padding(end = paddingMedium),
                    text = stringResource(id = R.string.next),
                    textModifier = Modifier.padding(
                        top = paddingXXXSmall,
                        bottom = paddingXXXSmall,
                    ),
                    textColor = enforcedDarkmo
                ) { cameraUploadHandler.onNextClicked(onNextStep) }
            }
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = paddingXSmall)
        ) {
            Text(
                text = stringResource(id = R.string.thumbnail).uppercase(),
                modifier = Modifier.padding(start = paddingMedium, top = paddingMedium),
                style = h6Heavy
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeightXXLarge)
                    .padding(top = paddingXSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(paddingXSmall)
            ) {
                item {
                    Spacer(modifier = Modifier.width(paddingMedium))
                    SelectUploadThumbnailView(
                        uri = uiState.selectedUploadImage,
                        onClick = {
                            launcher.launch(RumbleConstants.ACTIVITY_RESULT_CONTRACT_IMAGE_INPUT_TYPE)
                        }
                    )
                }
                items(uiState.thumbnails) {
                    UploadThumbnailView(
                        bitmap = it,
                        selected = uiState.selectedThumbnail == it,
                        onClick = {
                            cameraUploadHandler.onSelectUploadThumbnail(it)
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.width(paddingMedium))
                }
            }
            UploadTextInputField(
                initialValue = uiState.title,
                label = stringResource(id = R.string.title),
                maxLines = 1,
                maxCharacters = MAX_CHARACTERS_UPLOAD_TITLE,
                onValueChange = { cameraUploadHandler.onTitleChanged(it) },
                hasError = uiState.titleError || uiState.titleEmptyError,
                errorMessage = stringResource(
                    id = if (uiState.titleEmptyError) {
                        R.string.error_message_video_title_empty
                    } else {
                        R.string.error_message_video_title_too_long
                    }
                ),
                optional = false
            )
            UploadTextInputField(
                initialValue = uiState.description,
                label = stringResource(id = R.string.description),
                maxLines = 4,
                maxCharacters = MAX_CHARACTERS_UPLOAD_DESCRIPTION,
                onValueChange = { cameraUploadHandler.onDescriptionChanged(it) },
                hasError = uiState.descriptionError,
                errorMessage = stringResource(id = R.string.error_message_description_too_long),
                optional = true
            )
            Text(
                text = stringResource(id = R.string.select_a_channel).uppercase(),
                modifier = Modifier.padding(start = paddingMedium, top = paddingMedium),
                style = h6Heavy
            )
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = paddingMedium,
                        end = paddingMedium,
                        top = paddingXXXSmall
                    )
                    .clip(RoundedCornerShape(radiusMedium))
                    .background(MaterialTheme.colors.onSecondary)
                    .clickable { onSelectChannel() },
            ) {
                val (profileImage, title, icon) = createRefs()
                ProfileImageComponent(
                    modifier = Modifier
                        .padding(
                            start = paddingMedium,
                            top = paddingSmall,
                            bottom = paddingSmall,
                        )
                        .constrainAs(profileImage) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                    userName = uiState.selectedUploadChannel.title,
                    userPicture = uiState.selectedUploadChannel.thumbnail ?: ""
                )
                Text(
                    text = uiState.selectedUploadChannel.title,
                    modifier = Modifier
                        .padding(start = paddingMedium)
                        .constrainAs(title) {
                            start.linkTo(profileImage.end)
                            end.linkTo(icon.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                        },
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = RumbleTypography.body1Bold,
                    textAlign = TextAlign.Start
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = stringResource(id = R.string.select_a_channel),
                    modifier = Modifier
                        .padding(end = paddingMedium)
                        .constrainAs(icon) {
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    tint = MaterialTheme.colors.primaryVariant
                )
            }

            Row(
                modifier = Modifier
                    .padding(
                        start = paddingMedium,
                        top = paddingMedium,
                        end = paddingMedium,
                        bottom = paddingXXXSmall
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(id = R.string.categories).uppercase(),
                    style = h6Heavy
                )

                Text(
                    text = stringResource(R.string.required),
                    color = MaterialTheme.colors.primaryVariant,
                    style = tinyBody
                )
            }

            CategorySelection(
                selectedCategoryName = uiState.selectedPrimaryCategory?.title,
                categoryPlaceholder = stringResource(id = R.string.primary_category),
                iconContentDescription = stringResource(id = R.string.select_primary_category),
                onClick = { onSelectCategory(true) }
            )

            if (uiState.primaryCategoryError) {
                ErrorMessageView(
                    modifier = Modifier
                        .padding(
                            top = paddingXSmall,
                            start = paddingMedium,
                            end = paddingMedium
                        )
                        .fillMaxWidth(),
                    errorMessage = stringResource(R.string.primary_category_required_error),
                    textColor = MaterialTheme.colors.secondary
                )
            }

            Spacer(modifier = Modifier.height(paddingXSmall))

            CategorySelection(
                selectedCategoryName = uiState.selectedSecondaryCategory?.title,
                categoryPlaceholder = stringResource(id = R.string.secondary_category),
                iconContentDescription = stringResource(id = R.string.select_secondary_category),
                onClick = { onSelectCategory(false) }
            )
        }
    }
}

@Composable
fun UploadTextInputField(
    initialValue: String,
    label: String,
    maxLines: Int,
    maxCharacters: Int,
    onValueChange: (String) -> Unit = {},
    hasError: Boolean = false,
    errorMessage: String = "",
    errorMessageColor: Color = MaterialTheme.colors.secondary,
    optional: Boolean = false
) {
    var text by remember { mutableStateOf(initialValue) }
    val optionalText = stringResource(id = R.string.optional)
    val defaultCharactersCountTextColor = MaterialTheme.colors.primaryVariant
    val characters by remember(text, maxCharacters, optional, optionalText) {
        derivedStateOf { getCharactersText(text, maxCharacters, optional, optionalText) }
    }
    val charactersColor by remember(text, maxCharacters) {
        derivedStateOf {
            getCharactersTextColor(
                text,
                maxCharacters,
                defaultCharactersCountTextColor
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(
                start = paddingMedium,
                end = paddingMedium
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingMedium,
                ),
        ) {
            Text(
                text = label.uppercase(),
                modifier = Modifier.align(Alignment.CenterStart),
                style = h6Heavy,
            )
            Text(
                text = characters,
                modifier = Modifier.align(Alignment.CenterEnd),
                color = charactersColor,
                style = tinyBody
            )
        }
        OutlinedTextField(
            value = text,
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = RumbleTypography.body1,
            shape = RoundedCornerShape(radiusSmall),
            onValueChange = {
                onValueChange(it)
                text = it
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.onSecondary,
                cursorColor = MaterialTheme.colors.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = fierceRed
            ),
            isError = hasError,
            trailingIcon = {
                if (text.isNotBlank()) {
                    IconButton(
                        onClick = {
                            onValueChange("")
                            text = ""
                        },
                        modifier = Modifier.size(imageMedium)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear_text),
                            contentDescription = stringResource(id = R.string.clear_text),
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                }
            },
            maxLines = maxLines,
            minLines = maxLines,
        )
        if (hasError) {
            ErrorMessageView(
                modifier = Modifier
                    .padding(top = paddingXSmall)
                    .fillMaxWidth(),
                errorMessage = errorMessage,
                textColor = errorMessageColor
            )
        }
    }

}

@Composable
fun SelectUploadThumbnailView(
    modifier: Modifier = Modifier,
    uri: Uri? = null,
    onClick: () -> Unit
) {
    val borderColor = if (uri == null) MaterialTheme.colors.primaryVariant else rumbleGreen
    Box(modifier = modifier
        .width(if (uri != null) uploadSelectedThumbnailWidth else uploadThumbnailWidth)
        .height(if (uri != null) uploadSelectedThumbnailHeight else uploadThumbnailHeight)
        .clip(RoundedCornerShape(radiusMedium))
        .background(color = if (uri == null) MaterialTheme.colors.onSecondary else enforcedDarkmo)
        .conditional(uri != null) {
            this.border(
                borderSmall,
                color = borderColor,
                shape = RoundedCornerShape(radiusMedium)
            )
        }
        .conditional(uri == null) {
            this.dashedBorder(
                strokeWidth = radiusXXXXSmall,
                color = borderColor,
                cornerRadiusDp = radiusMedium
            )

        }
        .clickable { onClick() }
    ) {
        uri?.let { imageUri ->
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = imageUri,
                contentDescription = "",
                contentScale = ContentScale.Fit
            )
        } ?: run {
            Image(
                painter = painterResource(id = R.drawable.ic_upload_cloud),
                contentDescription = stringResource(id = R.string.thumbnail),
                modifier = Modifier.align(Alignment.Center),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant)
            )
        }
    }
}

@Composable
fun UploadThumbnailView(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(modifier = modifier
        .width(if (selected) uploadSelectedThumbnailWidth else uploadThumbnailWidth)
        .height(if (selected) uploadSelectedThumbnailHeight else uploadThumbnailHeight)
        .clip(RoundedCornerShape(radiusMedium))
        .conditional(selected) {
            this.border(
                borderSmall,
                color = rumbleGreen,
                shape = RoundedCornerShape(radiusMedium)
            )
        }
        .background(color = enforcedDarkmo)
        .clickable { onClick() }
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = bitmap,
            contentDescription = "",
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun CategorySelection(
    selectedCategoryName: String?,
    categoryPlaceholder: String,
    iconContentDescription: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingMedium)
            .clip(RoundedCornerShape(radiusMedium))
            .background(MaterialTheme.colors.onSecondary)
            .clickable { onClick() }
            .padding(horizontal = paddingMedium, vertical = paddingSmall),
        horizontalArrangement = Arrangement.spacedBy(paddingXSmall10),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedCategoryName ?: categoryPlaceholder,
            modifier = Modifier
                .weight(weight = 1f)
                .alpha(if (selectedCategoryName != null) 1f else 0.5f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = if (selectedCategoryName != null) {
                RumbleTypography.body1Bold
            } else {
                RumbleTypography.body1
            }
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = iconContentDescription,
            tint = MaterialTheme.colors.primaryVariant
        )
    }
}

private fun getCharactersText(
    text: String,
    maxCharacters: Int,
    optional: Boolean,
    optionalText: String
): String {
    return when {
        optional && text.isEmpty() -> optionalText
        else -> "${text.count()}/${maxCharacters}"
    }
}

private fun getCharactersTextColor(text: String, maxCharacters: Int, default: Color): Color {
    return when {
        text.count() > maxCharacters -> fierceRed
        else -> default
    }
}
