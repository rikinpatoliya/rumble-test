package com.rumble.battles.camera.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.UploadChannelTag
import com.rumble.battles.commonViews.ChannelSelectableRow
import com.rumble.battles.commonViews.EmptyView
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall

@Composable
fun UploadChannelSelectionScreen(
    cameraUploadHandler: CameraUploadHandler,
    onBackClick: () -> Unit,
) {
    val uiState by cameraUploadHandler.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .testTag(UploadChannelTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.select_a_channel),
            modifier = Modifier.fillMaxWidth(),
            onBackClick = onBackClick,
        )

        Text(
            text = stringResource(id = R.string.publish_under_your_profile),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = paddingXSmall),
            color = MaterialTheme.colors.primaryVariant,
            style = RumbleTypography.body1,
            textAlign = TextAlign.Center
        )
        ChannelSelectableRow(
            modifier = Modifier.padding(
                start = paddingMedium,
                end = paddingMedium,
                top = paddingLarge,
                bottom = paddingLarge,
            ),
            channelId = uiState.userUploadProfile.id,
            channelTitle = uiState.userUploadProfile.title,
            thumbnail = uiState.userUploadProfile.thumbnail ?: "",
            selected = uiState.userUploadProfile.id == uiState.selectedUploadChannel.id,
            onSelectChannel = { cameraUploadHandler.onUploadChannelSelected(it) }
        )
        Text(
            text = "${stringResource(id = R.string.or)} ${stringResource(id = R.string.select_a_channel).lowercase()}",
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.primaryVariant,
            style = RumbleTypography.body1,
            textAlign = TextAlign.Center
        )
        if (uiState.userUploadChannels.isEmpty()) {
            EmptyView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = paddingLarge,
                        bottom = paddingMedium,
                        start = paddingMedium,
                        end = paddingMedium,
                    ),
                title = stringResource(id = R.string.you_dont_have_channels),
                text = stringResource(id = R.string.you_can_create_one_on_rumble_com)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = paddingMedium,
                        start = paddingMedium,
                        end = paddingMedium,
                    ),
            ) {
                items(uiState.userUploadChannels) {
                    ChannelSelectableRow(
                        modifier = Modifier.padding(
                            top = paddingXSmall,
                            bottom = paddingXSmall
                        ),
                        channelId = it.id,
                        channelTitle = it.title,
                        thumbnail = it.thumbnail ?: "",
                        selected = it.id == uiState.selectedUploadChannel.id,
                        onSelectChannel = { channelId -> cameraUploadHandler.onUploadChannelSelected(channelId) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(paddingMedium))
                }
            }
        }
    }
}