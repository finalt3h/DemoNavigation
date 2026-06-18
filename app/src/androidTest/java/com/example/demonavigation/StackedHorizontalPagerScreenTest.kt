package com.example.demonavigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class StackedHorizontalPagerScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun stackedHorizontalPagerScreenShowsThreePageLabels() {
        composeRule.setContent {
            StackedHorizontalPagerScreen()
        }

        composeRule.onNodeWithText("Page 1").assertIsDisplayed()
        composeRule.onNodeWithText("1 / 3").assertIsDisplayed()
    }
}
