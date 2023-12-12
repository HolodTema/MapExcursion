package com.terabyte.map.excursions.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.terabyte.map.excursions.json.SightJson
import com.terabyte.map.excursions.ui.fragment.IntroIconFragment
import com.terabyte.map.excursions.ui.fragment.SightImageFragment

class ViewPagerIntroIconsAdapter(
    activity: FragmentActivity,
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 8

    override fun createFragment(position: Int): Fragment {
        return IntroIconFragment.create(position)
    }
}