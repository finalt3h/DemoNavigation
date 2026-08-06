package com.example.demonavigation

import android.os.SystemClock
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ScreenLogger {
    private const val TAG = "Test1111"
    public var timeStart = 0L
    fun log(target: Any, note: String) {
        val elapsedSeconds =
            (SystemClock.elapsedRealtimeNanos() - timeStart) / 1_000_000_000.0
        val formattedSeconds = String.format(Locale.US, "%.4f", elapsedSeconds)
        Log.d(TAG, "${target.javaClass.simpleName} - $note: $formattedSeconds s")
    }
}
