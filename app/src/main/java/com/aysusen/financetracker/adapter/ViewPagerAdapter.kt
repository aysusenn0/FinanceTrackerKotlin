package com.aysusen.financetracker.adapter


import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aysusen.financetracker.view.ExpenseFragment
import com.aysusen.financetracker.view.IncomeFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IncomeFragment()
            1 -> ExpenseFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}



