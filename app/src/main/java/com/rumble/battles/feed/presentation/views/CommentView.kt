package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.radiusXXXXSmall
import com.rumble.theme.verifiedBadgeHeightSmall
import com.rumble.utils.extension.agoString

@Composable
fun CommentView(
    modifier: Modifier = Modifier,
    commentEntity: CommentEntity,
    showReplies: Boolean = true,
    hasPremiumRestriction: Boolean,
    onReplies: (CommentEntity) -> Unit,
    onDelete: (CommentEntity) -> Unit = {},
    onReport: (CommentEntity) -> Unit = {},
    onLike: (CommentEntity) -> Unit = {},
    onReply: (CommentEntity) -> Unit = {}
) {
    Column(modifier = modifier) {
        MainCommentView(
            modifier = Modifier.padding(bottom = paddingXSmall),
            commentEntity = commentEntity,
            showReplies = showReplies,
            hasPremiumRestriction = hasPremiumRestriction,
            onReplies = onReplies,
            onDelete = onDelete,
            onReport = onReport,
            onLike = onLike,
            onReply = onReply
        )
        if (commentEntity.displayReplies) {
            commentEntity.replayList?.forEach {
                ReplyView(
                    commentEntity = it,
                    hasPremiumRestriction = hasPremiumRestriction,
                    onReplies = onReplies,
                    onDelete = onDelete,
                    onReport = onReport,
                    onLike = onLike,
                    onReply = onReply
                )
            }
        }
    }
}

@Composable
private fun MainCommentView(
    modifier: Modifier = Modifier,
    commentEntity: CommentEntity,
    showReplies: Boolean = true,
    hasPremiumRestriction: Boolean,
    onReplies: (CommentEntity) -> Unit,
    onDelete: (CommentEntity) -> Unit = {},
    onReport: (CommentEntity) -> Unit = {},
    onLike: (CommentEntity) -> Unit = {},
    onReply: (CommentEntity) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = paddingXSmall, end = paddingXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImageComponent(
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageSmallStyle(),
                userName = commentEntity.author,
                userPicture = commentEntity.authorThumb
            )

            UserNameViewSingleLine(
                modifier = Modifier.padding(start = paddingXSmall),
                name = commentEntity.author,
                verifiedBadge = commentEntity.verifiedBadge,
                textStyle = h6,
                textColor = MaterialTheme.colors.secondary,
                spacerWidth = paddingXXXSmall,
                verifiedBadgeHeight = verifiedBadgeHeightSmall
            )

            Box(
                modifier = Modifier
                    .padding(
                        start = paddingXSmall,
                        end = paddingXSmall,
                        top = paddingXXXXSmall
                    )
                    .clip(CircleShape)
                    .size(radiusXXXXSmall)
                    .background(MaterialTheme.colors.secondary)
            )

            Text(
                text = commentEntity.date?.agoString(LocalContext.current) ?: "",
                maxLines = 1,
                style = h6Light,
                color = MaterialTheme.colors.secondary
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingXSmall, start = paddingXSmall, end = paddingXSmall),
            text = commentEntity.commentText,
            style = body1,
            color = MaterialTheme.colors.primary
        )

        if (showReplies) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (commentEntity.replyAllowed) {
                    RepliesView(
                        modifier = Modifier.wrapContentSize(),
                        replaysNumber = commentEntity.replayList?.size ?: 0,
                        replied = commentEntity.repliedByCurrentUser,
                        onClick = { onReplies(commentEntity) },
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (commentEntity.currentUserComment) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(radiusSmall))
                            .clickable { onDelete(commentEntity) },
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(
                                    start = paddingXSmall,
                                    top = paddingXXXSmall,
                                    end = paddingXSmall,
                                    bottom = paddingXXXSmall
                                ),
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = stringResource(id = R.string.delete),
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(radiusSmall))
                            .clickable { onReport(commentEntity) },
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(
                                    start = paddingXSmall,
                                    top = paddingXXXSmall,
                                    end = paddingXSmall,
                                    bottom = paddingXXXSmall
                                ),
                            painter = painterResource(id = R.drawable.ic_flag),
                            contentDescription = stringResource(id = R.string.report),
                            tint = MaterialTheme.colors.secondary
                        )
                    }
                }

                LikeCommentView(
                    modifier = Modifier.wrapContentSize(),
                    likeNumber = commentEntity.likeNumber,
                    userVote = commentEntity.userVote,
                    onClick = { onLike(commentEntity) }
                )

                if (hasPremiumRestriction.not() && commentEntity.replyAllowed) {
                    RumbleTextActionButton(
                        text = stringResource(id = R.string.reply),
                        textStyle = h6,
                        textColor = MaterialTheme.colors.secondary
                    ) {
                        onReply(commentEntity)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReplyView(
    modifier: Modifier = Modifier,
    commentEntity: CommentEntity,
    hasPremiumRestriction: Boolean,
    onReplies: (CommentEntity) -> Unit,
    onDelete: (CommentEntity) -> Unit,
    onReport: (CommentEntity) -> Unit,
    onLike: (CommentEntity) -> Unit,
    onReply: (CommentEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = modifier
                .height(IntrinsicSize.Min)
                .padding(start = paddingMedium)
                .fillMaxWidth()
        ) {
            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colors.secondaryVariant
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                MainCommentView(
                    modifier = Modifier.padding(
                        top = paddingLarge,
                        start = paddingMedium,
                        bottom = paddingSmall
                    ),
                    commentEntity = commentEntity,
                    hasPremiumRestriction = hasPremiumRestriction,
                    onReplies = onReplies,
                    onDelete = onDelete,
                    onReport = onReport,
                    onLike = onLike,
                    onReply = onReply
                )
                if (commentEntity.displayReplies) {
                    commentEntity.replayList?.forEach {
                        ReplyView(
                            modifier = Modifier.padding(start = paddingMedium),
                            commentEntity = it,
                            hasPremiumRestriction = hasPremiumRestriction,
                            onReplies = onReplies,
                            onDelete = onDelete,
                            onReport = onReport,
                            onLike = onLike,
                            onReply = onReply
                        )
                    }
                }
            }
        }
    }
}