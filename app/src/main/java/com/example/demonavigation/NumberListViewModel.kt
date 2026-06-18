package com.example.demonavigation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class NumberListViewModel : ViewModel() {
    private val _numbers = mutableStateListOf(1, 2, 3, 4, 5)
    val numbers: List<Int> = _numbers

    fun addNumber() {
        val nextNumber = (_numbers.maxOrNull() ?: 0) + 1
        _numbers.add(nextNumber)
    }
}
