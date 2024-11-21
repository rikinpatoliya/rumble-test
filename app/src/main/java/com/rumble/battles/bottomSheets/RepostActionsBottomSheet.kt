package com.rumble.battles.bottomSheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.commonViews.RumbleBottomSheet
import com.rumble.domain.repost.domain.domainmodel.RepostEntity

@Composable
fun RepostActionsBottomSheet(
    repost: RepostEntity,
    currentUserId: String,
    onDeleteRepost: (RepostEntity) -> Unit = {},
    onReportRepost: (RepostEntity) -> Unit = {},
    onHideBottomSheet: () -> Unit
) {
    RumbleBottomSheet(
        sheetItems = mutableListOf(
            BottomSheetItem(
                imageResource = R.drawable.ic_flag,
                text = stringResource(R.string.report_repost),
                action = {
                    onReportRepost(repost)
                }
            )
        ).apply {
            if (currentUserId == repost.user.id || currentUserId == repost.channel?.channelId) {
                add(
                    0,
                    BottomSheetItem(
                        imageResource = R.drawable.ic_delete_message,
                        text = stringResource(R.string.undo_repost),
                        action = { onDeleteRepost(repost) })
                )
            }
        },
        onCancel = onHideBottomSheet
    )
}