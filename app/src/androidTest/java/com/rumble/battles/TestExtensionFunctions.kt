package com.rumble.battles

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode

fun ComposeContentTestRule.scrollToAndAssertDisplayed(scrollViewTag: String, targetTag: String) {
    this.onNodeWithTag(scrollViewTag).performScrollToNode(
        hasTestTag(
            targetTag
        )
    )
    this.onNodeWithTag(targetTag).assertIsDisplayed()
}