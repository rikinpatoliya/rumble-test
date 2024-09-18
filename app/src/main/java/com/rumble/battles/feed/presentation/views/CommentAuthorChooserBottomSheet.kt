package com.rumble.battles.feed.presentation.views

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.ChannelSelectionBottomSheet
import com.rumble.battles.commonViews.ChannelSelectionBottomSheetItem
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentAuthorChooserBottomSheet(
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    selectedAuthor: CommentAuthorEntity?,
    channels: List<CommentAuthorEntity>,
    onCommentAuthorChannelSelected: (CommentAuthorEntity) -> Unit,
) {
    ChannelSelectionBottomSheet(
        title = stringResource(id = R.string.chat_as),
        sheetItems = channels.mapIndexed { index, it ->

            ChannelSelectionBottomSheetItem(
                imageUrl = it.thumbnail ?: "",
                text = it.title,
                selected = it == selectedAuthor,
                action = {
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        onCommentAuthorChannelSelected(it)
                    }
                }
            )

        },
        onCancel = { coroutineScope.launch { bottomSheetState.hide() } }
    )
}