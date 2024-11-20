package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.LocalsPopupTag
import com.rumble.battles.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.brandedLocalsLogoBackground
import com.rumble.theme.brandedLocalsRed
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageXXLarge
import com.rumble.theme.overlapPadding
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXLarge
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.utils.extension.toLocalizedString

private const val TAG = "LocalsPopupBottomSheet"
private val BottomOffset = 200.dp

@Composable
internal fun LocalsPopupBottomSheet(
    modifier: Modifier = Modifier,
    localsCommunityEntity: LocalsCommunityEntity,
    withNavigationBar: Boolean = true,
    onSupport: () -> Unit,
    onCancel: () -> Unit,
) {
    LocalsContent(
        modifier = modifier.testTag(LocalsPopupTag),
        localsCommunityEntity = localsCommunityEntity,
        withNavigationBar = withNavigationBar,
        onSupport = onSupport,
        onCancel = onCancel,
    )
}

@Composable
private fun LocalsContent(
    modifier: Modifier = Modifier,
    localsCommunityEntity: LocalsCommunityEntity,
    withNavigationBar: Boolean,
    onSupport: () -> Unit,
    onCancel: () -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (close, scrollView, actionButton, gradient) = createRefs()
        Column(
            modifier = modifier
                .padding(start = paddingXLarge, end = paddingXLarge)
                .verticalScroll(rememberScrollState())
                .constrainAs(scrollView) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UserViewWithLocalsLogo(
                modifier = Modifier.padding(top = paddingXXXLarge),
                localsCommunityEntity = localsCommunityEntity
            )
            Text(
                text = "${localsCommunityEntity.title} ${stringResource(id = R.string.on_locals)}",
                modifier = Modifier.padding(top = paddingMedium),
                style = RumbleTypography.text26Bold,
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = localsCommunityEntity.description,
                modifier = Modifier.padding(top = paddingXXXSmall),
                style = RumbleTypography.h5,
                color = MaterialTheme.colors.primaryVariant,
                textAlign = TextAlign.Center
            )
            Divider(
                modifier = Modifier.padding(top = paddingLarge, bottom = paddingLarge),
                color = MaterialTheme.colors.secondaryVariant
            )
            LocalsBenefitView(
                drawableId = R.drawable.ic_unlock,
                text = stringResource(id = R.string.locals_access_all_content)
            )
            LocalsBenefitView(
                modifier = Modifier.padding(top = paddingMedium, bottom = paddingMedium),
                drawableId = R.drawable.ic_pencil,
                text = stringResource(id = R.string.locals_post_and_comment)
            )
            LocalsBenefitView(
                drawableId = R.drawable.ic_chat,
                text = stringResource(id = R.string.locals_join_live_chats)
            )
            Divider(
                modifier = Modifier.padding(top = paddingLarge, bottom = paddingLarge),
                color = MaterialTheme.colors.secondaryVariant,
            )
            LocalsStats(
                title = "${stringResource(id = R.string.community_members)}:",
                count = localsCommunityEntity.communityMembers,
            )
            LocalsStats(
                title = "${stringResource(id = R.string.comments)}:",
                count = localsCommunityEntity.comments,
            )
            LocalsStats(
                title = "${stringResource(id = R.string.posts)}:",
                count = localsCommunityEntity.posts,
            )
            LocalsStats(
                title = "${stringResource(id = R.string.likes)}:",
                count = localsCommunityEntity.likes,
            )
            Spacer(
                Modifier
                    .height(BottomOffset)
            )
        }
        IconButton(
            modifier = Modifier.constrainAs(close) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            },
            onClick = { onCancel.invoke() }
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.close),
                tint = MaterialTheme.colors.primaryVariant
            )
        }
        Column(modifier = Modifier.constrainAs(gradient) {
            top.linkTo(actionButton.top)
            bottom.linkTo(parent.bottom)
            height = Dimension.fillToConstraints
        }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colors.background.copy(alpha = 0F),
                                MaterialTheme.colors.background
                            ),
                        )
                    )
            ) {}
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2F)
                    .background(color = MaterialTheme.colors.background)
            ) {}
        }
        Column(
            modifier = Modifier
                .constrainAs(actionButton) {
                    bottom.linkTo(parent.bottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .padding(top = paddingMedium, start = paddingXXXLarge, end = paddingXXXLarge)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(radiusMedium),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = brandedLocalsRed
                ),
                onClick = { onSupport.invoke() }
            ) {
                Text(
                    modifier = Modifier.padding(top = paddingXSmall, bottom = paddingXSmall),
                    text = stringResource(id = R.string.support).uppercase(),
                    style = RumbleTypography.text18Black,
                    color = enforcedWhite
                )
            }
            Text(
                modifier = Modifier.padding(paddingMedium),
                text = stringResource(id = R.string.leave_rumble_to_locals),
                style = RumbleTypography.h6Light,
                color = MaterialTheme.colors.primaryVariant,
                textAlign = TextAlign.Center
            )
            if (withNavigationBar) {
                BottomNavigationBarScreenSpacer()
            }
        }
    }
}

@Composable
private fun UserViewWithLocalsLogo(
    modifier: Modifier = Modifier,
    localsCommunityEntity: LocalsCommunityEntity
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (profileImage, localsLogo) = createRefs()
        Box(
            Modifier
                .padding(start = overlapPadding)
                .size(imageXXLarge)
                .clip(CircleShape)
                .background(
                    color = brandedLocalsLogoBackground,
                    shape = CircleShape
                )
                .constrainAs(localsLogo) {
                    top.linkTo(parent.top)
                    centerTo(parent)
                },
        ) {
            Image(
                modifier = Modifier.align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_locals_logo),
                contentDescription = "",
            )
        }
        ProfileImageComponent(
            modifier = Modifier
                .constrainAs(profileImage) {
                    top.linkTo(parent.top)
                    centerTo(parent)
                }
                .padding(end = overlapPadding),
            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXLargeStyle(),
            userPicture = localsCommunityEntity.profileImage
        )
    }
}

@Composable
private fun LocalsBenefitView(modifier: Modifier = Modifier, drawableId: Int, text: String) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = paddingXXXSmall),
            painter = painterResource(id = drawableId),
            contentDescription = "",
            tint = MaterialTheme.colors.primaryVariant
        )
        Text(
            modifier = Modifier.padding(start = paddingMedium),
            text = text,
            style = RumbleTypography.body1,
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun LocalsStats(title: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = RumbleTypography.h3Normal,
            color = MaterialTheme.colors.secondary
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = count.toLocalizedString(TAG),
            style = RumbleTypography.h3,
            color = brandedLocalsRed,
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun PreviewLocalsPopupBottomSheet() {
    LocalsContent(
        modifier = Modifier.padding(paddingXLarge),
        localsCommunityEntity = LocalsCommunityEntity(
            title = "VivaFrei on Locals",
            description = "This is the VivaBarnesLaw Community. VivaBarnesLaw Community.",
            profileImage = R.drawable.ic_locals_logo.toString(),
            communityMembers = 22265,
            comments = 130267,
            posts = 10882,
            likes = 190211,
            videoUrl = "",
            channelUrl = "",
            showPremiumFlow = false,
            joinButtonText = ""
        ),
        withNavigationBar = true,
        onSupport = {},
        onCancel = {},
    )
}