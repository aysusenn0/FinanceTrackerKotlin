package com.aysusen.financetracker.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.aysusen.financetracker.R
import com.aysusen.financetracker.adapter.ViewPagerAdapter
import com.aysusen.financetracker.databinding.FragmentAddFinanceBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AddFinanceFragment : Fragment(R.layout.fragment_add_finance) {
    private lateinit var binding: FragmentAddFinanceBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddFinanceBinding.bind(view)
        tabLayout = binding.tabLayout
        viewPager2 = binding.viewPager
        val adapter = ViewPagerAdapter(this)

        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Gelir"
                }

                1 -> {
                    tab.text = "Gider"
                }

                else -> ""
            }
        }.attach()

    }
}