package com.rumble.battles.profile.presentation

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.rumble.battles.EditProfileImageTag
import com.rumble.battles.EditProfileMainActionButtonTag
import com.rumble.battles.EditProfileTag
import com.rumble.battles.LoadingTag
import com.rumble.battles.R
import com.rumble.battles.RumbleBasicTopAppBarTag
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test


internal class EditProfileScreenKtTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<EditProfileHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.uiState } returns MutableStateFlow(
            UserProfileUIState(
                userProfileEntity = UserProfileEntity(
                    "",
                    "",
                    "",
                    false,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    CountryEntity(0, ""),
                    "",
                    0,
                    false,
                    Gender.Unspecified,
                    null
                ),
                initialFetch = false
            )
        )
        every { mockHandler.countriesList } returns MutableStateFlow(emptyList())
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                EditProfileScreen(
                    editProfileHandler = mockHandler,
                    onBackClick = {}
                )
            }
        }
        composeRule.onNodeWithTag(EditProfileTag).assertIsDisplayed()
    }

    @Test
    fun testLoadingState() {
        every { mockHandler.uiState } returns MutableStateFlow(
            UserProfileUIState(
                userProfileEntity = UserProfileEntity(
                    "",
                    "",
                    "",
                    false,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    CountryEntity(0, ""),
                    "",
                    0,
                    false,
                    Gender.Unspecified,
                    null
                ),
                loading = true
            )
        )
        composeRule.setContent {
            RumbleTheme {
                EditProfileScreen(
                    editProfileHandler = mockHandler,
                    onBackClick = {}
                )
            }
        }
        composeRule.onNodeWithTag(LoadingTag).assertIsDisplayed()
    }

    @Test
    fun testEditProfileContent() {
        var name = ""
        var phone = ""
        var address = ""
        var city = ""
        var state = ""
        composeRule.setContent {
            name = stringResource(id = R.string.name).uppercase()
            name = stringResource(id = R.string.name).uppercase()
            phone = stringResource(id = R.string.phone).uppercase()
            address = stringResource(id = R.string.address).uppercase()
            city = stringResource(id = R.string.city).uppercase()
            state = stringResource(id = R.string.state).uppercase()
            RumbleTheme {
                EditProfileScreen(
                    editProfileHandler = mockHandler,
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithTag(EditProfileImageTag).assertIsDisplayed()
        composeRule.onNodeWithTag(EditProfileMainActionButtonTag).assertIsDisplayed()
        composeRule.onNodeWithText(name).assertIsDisplayed()
        composeRule.onNodeWithText(phone).assertIsDisplayed()
        composeRule.onNodeWithText(address).assertIsDisplayed()
        composeRule.onNodeWithText(city).assertIsDisplayed()
        composeRule.onNodeWithText(state).assertIsDisplayed()
    }

    @Test
    fun testTopBar() {
        composeRule.setContent {
            RumbleTheme {
                EditProfileScreen(
                    editProfileHandler = mockHandler,
                    onBackClick = {}
                )
            }
        }
        composeRule.onNodeWithTag(RumbleBasicTopAppBarTag).assertIsDisplayed()
    }
}