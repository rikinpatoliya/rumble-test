package com.rumble.battles.camera.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.rumble.battles.UploadLicenseTag
import com.rumble.battles.commonViews.RadioSelectionRow
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.domain.camera.UploadLicense
import com.rumble.theme.paddingLarge

@Composable
fun CameraUploadLicenceSelectionScreen(
    cameraUploadHandler: CameraUploadHandler,
    onBackClick: () -> Unit,
) {
    val uiState by cameraUploadHandler.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .testTag(UploadLicenseTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.select_license),
            modifier = Modifier.fillMaxWidth(),
            onBackClick = onBackClick,
        )
        LazyColumn {
            items(UploadLicense.values()) { license ->
                RadioSelectionRow(
                    title = stringResource(id = license.titleId),
                    subTitle = stringResource(id = license.subtitleId),
                    description = stringResource(id = license.descriptionId),
                    selected = license == uiState.selectedUploadLicense,
                    expandable = true,
                    onSelected = { cameraUploadHandler.onLicenseSelected(license) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(paddingLarge))
            }
        }
    }
}