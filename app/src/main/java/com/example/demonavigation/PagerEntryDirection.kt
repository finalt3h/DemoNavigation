package com.example.demonavigation

import android.content.Context
import androidx.core.content.edit

internal enum class PagerEntryDirection(val storedValue: String) {
    LEFT("left"),
    RIGHT("right");

    companion object {
        fun fromStoredValue(value: String?): PagerEntryDirection {
            return entries.firstOrNull { it.storedValue == value } ?: LEFT
        }
    }
}

internal class PagerEntryDirectionPreferences(context: Context) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_FILE,
        Context.MODE_PRIVATE
    )

    fun read(): PagerEntryDirection {
        return PagerEntryDirection.fromStoredValue(
            preferences.getString(ENTRY_DIRECTION_KEY, null)
        )
    }

    fun write(direction: PagerEntryDirection) {
        preferences.edit {
            putString(ENTRY_DIRECTION_KEY, direction.storedValue)
        }
    }

    private companion object {
        const val PREFERENCES_FILE = "pager_preferences"
        const val ENTRY_DIRECTION_KEY = "entry_direction"
    }
}
