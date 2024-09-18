package com.rumble.battles.camera

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.UploadChannelTag
import com.rumble.battles.camera.presentation.CameraUploadHandler
import com.rumble.battles.camera.presentation.UploadChannelSelectionScreen
import com.rumble.battles.camera.presentation.UserUploadUIState
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UploadChannelSelectionScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<CameraUploadHandler>(relaxed = true)
    private val mockState = mockk<UserUploadUIState>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.uiState } returns MutableStateFlow(mockState)
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                UploadChannelSelectionScreen(
                    cameraUploadHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(UploadChannelTag).assertIsDisplayed()
    }
}