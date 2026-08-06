package com.example.demonavigation

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentSector1A : BaseFragmentSector("FragmentSector1A", "#FFCDD2")
class FragmentSector1B : BaseFragmentSector("FragmentSector1B", "#EF9A9A")
class FragmentSector1C : BaseFragmentSector("FragmentSector1C", "#E57373")
class FragmentSector1D : BaseFragmentSector("FragmentSector1D", "#EF5350")

class FragmentSector2A : BaseFragmentSector("FragmentSector2A", "#BBDEFB")
class FragmentSector2B : BaseFragmentSector("FragmentSector2B", "#90CAF9")
class FragmentSector2C : BaseFragmentSector("FragmentSector2C", "#64B5F6")
class FragmentSector2D : BaseFragmentSector("FragmentSector2D", "#42A5F5")

class FragmentSector3A : BaseFragmentSector("FragmentSector3A", "#C8E6C9")
class FragmentSector3B : BaseFragmentSector("FragmentSector3B", "#A5D6A7")
class FragmentSector3C : BaseFragmentSector("FragmentSector3C", "#81C784")
class FragmentSector3D : BaseFragmentSector("FragmentSector3D", "#66BB6A")

class FragmentSector4A : BaseFragmentSector("FragmentSector4A", "#FFE0B2")
class FragmentSector4B : BaseFragmentSector("FragmentSector4B", "#FFCC80")
class FragmentSector4C : BaseFragmentSector("FragmentSector4C", "#FFB74D")
class FragmentSector4D : BaseFragmentSector("FragmentSector4D", "#FFA726")

class FragmentSector5A : BaseFragmentSector("FragmentSector5A", "#E1BEE7")
class FragmentSector5B : BaseFragmentSector("FragmentSector5B", "#CE93D8")
class FragmentSector5C : BaseFragmentSector("FragmentSector5C", "#BA68C8")
class FragmentSector5D : BaseFragmentSector("FragmentSector5D", "#AB47BC")

abstract class BaseFragmentSector(
    private val sectorName: String,
    colorHex: String
) : Fragment() {
    private val sectorColor = Color.parseColor(colorHex)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenLogger.log(this, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = TextView(requireContext()).apply {
        text = sectorName
        textSize = 16f
        setTextColor(Color.rgb(35, 35, 35))
        setTypeface(typeface, Typeface.BOLD)
        gravity = Gravity.CENTER_VERTICAL
        setPadding(dp(16), 0, dp(16), 0)
        background = GradientDrawable().apply {
            setColor(sectorColor)
            cornerRadius = dp(10).toFloat()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ScreenLogger.log(this, "onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        ScreenLogger.log(this, "onStart")
    }

    override fun onResume() {
        super.onResume()
        ScreenLogger.log(this, "onResume")
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
