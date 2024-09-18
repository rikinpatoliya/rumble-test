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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.commonViews.ErrorMessageView
import com.rumble.battles.commonViews.MainActionButton
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RumbleModalBottomSheetLayout
import com.rumble.battles.library.presentation.playlist.EditPlayListHandler
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
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayListSettingsBottomSheet(
    playListHandler: EditPlayListHandler,
    playListAction: PlayListAction,
    videoId: Long? = null,
    onClose: () -> Unit
) {
    val state by playListHandler.editPlayListState.collectAsStateWithLifecycle()
    val popupState by playListHandler.playListSettingsState.collectAsStateWithLifecycle()
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
                playListHandler = playListHandler,
                context = context,
                popupState = popupState,
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
            PlayListSettingsBottomSheetHeader(
                title = context.getString(if (playListAction == PlayListAction.Edit) R.string.edit_playlist else R.string.new_playlist),
                onClose = playListHandler::onCancelPlayListSettings
            )
            PlayListTextInputField(
                initialValue = state.editPlayListEntity?.title ?: "",
                label = stringResource(id = R.string.name).uppercase(),
                maxLines = 1,
                maxCharacters = RumbleConstants.MAX_CHARACTERS_PLAYLIST_TITLE,
                onValueChange = { playListHandler.onTitleChanged(it) },
                hasError = state.titleError,
                errorMessage = stringResource(id = if (state.editPlayListEntity?.title.isNullOrEmpty()) R.string.error_message_playlist_title_empty else R.string.error_message_playlist_title_too_long)
            )
            PlayListTextInputField(
                initialValue = state.editPlayListEntity?.description ?: "",
                label = stringResource(id = R.string.description),
                maxLines = 4,
                maxCharacters = RumbleConstants.MAX_CHARACTERS_PLAYLIST_DESCRIPTION,
                onValueChange = { playListHandler.onDescriptionChanged(it) },
                hasError = state.descriptionError,
                errorMessage = stringResource(id = R.string.error_message_playlist_description_too_long)
            )
            Text(
                text = stringResource(id = R.string.channel).uppercase(),
                modifier = Modifier.padding(start = paddingMedium, top = paddingMedium),
                color = MaterialTheme.colors.primary,
                style = RumbleTypography.h6Heavy
            )
            state.editPlayListEntity?.let {
                PlayListSettingsSelectableRow(
                    getPlayListOwnerTitle(it, state.userUploadChannels),
                    getPlayListOwnerHandle(it, state.userUploadChannels)
                ) {
                    playListHandler.onOpenChannelSelectionBottomSheet()
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
            state.editPlayListEntity?.visibility?.let { visibility ->
                PlayListSettingsSelectableRow(
                    title = context.getString(visibility.titleId),
                    description = context.getString(visibility.subtitleId)
                ) {
                    playListHandler.onOpenPlayListVisibilitySelectionBottomSheet()
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
                    onClick = playListHandler::onCancelPlayListSettings
                )
                MainActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    textModifier = Modifier.padding(vertical = paddingXSmall),
                    text = context.getString(if (playListAction == PlayListAction.Edit) R.string.save else R.string.create_and_save),
                    textColor = enforcedDarkmo,
                    onClick = { playListHandler.onSavePlayListSettings(playListAction, videoId) }
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
        handle = stringResource(id = R.string.username_prefix) + editPlayListEntity.playListUserEntity.username
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
private fun PlayListSettingsBottomSheetHeader(
    title: String,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colors.primary,
            style = RumbleTypography.h3
        )
        Spacer(modifier = Modifier.weight(1F))
        Icon(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = stringResource(id = R.string.close),
            modifier = Modifier
                .clickable { onClose() },
            tint = MaterialTheme.colors.primary
        )
    }
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

@Composable
fun PlayListSettingsSelectableRow(
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = paddingMedium,
                end = paddingMedium,
                top = paddingXXXSmall
            )
            .clip(RoundedCornerShape(radiusMedium))
            .background(MaterialTheme.colors.onSurface)
            .clickable { onClick() },
    ) {
        val (text, icon) = createRefs()
        Column(
            modifier = Modifier
                .padding(start = paddingMedium, top = paddingMedium, bottom = paddingMedium)
                .constrainAs(text) {
                    start.linkTo(parent.start)
                    end.linkTo(icon.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = title,
                color = MaterialTheme.colors.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = RumbleTypography.h4,
                textAlign = TextAlign.Start
            )
            Text(
                text = description,
                color = MaterialTheme.colors.primaryVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = RumbleTypography.smallBody,
                textAlign = TextAlign.Start
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_chevron_down),
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayListSettingsSelectionBottomSheet(
    playListHandler: EditPlayListHandler,
    popupState: PlayListSettingsBottomSheetDialog,
    context: Context,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
) {
    when (popupState) {
        is PlayListSettingsBottomSheetDialog.PlayListVisibilitySelection ->
            PlayListVisibilitySelectionBottomSheet(
                playListEntity = popupState.playListEntity,
                onVisibilitySelected = playListHandler::onPlayListVisibilityChanged,
                context = context,
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState
            )

        is PlayListSettingsBottomSheetDialog.PlayListChannelSelection ->
            PlayListChannelSelectionBottomSheet(
                playListEntity = popupState.playListEntity,
                userUploadChannels = popupState.userUploadChannels,
                onPlayListOwnerSelected = playListHandler::onPlayListOwnerChanged,
                context = context,
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState
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
        PlayListSettingsBottomSheetHeader(
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PlayListChannelSelectionBottomSheet(
    playListEntity: PlayListEntity,
    userUploadChannels: List<UserUploadChannelEntity>,
    onPlayListOwnerSelected: (String) -> Unit,
    context: Context,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
            .background(MaterialTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
                .background(MaterialTheme.colors.background)
        ) {
            PlayListSettingsBottomSheetHeader(
                title = context.getString(R.string.select_channel),
                onClose = {
                    coroutineScope.launch { bottomSheetState.hide() }
                }
            )
            LazyColumn(
                state = rememberLazyListState(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    PlayListSettingsSelectionRow(
                        iconId = null,
                        imageUrl = playListEntity.playListUserEntity.thumbnail,
                        title = playListEntity.playListUserEntity.username,
                        subTitle = stringResource(id = R.string.username_prefix) + playListEntity.playListUserEntity.username,
                        selected = playListEntity.playListOwnerId == playListEntity.playListUserEntity.id,
                        onSelected = { onPlayListOwnerSelected(playListEntity.playListUserEntity.id) }
                    )
                }
                if (userUploadChannels.isNotEmpty()) {
                    item {
                        Divider(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.onSurface
                        )
                    }
                    itemsIndexed(userUploadChannels) {index, userChannelEntity ->
                        PlayListSettingsSelectionRow(
                            iconId = null,
                            imageUrl = userChannelEntity.thumbnail,
                            title = userChannelEntity.title,
                            subTitle = stringResource(id = R.string.channel_name_prefix) + userChannelEntity.name,
                            selected = playListEntity.playListOwnerId == userChannelEntity.id,
                            onSelected = { onPlayListOwnerSelected(userChannelEntity.id) }
                        )
                        if (index != userUploadChannels.size - 1) {
                            Divider(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(paddingMedium))
                }
            }
        }
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
