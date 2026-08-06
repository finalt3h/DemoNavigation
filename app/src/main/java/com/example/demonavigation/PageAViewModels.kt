package com.example.demonavigation

import androidx.lifecycle.ViewModel

abstract class PageAViewModel(pageName: String) : ViewModel() {
    val heavyTextData: String = createHeavyTextData(pageName)

    private fun createHeavyTextData(pageName: String): String {
        val pageSeed = pageName.hashCode()
        return CharArray(HEAVY_TEXT_CHARACTER_COUNT) { index ->
            ('A'.code + Math.floorMod(index + pageSeed, 26)).toChar()
        }.concatToString()
    }

    private companion object {
        const val HEAVY_TEXT_CHARACTER_COUNT = 1_000_000
    }
}

class Fragment1AViewModel : PageAViewModel("Fragment1A")
class Fragment2AViewModel : PageAViewModel("Fragment2A")
class Fragment3AViewModel : PageAViewModel("Fragment3A")
class Fragment4AViewModel : PageAViewModel("Fragment4A")
class Fragment5AViewModel : PageAViewModel("Fragment5A")
