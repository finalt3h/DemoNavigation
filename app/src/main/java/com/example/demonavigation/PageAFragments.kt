package com.example.demonavigation

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class Fragment1A : BasePageAFragment("Fragment1A", Color.rgb(239, 83, 80))
class Fragment2A : BasePageAFragment("Fragment2A", Color.rgb(66, 165, 245))
class Fragment3A : BasePageAFragment("Fragment3A", Color.rgb(102, 187, 106))
class Fragment4A : BasePageAFragment("Fragment4A", Color.rgb(255, 167, 38))
class Fragment5A : BasePageAFragment("Fragment5A", Color.rgb(171, 71, 188))

abstract class BasePageAFragment(
    private val pageName: String,
    private val pageColor: Int
) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenLogger.log(this, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        return ScrollView(context).apply {
            setBackgroundColor(pageColor)
            isFillViewport = true
            addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(20), dp(20), dp(20), dp(20))

                addView(TextView(context).apply {
                    text = pageName
                    textSize = 30f
                    setTextColor(Color.WHITE)
                    setTypeface(typeface, Typeface.BOLD)
                    gravity = Gravity.CENTER
                    setPadding(0, dp(12), 0, dp(20))
                }, matchWrap())

                addView(createGrid10x10(), matchWrap(dp(10)))

                addView(Button(context).apply {
                    text = "Mở TradeFragment từ $pageName"
                    isAllCaps = false
                    setOnClickListener {
                        requireParentFragment().findNavController().navigate(
                            R.id.action_viewPageHahaFragment_to_tradeFragment
                        )
                    }
                }, matchWrap(dp(18)))
            }, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ScreenLogger.log(this, "onViewCreated")
    }

    private fun createGrid10x10(): View {
        val context = requireContext()
        return HorizontalScrollView(context).apply {
            isHorizontalScrollBarEnabled = true
            addView(GridLayout(context).apply {
                columnCount = GRID_SIZE
                rowCount = GRID_SIZE
                orientation = GridLayout.HORIZONTAL
                setPadding(dp(4), dp(4), dp(4), dp(4))

                repeat(GRID_SIZE * GRID_SIZE) { index ->
                    addView(createNestedGridItem(index + 1), GridLayout.LayoutParams().apply {
                        width = dp(64)
                        height = dp(64)
                        setMargins(dp(3), dp(3), dp(3), dp(3))
                    })
                }
            })
        }
    }

    private fun createNestedGridItem(number: Int): View {
        val context = requireContext()
        return FrameLayout(context).apply {
            background = roundedBackground(Color.argb(225, 255, 255, 255), 10f)
            setPadding(dp(4), dp(4), dp(4), dp(4))

            addView(LinearLayout(context).apply {
                gravity = Gravity.CENTER
                orientation = LinearLayout.VERTICAL
                background = roundedBackground(Color.argb(40, 0, 0, 0), 8f)
                setPadding(dp(3), dp(3), dp(3), dp(3))

                addView(FrameLayout(context).apply {
                    background = roundedBackground(Color.WHITE, 6f)
                    setPadding(dp(2), dp(2), dp(2), dp(2))

                    addView(TextView(context).apply {
                        text = number.toString()
                        textSize = 14f
                        setTextColor(pageColor)
                        setTypeface(typeface, Typeface.BOLD)
                        gravity = Gravity.CENTER
                    }, frameMatchMatch())
                }, LinearLayout.LayoutParams(dp(42), dp(42)))
            }, frameMatchMatch())
        }
    }

    private fun roundedBackground(color: Int, radiusDp: Float) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radiusDp.toInt()).toFloat()
    }

    private fun matchWrap(topMargin: Int = 0) = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply { this.topMargin = topMargin }

    private fun frameMatchMatch() = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onStart() {
        super.onStart()
        ScreenLogger.log(this, "onStart")
    }

    override fun onResume() {
        super.onResume()
        ScreenLogger.log(this, "onResume")
    }

    private companion object {
        const val GRID_SIZE = 10
    }
}
