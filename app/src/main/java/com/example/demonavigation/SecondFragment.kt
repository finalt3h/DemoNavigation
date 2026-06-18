package com.example.demonavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SecondFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userName = arguments?.getString("userName").orEmpty()
        view.findViewById<TextView>(R.id.tvUserDetail).text =
            getString(R.string.user_detail_message, userName)

        view.findViewById<Button>(R.id.btnBackToFirstFromSecond).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
