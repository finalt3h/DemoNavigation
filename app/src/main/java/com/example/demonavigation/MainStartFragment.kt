package com.example.demonavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class MainStartFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenLogger.log(this, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main_start, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ScreenLogger.log(this, "onViewCreated")
        view.findViewById<Button>(R.id.btnOpenViewPager).setOnClickListener {
            findNavController().navigate(
                R.id.action_mainStartFragment_to_viewPageHahaFragment
            )
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
