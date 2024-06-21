package com.app.barterbuddy.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.app.barterbuddy.ui.profile.InterestFragment
import com.app.barterbuddy.ui.profile.PostsFragment
import com.app.barterbuddy.ui.profile.TransactionFragment

class SectionPagerAdapter(activity: FragmentActivity, private val uid: String): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> PostsFragment.newInstance(uid)
            1 -> InterestFragment.newInstance(uid)
            2 -> TransactionFragment.newInstance(uid)
            else -> throw IllegalArgumentException("Invalid Menu")
        }
    }

}