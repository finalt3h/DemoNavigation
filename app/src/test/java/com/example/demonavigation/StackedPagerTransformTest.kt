package com.example.demonavigation

import org.junit.Assert.assertEquals
import org.junit.Test

class StackedPagerTransformTest {
    @Test
    fun outgoingPageCountersPagerHorizontalMovement() {
        val transform = computeStackedPagerTransform(
            page = 0,
            transitionBasePage = 0,
            transitionDirection = 1,
            currentPage = 0,
            currentPageOffsetFraction = 0.4f,
            pageWidth = 1_000f
        )

        assertEquals(400f, transform.translationX, 0.001f)
        assertEquals(0.944f, transform.scale, 0.001f)
        assertEquals(0.672f, transform.alpha, 0.001f)
        assertEquals(38.4f, transform.translationY, 0.001f)
    }

    @Test
    fun leftOptionEntersFromRightWhenSwipingRightToLeft() {
        val transform = computeStackedPagerTransform(
            page = 1,
            transitionBasePage = 0,
            transitionDirection = 1,
            currentPage = 0,
            currentPageOffsetFraction = 0.4f,
            pageWidth = 1_000f,
            entryDirection = PagerEntryDirection.LEFT
        )

        assertEquals(0f, transform.translationX, 0.001f)
        assertEquals(1f, transform.scale, 0.001f)
        assertEquals(1f, transform.alpha, 0.001f)
        assertEquals(0f, transform.translationY, 0.001f)
    }

    @Test
    fun rightOptionEntersFromLeftWhenSwipingRightToLeft() {
        val transform = computeStackedPagerTransform(
            page = 1,
            transitionBasePage = 0,
            transitionDirection = 1,
            currentPage = 0,
            currentPageOffsetFraction = 0.4f,
            pageWidth = 1_000f,
            entryDirection = PagerEntryDirection.RIGHT
        )

        assertEquals(-1_200f, transform.translationX, 0.001f)
    }

    @Test
    fun leftOptionEntersFromLeftWhenSwipingLeftToRight() {
        val transform = computeStackedPagerTransform(
            page = 0,
            transitionBasePage = 1,
            transitionDirection = -1,
            currentPage = 1,
            currentPageOffsetFraction = -0.4f,
            pageWidth = 1_000f,
            entryDirection = PagerEntryDirection.LEFT
        )

        assertEquals(0f, transform.translationX, 0.001f)
    }

    @Test
    fun rightOptionEntersFromRightWhenSwipingLeftToRight() {
        val transform = computeStackedPagerTransform(
            page = 0,
            transitionBasePage = 1,
            transitionDirection = -1,
            currentPage = 1,
            currentPageOffsetFraction = -0.4f,
            pageWidth = 1_000f,
            entryDirection = PagerEntryDirection.RIGHT
        )

        assertEquals(1_200f, transform.translationX, 0.001f)
    }

    @Test
    fun outgoingPageKeepsSinkingAfterCurrentPageFlipsAtHalfway() {
        val outgoingTransform = computeStackedPagerTransform(
            page = 0,
            transitionBasePage = 0,
            transitionDirection = 1,
            currentPage = 1,
            currentPageOffsetFraction = -0.4f,
            pageWidth = 1_000f
        )
        val incomingTransform = computeStackedPagerTransform(
            page = 1,
            transitionBasePage = 0,
            transitionDirection = 1,
            currentPage = 1,
            currentPageOffsetFraction = -0.4f,
            pageWidth = 1_000f,
            entryDirection = PagerEntryDirection.LEFT
        )

        assertEquals(600f, outgoingTransform.translationX, 0.001f)
        assertEquals(0.916f, outgoingTransform.scale, 0.001f)
        assertEquals(0.508f, outgoingTransform.alpha, 0.001f)
        assertEquals(57.6f, outgoingTransform.translationY, 0.001f)
        assertEquals(0f, incomingTransform.translationX, 0.001f)
        assertEquals(1f, incomingTransform.scale, 0.001f)
        assertEquals(1f, incomingTransform.alpha, 0.001f)
    }

    @Test
    fun outgoingPageDoesNotResetWhenPagerStateChangesNearEnd() {
        val outgoingTransform = computeStackedPagerTransform(
            page = 0,
            transitionBasePage = 0,
            transitionDirection = 1,
            currentPage = 1,
            currentPageOffsetFraction = 0f,
            pageWidth = 1_000f
        )
        val incomingTransform = computeStackedPagerTransform(
            page = 1,
            transitionBasePage = 0,
            transitionDirection = 1,
            currentPage = 1,
            currentPageOffsetFraction = 0f,
            pageWidth = 1_000f,
            entryDirection = PagerEntryDirection.LEFT
        )

        assertEquals(1_000f, outgoingTransform.translationX, 0.001f)
        assertEquals(0.86f, outgoingTransform.scale, 0.001f)
        assertEquals(0.18f, outgoingTransform.alpha, 0.001f)
        assertEquals(96f, outgoingTransform.translationY, 0.001f)
        assertEquals(0f, incomingTransform.translationX, 0.001f)
        assertEquals(1f, incomingTransform.scale, 0.001f)
        assertEquals(1f, incomingTransform.alpha, 0.001f)
    }

    @Test
    fun fastFlingSinksEachPageAsItBecomesTheOutgoingPage() {
        val transform = computeStackedPagerTransform(
            page = 1,
            transitionBasePage = 0,
            transitionDirection = 1,
            currentPage = 1,
            currentPageOffsetFraction = 0.35f,
            pageWidth = 1_000f
        )

        assertEquals(350f, transform.translationX, 0.001f)
        assertEquals(0.951f, transform.scale, 0.001f)
        assertEquals(0.713f, transform.alpha, 0.001f)
        assertEquals(33.6f, transform.translationY, 0.001f)
    }
}
