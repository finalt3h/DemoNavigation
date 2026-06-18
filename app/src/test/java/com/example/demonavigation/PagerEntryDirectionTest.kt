package com.example.demonavigation

import org.junit.Assert.assertEquals
import org.junit.Test

class PagerEntryDirectionTest {
    @Test
    fun missingValueDefaultsToLeft() {
        assertEquals(PagerEntryDirection.LEFT, PagerEntryDirection.fromStoredValue(null))
    }

    @Test
    fun invalidValueDefaultsToLeft() {
        assertEquals(PagerEntryDirection.LEFT, PagerEntryDirection.fromStoredValue("up"))
    }

    @Test
    fun storedValuesRestoreTheirDirections() {
        assertEquals(PagerEntryDirection.LEFT, PagerEntryDirection.fromStoredValue("left"))
        assertEquals(PagerEntryDirection.RIGHT, PagerEntryDirection.fromStoredValue("right"))
    }
}
