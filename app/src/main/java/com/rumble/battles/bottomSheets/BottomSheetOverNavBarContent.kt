package com.rumble.battles.bottomSheets

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.rumble.battles.R
import com.rumble.battles.commonViews.ReportBottomSheet
import com.rumble.battles.content.presentation.BottomSheetContent
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.feed.presentation.repost.RepostUpsellScreen
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.library.presentation.playlist.PlayListAction
import com.rumble.battles.login.presentation.AuthHandler
import com.rumble.battles.navigation.RumbleScreens
import com.rumble.battles.sort.SortFollowingBottomSheet
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscription

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetOverNavBarContent(
    bottomSheetState: ModalBottomSheetState,
    bottomSheetData: BottomSheetContent,
    contentHandler: ContentHandler,
    authHandler: AuthHandler,
    activityHandler: RumbleActivityHandler,
    navController: NavHostController,
    onHideBottomSheet: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegistration: (String, String, String, String) -> Unit,
    onNavigateToAgeVerification: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    when (bottomSheetData) {
        is BottomSheetContent.UserUploadChannelSwitcher -> {
            ChannelSwitchBottomSheet(
                channelEntityList = bottomSheetData.channels,
                onChannelClick = { channelId ->
                    navController.navigate(RumbleScreens.Channel.getPath(channelId))
                },
                onHideBottomSheet = onHideBottomSheet
            )
        }

        is BottomSheetContent.ChannelNotificationsSheet -> {
            ChannelNotificationsBottomSheet(
                channelDetailsEntity = bottomSheetData.channelDetailsEntity,
                contentHandler = contentHandler,
                onHideBottomSheet = onHideBottomSheet
            )
        }

        is BottomSheetContent.MoreUploadOptionsSheet -> {
            MoreUploadOptionsBottomSheet(
                title = bottomSheetData.title,
                subtitle = bottomSheetData.subtitle,
                bottomSheetItems = bottomSheetData.bottomSheetItems,
                onHideBottomSheet = onHideBottomSheet
            )
        }

        is BottomSheetContent.MoreVideoOptionsSheet -> {
            VideoOptionsBottomSheet(
                videoOptions = bottomSheetData.videoOptions,
                onSaveToPlaylist = {
                    contentHandler.onSaveToPlayList(bottomSheetData.videoEntity.id)
                },
                onSaveToWatchLater = {
                    onHideBottomSheet()
                    contentHandler.onSaveToWatchLater(bottomSheetData.videoEntity.id)
                },
                onRemoveFromPlayList = { playListId ->
                    contentHandler.onRemoveFromPlayList(playListId, bottomSheetData.videoEntity.id)
                    onHideBottomSheet()
                },
                onShare = {
                    contentHandler.onShare(bottomSheetData.videoEntity.url)
                    onHideBottomSheet()
                },
                onHideBottomSheet = onHideBottomSheet
            )
        }

        is BottomSheetContent.AddToPlayList -> {
            AddToPlayListBottomSheet(
                videoId = bottomSheetData.videoEntityId,
                addToPlayListHandler = contentHandler,
                onClose = onHideBottomSheet
            )
        }

        is BottomSheetContent.CreateNewPlayList -> {
            PlayListSettingsBottomSheet(
                contentHandler = contentHandler,
                playListAction = PlayListAction.Create,
                videoId = bottomSheetData.videoEntityId,
                onClose = onHideBottomSheet
            )
        }

        is BottomSheetContent.MorePlayListOptionsSheet -> {
            PlayListOptionsBottomSheet(
                bottomSheetItems = getPlayListOptionBottomSheetItems(
                    playListOptions = bottomSheetData.playListEntityWithOptions.playListOptions,
                    onConfirmDeleteWatchHistory = contentHandler::onConfirmDeleteWatchHistory,
                    onPlayListSettings = { contentHandler.onPlayListSettings(bottomSheetData.playListEntityWithOptions.playListEntity) },
                    onConfirmDeletePlayList = {
                        contentHandler.onConfirmDeletePlayList(
                            bottomSheetData.playListEntityWithOptions.playListEntity.id
                        )
                    },
                ),
                onHideBottomSheet = onHideBottomSheet
            )
        }

        is BottomSheetContent.RepostVideo -> {
            RepostVideoBottomSheet(
                contentHandler = contentHandler,
                videoEntity = bottomSheetData.videoEntity,
                onClose = {
                    contentHandler.resetRepostState()
                    onHideBottomSheet()
                }
            )
        }

        is BottomSheetContent.PlayListSettingsSheet -> {
            PlayListSettingsBottomSheet(
                contentHandler = contentHandler,
                playListAction = PlayListAction.Edit,
                onClose = onHideBottomSheet
            )
        }

        is BottomSheetContent.PremiumPromo -> {
            PremiumOptionsBottomSheet(
                bottomSheetState = bottomSheetState,
                isPremiumUser = false,
                onClose = {
                    contentHandler.onClosePremiumPromo()
                    onHideBottomSheet()
                },
                onActionButtonClicked = { contentHandler.onGetPremium() }
            )
        }

        is BottomSheetContent.PremiumOptions -> {
            PremiumOptionsBottomSheet(
                bottomSheetState = bottomSheetState,
                isPremiumUser = true,
                onClose = onHideBottomSheet
            ) {
                activityHandler.onOpenWebView(PremiumSubscription.RESTORE_SUBSCRIPTION_LINK)
            }
        }

        is BottomSheetContent.PremiumSubscriptionDetails -> {
            PremiumSubscriptionDetailsBottomSheet(
                bottomSheetState = bottomSheetState,
                contentHandler = contentHandler,
                activityHandler = activityHandler,
                onClose = onHideBottomSheet
            )
        }

        is BottomSheetContent.PremiumSubscription -> {
            PremiumSubscriptionBottomSheet(
                handler = contentHandler,
                activityHandler = activityHandler
            )
        }

        is BottomSheetContent.SortFollowingSheet -> {
            SortFollowingBottomSheet(
                sortFollowingType = bottomSheetData.sortFollowingType,
                onSelected = {
                    contentHandler.onSortFollowingSelected(it)
                    onHideBottomSheet()
                },
                onClose = onHideBottomSheet
            )
        }

        is BottomSheetContent.AuthMenu -> {
            AuthBottomSheet(
                authHandler = authHandler,
                bottomSheetState = bottomSheetState,
                onClose = onHideBottomSheet,
                onEmailLogin = {
                    onHideBottomSheet()
                    onNavigateToLogin()
                },
                onError = { contentHandler.onError(errorMessage = it, withPadding = false) },
                onNavigateToRegistration = { loginType, userId, token, email ->
                    onHideBottomSheet()
                    onNavigateToRegistration(loginType.value.toString(), userId, token, email)
                },
                onNavigateToAgeVerification = {
                    onHideBottomSheet()
                    onNavigateToAgeVerification()
                }
            )
        }

        is BottomSheetContent.RepostMoreActions -> {
            RepostActionsBottomSheet(
                repost = bottomSheetData.repost,
                currentUserId = bottomSheetData.userId,
                onDeleteRepost = {
                    onHideBottomSheet()
                    contentHandler.onUndoRepost(it.id)
                },
                onReportRepost = {
                    onHideBottomSheet()
                    contentHandler.onDisplayReportRepostOptions(it)
                },
                onHideBottomSheet = onHideBottomSheet,
            )
        }

        is BottomSheetContent.ReportRepost -> {
            ReportBottomSheet(
                subtitle = stringResource(id = R.string.why_report_video),
                coroutineScope = coroutineScope,
                bottomSheetState = bottomSheetState,
                onReport = {
                    contentHandler.onReportRepost(bottomSheetData.repost, it)
                }
            )
        }

        is BottomSheetContent.RepostUpsell -> {
            RepostUpsellScreen(
                onGoPremium = contentHandler::onGetPremium,
                onClose = onHideBottomSheet,
            )
        }

        else -> return
    }
}