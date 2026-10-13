package com.example.demonavigation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenLogger.log(this, "onCreate")
        setContentView(R.layout.activity_main)
        val dee : Double = 0.0001
        android.util.Log.d("Tesssss", "onCreate: ${dee.toString()}")
    }

    override fun onStart() {
        super.onStart()
        ab ()
        ScreenLogger.log(this, "onStart")
    }

    override fun onResume() {
        super.onResume()
        ScreenLogger.log(this, "onResume")
    }

    override fun onPause() {
        super.onPause()
        ScreenLogger.log(this, "onPause")
    }
    var i = 0
    var event: MutableSharedFlow<Int> = MutableSharedFlow()
    private fun ab () {
//        Log.d("hahae -444-:", "ab: $i")
        lifecycleScope.launch {
            event.throttleFirst(1000, false).collect {
                Log.d("hahae 1:", "ab: $it")
            }

        }
        lifecycleScope.launch {
            event.throttleFirst(1000, true).collect {
                Log.d("hahae 2:", "ab: $it")
            }

        }
        lifecycleScope.launch {
            var i = 0
            repeat(10) {
                event.emit(i)
                i++
                delay(300) // Delay ngắn hơn period 1000ms để thấy rõ hiệu ứng throttle
            }
        }
    }
}

fun <T> Flow<T>.throttleFirst(
    periodMillis: Long,
    keepLatest: Boolean = false
): Flow<T> = channelFlow {
    var isThrottling = false
    var cachedValue: T? = null
    var hasCache = false

    suspend fun throttle(value: T){
        isThrottling = true
        send(value)
        delay(periodMillis)
        if (hasCache == true && keepLatest) {
            hasCache = false
            throttle(cachedValue as T)
        }
        isThrottling = false
    }
    collect { value ->
        if (!isThrottling) {
            // 1. Chưa throttle: Emit ngay lập tức
            throttle(value)

        } else if (keepLatest) {
            // Đang trong thời gian throttle: Lưu lại giá trị mới nhất chưa được emit
            cachedValue = value
            hasCache = true
        }
    }
}