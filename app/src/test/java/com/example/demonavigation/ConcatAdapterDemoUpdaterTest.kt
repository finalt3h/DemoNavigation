package com.example.demonavigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ConcatAdapterDemoUpdaterTest {
    @Test
    fun nextState_updatesBetweenTwoAndTenItemsAcrossAllLists() {
        val initialState = ConcatDemoState.initial()
        val nextState = ConcatDemoUpdater(Random(1)).nextState(initialState)

        assertEquals(15, nextState.people.size)
        assertEquals(5, nextState.profiles.size)
        assertEquals(4, nextState.photos.size)
        assertEquals(3, nextState.contacts.size)

        val changedCount =
            initialState.people.zip(nextState.people).count { (before, after) -> before != after } +
                initialState.profiles.zip(nextState.profiles).count { (before, after) -> before != after } +
                initialState.photos.zip(nextState.photos).count { (before, after) -> before != after } +
                initialState.contacts.zip(nextState.contacts).count { (before, after) -> before != after }

        assertTrue(changedCount in 2..10)
    }
}
