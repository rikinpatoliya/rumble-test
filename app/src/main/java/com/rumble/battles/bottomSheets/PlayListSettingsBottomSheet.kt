package com.rumble.battles.bottomSheets

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomSheetHeader
import com.rumble.battles.commonViews.ChannelSelectionBottomSheet
import com.rumble.battles.commonViews.ChannelSelectionBottomSheetItem
import com.rumble.battles.commonViews.ErrorMessageView
import com.rumble.battles.commonViews.MainActionButton
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.commonViews.SelectChannelRowView
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.library.presentation.playlist.PlayListAction
import com.rumble.battles.library.presentation.playlist.PlayListSettingsBottomSheetDialog
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.library.domain.model.PlayListVisibility
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.fierceRed
import com.rumble.theme.imageMedium
import com.rumble.theme.imageXMedium
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayListSettingsBottomSheet(
    contentHandler: ContentHandler,
    playListAction: PlayListAction,
    videoId: Long? = null,
    onClose: () -> Unit
) {
    val userState by contentHandler.userUIState.collectAsStateWithLifecycle()
    val editPlayListScreenUIState by contentHandler.editPlayListState.collectAsStateWithLifecycle()
    val playListSettingsPopupState by contentHandler.playListSettingsState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )

    BackHandler {
        if (bottomSheetState.isVisible) {
            coroutineScope.launch { bottomSheetState.hide() }
        } else {
            onClose()
        }
    }

    RumbleModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            PlayListSettingsSelectionBottomSheet(
                contentHandler = contentHandler,
                context = context,
                popupState = playListSettingsPopupState,
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState
            )
        }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .statusBarsPadding()
                .navigationBarsPadding()
                .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
                .background(MaterialTheme.colors.background)
        ) {
            BottomSheetHeader(
                modifier = Modifier.padding(paddingMedium),
                title = context.getString(if (playListAction == PlayListAction.Edit) R.string.edit_playlist else R.string.new_playlist),
                onClose = contentHandler::onCancelPlayListSettings
            )
            PlayListTextInputField(
                initialValue = editPlayListScreenUIState.editPlayListEntity?.title ?: "",
                label = stringResource(id = R.string.name).uppercase(),
                maxLines = 1,
                maxCharacters = RumbleConstants.MAX_CHARACTERS_PLAYLIST_TITLE,
                onValueChange = { contentHandler.onTitleChanged(it) },
                hasError = editPlayListScreenUIState.titleError,
                errorMessage = stringResource(id = if (editPlayListScreenUIState.editPlayListEntity?.title.isNullOrEmpty()) R.string.error_message_playlist_title_empty else R.string.error_message_playlist_title_too_long)
            )
            PlayListTextInputField(
                initialValue = editPlayListScreenUIState.editPlayListEntity?.description ?: "",
                label = stringResource(id = R.string.description),
                maxLines = 4,
                maxCharacters = RumbleConstants.MAX_CHARACTERS_PLAYLIST_DESCRIPTION,
                onValueChange = { contentHandler.onDescriptionChanged(it) },
                hasError = editPlayListScreenUIState.descriptionError,
                errorMessage = stringResource(id = R.string.error_message_playlist_description_too_long)
            )
            Text(
                text = stringResource(id = R.string.channel).uppercase(),
                modifier = Modifier.padding(start = paddingMedium, top = paddingMedium),
                color = MaterialTheme.colors.primary,
                style = RumbleTypography.h6Heavy
            )
            editPlayListScreenUIState.editPlayListEntity?.let {
                SelectChannelRowView(
                    getPlayListOwnerTitle(it, userState.userUploadChannels),
                    getPlayListOwnerHandle(it, userState.userUploadChannels)
                ) {
                    contentHandler.onOpenChannelSelectionBottomSheet()
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }
            }
            Text(
                text = stringResource(id = R.string.visibility).uppercase(),
                modifier = Modifier.padding(start = paddingMedium, top = paddingMedium),
                color = MaterialTheme.colors.primary,
                style = RumbleTypography.h6Heavy
            )
            editPlayListScreenUIState.editPlayListEntity?.visibility?.let { visibility ->
                SelectChannelRowView(
                    title = context.getString(visibility.titleId),
                    description = context.getString(visibility.subtitleId)
                ) {
                    contentHandler.onOpenPlayListVisibilitySelectionBottomSheet()
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }
            }
            Spacer(modifier = Modifier.height(paddingMedium))
            Row(
                modifier = Modifier.padding(vertical = paddingMedium, horizontal = paddingMedium),
                horizontalArrangement = Arrangement.spacedBy(paddingMedium)
            ) {
                MainActionButton(
                    textModifier = Modifier.padding(
                        horizontal = paddingMedium,
                        vertical = paddingXSmall
                    ),
                    text = context.getString(R.string.cancel),
                    backgroundColor = MaterialTheme.colors.onSecondary,
                    onClick = contentHandler::onCancelPlayListSettings
                )
                MainActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    textModifier = Modifier.padding(vertical = paddingXSmall),
                    text = context.getString(if (playListAction == PlayListAction.Edit) R.string.save else R.string.create_and_save),
                    textColor = enforcedDarkmo,
                    onClick = { contentHandler.onSavePlayListSettings(playListAction, videoId) }
                )
            }
        }

    }
}

@Composable
private fun getPlayListOwnerTitle(
    editPlayListEntity: PlayListEntity,
    userUploadChannels: List<UserUploadChannelEntity>,
): String {
    var title = ""
    if (editPlayListEntity.playListOwnerId == editPlayListEntity.playListUserEntity.id) {
        title = editPlayListEntity.playListUserEntity.username
    } else {
        userUploadChannels.forEach {
            if (editPlayListEntity.playListOwnerId == it.id) {
                title = it.title
                return@forEach
            }
        }
    }
    return title
}

@Composable
private fun getPlayListOwnerHandle(
    editPlayListEntity: PlayListEntity,
    userUploadChannels: List<UserUploadChannelEntity>,
): String {
    var handle = ""
    if (editPlayListEntity.playListOwnerId == editPlayListEntity.playListUserEntity.id) {
        handle =
            stringResource(id = R.string.username_prefix) + editPlayListEntity.playListUserEntity.username
    } else {
        userUploadChannels.forEach {
            if (editPlayListEntity.playListOwnerId == it.id) {
                handle = stringResource(id = R.string.channel_name_prefix) + it.name
                return@forEach
            }
        }
    }
    return handle
}

@Composable
fun PlayListTextInputField(
    initialValue: String,
    label: String,
    maxLines: Int,
    maxCharacters: Int,
    onValueChange: (String) -> Unit = {},
    hasError: Boolean = false,
    errorMessage: String = "",
    errorMessageColor: Color = MaterialTheme.colors.secondary,
) {
    var text by remember { mutableStateOf(initialValue) }
    val defaultCharactersCountText = ""
    val defaultCharactersCountTextColor = MaterialTheme.colors.primaryVariant
    var characters by remember { mutableStateOf(defaultCharactersCountText) }
    var charactersColor by remember { mutableStateOf(defaultCharactersCountTextColor) }

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
                color = MaterialTheme.colors.primary,
                style = RumbleTypography.h6Heavy,
            )
            Text(
                text = characters,
                modifier = Modifier.align(Alignment.CenterEnd),
                color = charactersColor,
                style = RumbleTypography.tinyBody
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
                characters = getCharactersText(it, maxCharacters, defaultCharactersCountText)
                charactersColor =
                    getCharactersTextColor(it, maxCharacters, defaultCharactersCountTextColor)
            },
            colors = TextFieldDefaults.textFieldColors(
                textColor = MaterialTheme.colors.primary,
                backgroundColor = MaterialTheme.colors.onSurface,
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
                            characters =
                                getCharactersText("", maxCharacters, defaultCharactersCountText)
                            charactersColor =
                                getCharactersTextColor(
                                    "",
                                    maxCharacters,
                                    defaultCharactersCountTextColor
                                )
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

private fun getCharactersText(text: String, maxCharacters: Int, default: String): String {
    return when {
        text.isEmpty() -> default
        else -> "${text.count()}/${maxCharacters}"
    }
}

private fun getCharactersTextColor(text: String, maxCharacters: Int, default: Color): Color {
    return when {
        text.count() > maxCharacters -> fierceRed
        else -> default
    }
}

private fun buildChannelSelectionBottomSheetItems(
    playListEntity: PlayListEntity,
    userUploadChannels: List<UserUploadChannelEntity>,
    onPlayListOwnerSelected: (String) -> Unit,
    context: Context
): List<ChannelSelectionBottomSheetItem> {
    val result = mutableListOf<ChannelSelectionBottomSheetItem>()
    result.add(
        ChannelSelectionBottomSheetItem(
            imageUrl = playListEntity.playListUserEntity.thumbnail ?: "",
            text = playListEntity.playListUserEntity.username,
            subText = context.getString(R.string.username_prefix) + playListEntity.playListUserEntity.username,
            selected = playListEntity.playListOwnerId == playListEntity.playListUserEntity.id,
            action = { onPlayListOwnerSelected(playListEntity.playListUserEntity.id) }
        )
    )
    result.addAll(
        userUploadChannels.map { userUploadChannelEntity ->
            ChannelSelectionBottomSheetItem(
                imageUrl = userUploadChannelEntity.thumbnail ?: "",
                text = userUploadChannelEntity.title,
                subText = context.getString(R.string.channel_name_prefix) + userUploadChannelEntity.name,
                selected = playListEntity.playListOwnerId == userUploadChannelEntity.id,
                action = { onPlayListOwnerSelected(userUploadChannelEntity.id) }
            )
        }
    )
    return result
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayListSettingsSelectionBottomSheet(
    contentHandler: ContentHandler,
    popupState: PlayListSettingsBottomSheetDialog,
    context: Context,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
) {
    val userState by contentHandler.userUIState.collectAsStateWithLifecycle()
    when (popupState) {
        is PlayListSettingsBottomSheetDialog.PlayListVisibilitySelection ->
            PlayListVisibilitySelectionBottomSheet(
                playListEntity = popupState.playListEntity,
                onVisibilitySelected = contentHandler::onPlayListVisibilityChanged,
                context = context,
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState
            )

        is PlayListSettingsBottomSheetDialog.PlayListChannelSelection ->
            ChannelSelectionBottomSheet(
                title = stringResource(id = R.string.select_channel),
                sheetItems = buildChannelSelectionBottomSheetItems(
                    popupState.playListEntity,
                    userState.userUploadChannels,
                    onPlayListOwnerSelected = contentHandler::onPlayListOwnerChanged,
                    context
                ),
                onCancel = { coroutineScope.launch { bottomSheetState.hide() } }
            )

        else -> {}
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayListVisibilitySelectionBottomSheet(
    playListEntity: PlayListEntity,
    onVisibilitySelected: (PlayListVisibility) -> Unit,
    context: Context,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
            .background(MaterialTheme.colors.background)
    ) {
        BottomSheetHeader(
            modifier = Modifier.padding(paddingMedium),
            title = context.getString(R.string.select_visibility),
            onClose = {
                coroutineScope.launch { bottomSheetState.hide() }
            }
        )
        LazyColumn {
            itemsIndexed(PlayListVisibility.values()) { index, playListVisibility ->
                PlayListSettingsSelectionRow(
                    iconId = getPlayListVisibilityIcon(playListVisibility),
                    imageUrl = null,
                    title = stringResource(id = playListVisibility.titleId),
                    subTitle = stringResource(id = playListVisibility.subtitleId),
                    selected = playListEntity.visibility == playListVisibility,
                    onSelected = { onVisibilitySelected(playListVisibility) }
                )
                if (index != PlayListVisibility.values().size - 1) {
                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(paddingLarge))
            }
        }
        Spacer(modifier = Modifier.height(paddingMedium))
    }
}

private fun getPlayListVisibilityIcon(playListVisibility: PlayListVisibility): Int {
    return when (playListVisibility) {
        PlayListVisibility.PUBLIC -> R.drawable.ic_globe
        PlayListVisibility.UNLISTED -> R.drawable.ic_link
        PlayListVisibility.PRIVATE -> R.drawable.ic_lock
    }
}

@Composable
fun PlayListSettingsSelectionRow(
    iconId: Int?,
    imageUrl: String?,
    title: String,
    subTitle: String,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
    ) {
        val (profileIcon, info, radioIcon) = createRefs()
        Box(modifier = Modifier.constrainAs(profileIcon) {
            start.linkTo(parent.start)
            end.linkTo(info.start)
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
        }) {
            if (iconId != null) {
                Box(
                    modifier = Modifier
                        .padding(start = paddingMedium)
                        .clip(CircleShape)
                        .size(imageXMedium)
                        .background(MaterialTheme.colors.onSurface),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier
                            .size(imageXXSmall),
                        painter = painterResource(id = iconId),
                        contentDescription = title,
                        tint = MaterialTheme.colors.primary
                    )
                }
            } else {
                ProfileImageComponent(
                    modifier = Modifier
                        .padding(start = paddingMedium),
                    profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXMediumStyle(),
                    userName = title,
                    userPicture = imageUrl ?: ""
                )
            }
        }
        Column(
            modifier = Modifier
                .constrainAs(info) {
                    start.linkTo(profileIcon.end)
                    end.linkTo(radioIcon.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(vertical = paddingMedium, horizontal = paddingSmall)
        ) {
            Text(
                text = title,
                style = RumbleTypography.h4,
                color = MaterialTheme.colors.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
            Text(
                text = subTitle,
                style = RumbleTypography.h6Light,
                color = MaterialTheme.colors.secondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }
        Box(
            modifier = Modifier
                .constrainAs(radioIcon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .padding(end = paddingMedium)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_radio),
                contentDescription = stringResource(
                    id = R.string.select
                ),
                tint = if (selected) MaterialTheme.colors.secondary else MaterialTheme.colors.onSecondary
            )
            if (selected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_radio_dot),
                    contentDescription = stringResource(
                        id = R.string.select
                    ),
                    tint = rumbleGreen
                )
            }
        }
    }
}
