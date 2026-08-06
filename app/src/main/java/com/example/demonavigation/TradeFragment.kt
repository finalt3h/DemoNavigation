package com.example.demonavigation

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class TradeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenLogger.log(this, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_trade, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ScreenLogger.log(this, "onViewCreated")
        view.findViewById<Button>(R.id.btnBackToPager).setOnClickListener {
            findNavController().navigateUp()
            ScreenLogger.timeStart = SystemClock.elapsedRealtimeNanos()
            ScreenLogger.log(this, "Back")
        }
    }

    override fun onStart() {
        super.onStart()
        ScreenLogger.log(this, "onStart")
    }

    override fun onResume() {
        super.onResume()
        ScreenLogger.log(this, "onResume")
    }
}
