package com.example.demonavigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class StackedHorizontalPagerScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun stackedHorizontalPagerScreenShowsDirectionSelectorAndPageLabels() {
        val preferences = PagerEntryDirectionPreferences(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        preferences.write(PagerEntryDirection.LEFT)

        composeRule.setContent {
            StackedHorizontalPagerScreen()
        }

        composeRule.onNodeWithText("Page 1").assertIsDisplayed()
        composeRule.onNodeWithText("1 / 5").assertIsDisplayed()
        composeRule.onNodeWithText("Trái").assertIsDisplayed().assertIsSelected()
        composeRule.onNodeWithText("Phải").assertIsDisplayed()
    }

    @Test
    fun selectingRightUpdatesAndPersistsTheDirection() {
        val preferences = PagerEntryDirectionPreferences(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        preferences.write(PagerEntryDirection.LEFT)

        composeRule.setContent {
            StackedHorizontalPagerScreen()
        }

        composeRule.onNodeWithText("Phải").performClick().assertIsSelected()
        composeRule.runOnIdle {
            assertEquals(PagerEntryDirection.RIGHT, preferences.read())
        }
    }

    @Test
    fun savedRightDirectionIsRestored() {
        val preferences = PagerEntryDirectionPreferences(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        preferences.write(PagerEntryDirection.RIGHT)

        composeRule.setContent {
            StackedHorizontalPagerScreen()
        }

        composeRule.onNodeWithText("Phải").assertIsSelected()
    }
}
