package com.terabyte.map.excursions.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.terabyte.map.excursions.json.SightJson
import com.terabyte.map.excursions.ui.fragment.SightImageFragment

class ViewPagerSightImagesAdapter(
    activity: FragmentActivity,
    private val imageIds: List<Int>
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = imageIds.size

    override fun createFragment(position: Int): Fragment {
        val fragment = SightImageFragment.create()
    }
}