package com.example.socialx.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.socialx.ui.login.SigninFragment
import com.example.socialx.ui.login.SignupFragment

class ViewPagerAdapter( fragmentManager:FragmentManager, lifecycle:Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private val itemCount=2
    override fun getItemCount(): Int {
        return itemCount
    }
    override fun createFragment(position: Int): Fragment {
        return if (position == 1) SignupFragment() else SigninFragment()
    }
}//https://newsapi.org/v2/top-headlines?country=us&apiKey=868a0d325e124aec83e4b0e16eb8749b