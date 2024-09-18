package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.comments.CommentsHandler
import com.rumble.battles.feed.presentation.views.AddCommentView
import com.rumble.battles.feed.presentation.views.CloseAddCommentView
import com.rumble.battles.feed.presentation.views.CommentView
import com.rumble.battles.feed.presentation.views.ReplyToCommentView
import com.rumble.theme.RumbleTypography
import com.rumble.theme.minDefaultEmptyViewHeight
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.wokeGreen
import com.rumble.utils.extension.shortString

private const val TAG = "CommentsPopupBottomSheet"

@Composable
internal fun CommentsPopupBottomSheet(
    modifier: Modifier = Modifier,
    handler: CommentsHandler,
    listState: LazyListState,
    userName: String,
    userPicture: String,
    onHideBottomSheet: () -> Unit
) {
    val state by handler.commentsUIState.collectAsStateWithLifecycle()
    val isKeyboardVisible by keyboardAsState()

    Column(
        modifier = modifier
            .fillMaxHeight(if (isKeyboardVisible) 1F else 0.7F)
            .clip(RoundedCornerShape(topStart = radiusMedium, topEnd = radiusMedium))
            .background(color = MaterialTheme.colors.background)
    ) {

        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (header, lazyColumn, addComment) = createRefs()

            if (isKeyboardVisible) {
                CloseAddCommentView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = paddingMedium)
                        .constrainAs(header) {
                            top.linkTo(parent.top)
                        },
                    title = stringResource(id = R.string.add_comment_close)
                ) {
                    handler.onCloseAddComment()
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = paddingMedium,
                            top = paddingMedium,
                            end = paddingMedium,
                            bottom = paddingXSmall
                        )
                        .constrainAs(header) {
                            top.linkTo(parent.top)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TitleWithBoxedCount(
                        title = stringResource(id = R.string.comments),
                        count = state.commentNumber.shortString()
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    IconButton(
                        onClick = onHideBottomSheet,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = stringResource(id = R.string.close),
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }
            }

            state.commentToReply?.let {
                ReplyToCommentView(
                    modifier = Modifier
                        .imePadding()
                        .fillMaxWidth(),
                    commentEntity = it,
                    hasPremiumRestriction = state.hasPremiumRestriction,
                    comment = state.currentComment,
                    userName = userName,
                    userPicture = userPicture,
                    onChange = handler::onCommentChanged,
                    onClose = handler::onCloseAddComment,
                    onReply = handler::onSubmitComment
                )
            } ?: run {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(lazyColumn) {
                            top.linkTo(header.bottom)
                            bottom.linkTo(addComment.top)
                            height = Dimension.fillToConstraints
                        },
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    state.commentList?.let { commentEntities ->
                        commentEntities.forEach {
                            item {
                                CommentView(
                                    modifier = Modifier.padding(
                                        start = paddingXSmall,
                                        end = paddingXSmall,
                                        top = paddingSmall
                                    ),
                                    commentEntity = it,
                                    hasPremiumRestriction = state.hasPremiumRestriction,
                                    onReplies = handler::onReplies,
                                    onDelete = handler::onDelete,
                                    onReport = handler::onReport,
                                    onReply = handler::onReplyToComment,
                                    onLike = handler::onLikeComment
                                )
                            }
                        }
                    }
                    if (state.commentsDisabled) {
                        item {
                            EmptyView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = minDefaultEmptyViewHeight)
                                    .padding(
                                        start = paddingMedium,
                                        end = paddingMedium,
                                        bottom = paddingMedium
                                    ),
                                iconId = R.drawable.ic_lock,
                                title = stringResource(id = R.string.comments_disabled)
                            )
                        }
                    } else {
                        if (state.commentList.isNullOrEmpty()) {
                            item {
                                EmptyView(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = minDefaultEmptyViewHeight)
                                        .padding(
                                            start = paddingMedium,
                                            end = paddingMedium,
                                            bottom = paddingMedium
                                        ),
                                    title = stringResource(id = R.string.no_comments_yet),
                                    text = stringResource(id = R.string.be_first_comment)
                                )
                            }
                        }
                    }
                }
            }

            if (state.commentsDisabled.not() && state.commentToReply == null) {
                if (state.userProfile?.validated == true) {
                    AddCommentView(
                        modifier = Modifier
                            .imePadding()
                            .fillMaxWidth()
                            .constrainAs(addComment) {
                                bottom.linkTo(parent.bottom)
                            },
                        comment = state.currentComment,
                        placeHolder = stringResource(id = R.string.add_comment),
                        userName = userName,
                        userPicture = userPicture,
                        onChange = handler::onCommentChanged,
                        onSubmit = handler::onSubmitComment
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .clickable { handler.onVerifyEmailForComments() }
                            .padding(vertical = paddingMedium)
                            .fillMaxWidth()
                            .constrainAs(addComment) {
                                bottom.linkTo(parent.bottom)
                            },
                        text = stringResource(id = R.string.verify_your_email_comments),
                        style = RumbleTypography.h4Underlined,
                        color = wokeGreen,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}