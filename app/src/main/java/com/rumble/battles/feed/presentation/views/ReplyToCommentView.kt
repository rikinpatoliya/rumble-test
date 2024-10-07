package com.rumble.battles.feed.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.theme.paddingMedium

@Composable
fun ReplyToCommentView(
    modifier: Modifier = Modifier,
    commentEntity: CommentEntity?,
    activityHandler: RumbleActivityHandler,
    hasPremiumRestriction: Boolean,
    comment: String,
    userName: String = "",
    userPicture: String = "",
    withHeader: Boolean = true,
    onChange: (String) -> Unit = {},
    onClose: () -> Unit = {},
    onReply: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    val configuration = LocalConfiguration.current
    LaunchedEffect(configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            focusRequester.requestFocus()
        }
    }

    Column(modifier = modifier) {
        if (withHeader) {
            CloseAddCommentView(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(id = R.string.reply_to),
                onClose = onClose
            )
        }

        commentEntity?.let {
            CommentView(
                modifier = Modifier
                    .padding(paddingMedium)
                    .weight(1f),
                commentEntity = commentEntity,
                activityHandler = activityHandler,
                hasPremiumRestriction = hasPremiumRestriction,
                showReplies = false,
                onReplies = {}
            )
        }

        AddCommentView(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            comment = comment,
            placeHolder = stringResource(id = R.string.add_reply),
            userName = userName,
            userPicture = userPicture,
            onChange = { onChange(it) },
            onSubmit = onReply
        )
    }
}
