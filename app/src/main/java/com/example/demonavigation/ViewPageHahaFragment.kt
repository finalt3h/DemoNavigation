package com.example.demonavigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPageHahaFragment : Fragment() {
    private var selectedPage = 0
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ScreenLogger.log(this, "onCreate")
        selectedPage = savedInstanceState?.getInt(KEY_SELECTED_PAGE) ?: selectedPage
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_view_page_haha, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ScreenLogger.log(this, "onViewCreated")
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerHaha).apply {
            offscreenPageLimit = 5
            adapter = HahaPagerAdapter(this@ViewPageHahaFragment)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    selectedPage = position
                }
            })
        }
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayoutHaha)
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = TAB_NAMES[position]
        }.also { it.attach() }
        viewPager.setCurrentItem(selectedPage, false)
    }

    override fun onStart() {
        super.onStart()
        ScreenLogger.log(this, "onStart")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SELECTED_PAGE, selectedPage)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        ScreenLogger.log(this, "onResume")
    }

    override fun onDestroyView() {
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        super.onDestroyView()
    }

    private class HahaPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 5

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> Fragment1A()
            1 -> Fragment2A()
            2 -> Fragment3A()
            3 -> Fragment4A()
            else -> Fragment5A()
        }
    }

    private companion object {
        const val KEY_SELECTED_PAGE = "selected_page"
        val TAB_NAMES = listOf(
            "Fragment1A",
            "Fragment2A",
            "Fragment3A",
            "Fragment4A",
            "Fragment5A"
        )
    }
}
