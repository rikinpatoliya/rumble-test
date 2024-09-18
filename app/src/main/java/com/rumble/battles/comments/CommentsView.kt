package com.rumble.battles.comments

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.commonViews.DrawerCloseIndicatorView
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.feed.presentation.videodetails.VideoDetailsHandler
import com.rumble.battles.feed.presentation.views.AddCommentView
import com.rumble.battles.feed.presentation.views.CloseAddCommentView
import com.rumble.battles.feed.presentation.views.CommentView
import com.rumble.battles.feed.presentation.views.GoPremiumToCharOrCommentView
import com.rumble.battles.feed.presentation.views.ReplyToCommentView
import com.rumble.domain.sort.CommentSortOrder
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h4Underlined
import com.rumble.theme.borderXXSmall
import com.rumble.theme.darkGreen
import com.rumble.theme.minDefaultEmptyViewHeight
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusLarge
import com.rumble.theme.wokeGreen
import com.rumble.utils.extension.shortString

@Composable
fun CommentsView(
    modifier: Modifier = Modifier,
    handler: VideoDetailsHandler,
) {
    val state by handler.state
    val commentsList = handler.getSortedCommentsList(handler.state.value.videoEntity?.commentList)
    val userName by handler.userNameFlow.collectAsStateWithLifecycle(initialValue = "")
    val userPicture by handler.userPictureFlow.collectAsStateWithLifecycle(initialValue = "")
    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .background(MaterialTheme.colors.onPrimary)
    ) {
        DrawerCloseIndicatorView(
            modifier = Modifier.padding(
                top = paddingXSmall,
                bottom = paddingXSmall
            )
        )
        if (state.commentToReply != null) {
            CloseAddCommentView(
                modifier = Modifier
                    .fillMaxWidth(),
                title = stringResource(id = R.string.reply_to)
            ) {
                handler.onCloseAddComment()
            }
        } else {
            CommentsHeaderView(
                modifier = Modifier.fillMaxWidth(),
                commentsNumber = state.videoEntity?.commentNumber ?: 0,
                commentsSortType = state.commentsSortOrder,
                onChangeCommentSortOrder = handler::onChangeCommentSortOrder,
                onClose = { handler.onCloseComments() }
            )
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.secondaryVariant
        )

        if (state.commentsDisabled) {
            EmptyView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = minDefaultEmptyViewHeight)
                    .padding(paddingMedium),
                iconId = R.drawable.ic_lock,
                title = stringResource(id = R.string.comments_disabled)
            )
        } else if (state.videoEntity?.commentList.isNullOrEmpty()) {
            EmptyView(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = minDefaultEmptyViewHeight)
                    .padding(paddingMedium),
                title = stringResource(id = R.string.no_comments_yet),
                text = stringResource(id = R.string.be_first_comment)
            )
        } else {
            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier
                        .padding(
                            start = paddingSmall,
                            end = paddingMedium,
                            bottom = paddingXXXXSmall
                        ),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(paddingXXXXSmall)
                ) {
                    commentsList?.let { commentEntities ->
                        commentEntities.forEach {
                            item {
                                CommentView(
                                    modifier = Modifier.padding(
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
                }
            }
        }

        if (state.commentToReply != null) {
            ReplyToCommentView(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
                commentEntity = state.commentToReply,
                hasPremiumRestriction = state.hasPremiumRestriction,
                comment = state.currentComment,
                userName = userName,
                userPicture = userPicture,
                withHeader = false,
                onChange = handler::onCommentChanged,
                onClose = handler::onCloseAddComment,
                onReply = handler::onSubmitComment
            )
        } else if (state.isLoggedIn.not()) {
            Text(
                modifier = Modifier
                    .clickable { handler.onSignIn() }
                    .padding(vertical = paddingMedium)
                    .fillMaxWidth(),
                text = stringResource(id = R.string.sign_in_to_comment),
                style = h4Underlined,
                color = darkGreen,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        } else if (state.userProfile?.validated == true && state.hasPremiumRestriction) {
            GoPremiumToCharOrCommentView(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
                text = stringResource(id = R.string.go_premium_to_comment),
                onClick = { handler.onSubscribeToPremium() }
            )
        } else if (state.userProfile?.validated == true) {
            AddCommentView(
                modifier = Modifier
                    .imePadding()
                    .fillMaxWidth(),
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
                    .fillMaxWidth(),
                text = stringResource(id = R.string.verify_your_email_comments),
                style = h4Underlined,
                color = wokeGreen,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CommentsHeaderView(
    modifier: Modifier,
    commentsNumber: Long,
    commentsSortType: CommentSortOrder,
    onChangeCommentSortOrder: (CommentSortOrder) -> Unit,
    onClose: () -> Unit
) {
    Column(modifier = modifier.background(MaterialTheme.colors.surface)) {
        Row(
            modifier = Modifier.padding(start = paddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = paddingXXXSmall),
                text = stringResource(id = R.string.comments).uppercase(),
                style = RumbleTypography.h6Heavy,
                color = MaterialTheme.colors.primary,
            )

            Text(
                modifier = Modifier.padding(start = paddingXSmall),
                text = commentsNumber.shortString(withDecimal = true),
                style = RumbleTypography.h6Light,
                color = MaterialTheme.colors.secondary
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(modifier = Modifier.border(width = borderXXSmall, shape = RoundedCornerShape(radiusLarge), color = MaterialTheme.colors.secondaryVariant)) {
                Row {
                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = radiusLarge,
                                    bottomStart = radiusLarge
                                )
                            )
                            .background(if (commentsSortType == CommentSortOrder.NEW) MaterialTheme.colors.primary else MaterialTheme.colors.background)
                            .clickable {
                                onChangeCommentSortOrder(CommentSortOrder.NEW)
                            }
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(
                                    start = paddingMedium,
                                    end = paddingSmall,
                                    top = paddingXSmall,
                                    bottom = paddingXSmall,
                                ),
                            text = stringResource(id = CommentSortOrder.NEW.nameId),
                            color = if (commentsSortType == CommentSortOrder.NEW) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                            style = RumbleTypography.h6
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topEnd = radiusLarge, bottomEnd = radiusLarge))
                            .background(if (commentsSortType == CommentSortOrder.POPULAR) MaterialTheme.colors.primary else MaterialTheme.colors.background)
                            .clickable {
                                onChangeCommentSortOrder(CommentSortOrder.POPULAR)
                            }
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(
                                    start = paddingSmall,
                                    end = paddingMedium,
                                    top = paddingXSmall,
                                    bottom = paddingXSmall,
                                ),
                            text = stringResource(id = CommentSortOrder.POPULAR.nameId),
                            color = if (commentsSortType == CommentSortOrder.POPULAR) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primary,
                            style = RumbleTypography.h6
                        )
                    }
                }
            }

            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close),
                    tint = MaterialTheme.colors.primary
                )
            }
        }
    }
}

@Composable
@Preview
private fun CommentsHeaderViewPreview() {
    RumbleTheme {
        CommentsHeaderView(
            modifier = Modifier,
            commentsNumber = 100,
            commentsSortType = CommentSortOrder.NEW,
            onChangeCommentSortOrder = {},
            onClose = {}
        )
    }
}