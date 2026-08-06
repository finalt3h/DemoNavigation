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
import androidx.fragment.app.commitNow
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class Fragment1A : BasePageAFragment("Fragment1A", Color.rgb(239, 83, 80)) {
    override val pageViewModel: Fragment1AViewModel by viewModels()
    override fun createSectorFragments(): List<Fragment> = listOf(
        FragmentSector1A(), FragmentSector1B(), FragmentSector1C(), FragmentSector1D()
    )
}

class Fragment2A : BasePageAFragment("Fragment2A", Color.rgb(66, 165, 245)) {
    override val pageViewModel: Fragment2AViewModel by viewModels()
    override fun createSectorFragments(): List<Fragment> = listOf(
        FragmentSector2A(), FragmentSector2B(), FragmentSector2C(), FragmentSector2D()
    )
}

class Fragment3A : BasePageAFragment("Fragment3A", Color.rgb(102, 187, 106)) {
    override val pageViewModel: Fragment3AViewModel by viewModels()
    override fun createSectorFragments(): List<Fragment> = listOf(
        FragmentSector3A(), FragmentSector3B(), FragmentSector3C(), FragmentSector3D()
    )
}

class Fragment4A : BasePageAFragment("Fragment4A", Color.rgb(255, 167, 38)) {
    override val pageViewModel: Fragment4AViewModel by viewModels()
    override fun createSectorFragments(): List<Fragment> = listOf(
        FragmentSector4A(), FragmentSector4B(), FragmentSector4C(), FragmentSector4D()
    )
}

class Fragment5A : BasePageAFragment("Fragment5A", Color.rgb(171, 71, 188)) {
    override val pageViewModel: Fragment5AViewModel by viewModels()
    override fun createSectorFragments(): List<Fragment> = listOf(
        FragmentSector5A(), FragmentSector5B(), FragmentSector5C(), FragmentSector5D()
    )
}

abstract class BasePageAFragment(
    private val pageName: String,
    private val pageColor: Int
) : Fragment() {
    protected abstract val pageViewModel: PageAViewModel
    protected abstract fun createSectorFragments(): List<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Truy cập lần đầu để ViewModel tạo và giữ chuỗi nặng của riêng page này.
        pageViewModel.heavyTextData.length
        ScreenLogger.log(this, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
//        runBlocking {
//            delay(1000)
//        }
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

                addView(TextView(context).apply {
                    text = "${pageViewModel.javaClass.simpleName}: " +
                        "${pageViewModel.heavyTextData.length} ký tự (~1 MB)"
                    textSize = 14f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    setPadding(0, 0, 0, dp(10))
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

                addView(TextView(context).apply {
                    text = "4 child fragments của $pageName"
                    textSize = 16f
                    setTextColor(Color.WHITE)
                    setTypeface(typeface, Typeface.BOLD)
                    setPadding(0, dp(18), 0, dp(8))
                }, matchWrap())

                SECTOR_CONTAINER_IDS.forEach { containerId ->
                    addView(FrameLayout(context).apply {
                        id = containerId
                    }, LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(56)
                    ).apply { topMargin = dp(6) })
                }
            }, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ScreenLogger.log(this, "onViewCreated")
        addSectorFragmentsIfNeeded()
    }

    private fun addSectorFragmentsIfNeeded() {
        val sectorTagPrefix = "${javaClass.simpleName}_sector_"
        if (childFragmentManager.findFragmentByTag("${sectorTagPrefix}0") != null) return

        val sectors = createSectorFragments()
        childFragmentManager.commitNow {
            setReorderingAllowed(true)
            sectors.forEachIndexed { index, sectorFragment ->
                add(
                    SECTOR_CONTAINER_IDS[index],
                    sectorFragment,
                    "$sectorTagPrefix$index"
                )
            }
        }
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
        val SECTOR_CONTAINER_IDS = intArrayOf(
            R.id.sectorContainerA,
            R.id.sectorContainerB,
            R.id.sectorContainerC,
            R.id.sectorContainerD
        )
    }
}
