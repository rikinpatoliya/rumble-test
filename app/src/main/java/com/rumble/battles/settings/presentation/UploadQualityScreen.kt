package com.rumble.battles.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.SettingsUploadingQualityTag
import com.rumble.battles.commonViews.CheckMarkItem
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.domain.settings.domain.domainmodel.UploadQuality
import com.rumble.theme.paddingLarge

@Composable
fun UploadQualityScreen(
    settingsHandler: SettingsHandler,
    onBackClick: () -> Unit
) {
    val uploadQuality by settingsHandler.uploadQuality.collectAsStateWithLifecycle(
        initialValue = UploadQuality.defaultUploadQuality
    )

    Column(
        modifier = Modifier
            .testTag(SettingsUploadingQualityTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.upload_quality),
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            onBackClick = onBackClick,
        )
        LazyColumn(
            modifier = Modifier.padding(top = paddingLarge)
        ) {
            items(UploadQuality.values()) { quality ->
                if (quality != UploadQuality.QUALITY_UNDEFINED) {
                    CheckMarkItem(
                        title = stringResource(id = quality.titleId),
                        selected = quality == uploadQuality,
                        addSeparator = true,
                    ) {
                        settingsHandler.onUpdateUploadQuality(quality)
                    }
                }
            }
        }
    }
}