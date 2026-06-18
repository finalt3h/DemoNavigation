package com.example.demonavigation

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.example.demonavigation.ui.theme.DemoNavigationTheme

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoNavigationTheme {
                StackedHorizontalPagerScreen()
            }
        }
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }
    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }
}
