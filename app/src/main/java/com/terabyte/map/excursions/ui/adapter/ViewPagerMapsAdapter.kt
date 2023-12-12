package com.terabyte.map.excursions.ui.adapter

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.terabyte.map.excursions.MAP_TYPES
import com.terabyte.map.excursions.json.MapJson
import com.terabyte.map.excursions.json.SightJson
import com.terabyte.map.excursions.ui.fragment.MapsListFragment

class ViewPagerMapsAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = MAP_TYPES

    override fun createFragment(position: Int): Fragment {
        return when(position) { //all
            0 -> {
                MapsListFragment.create(MapsListFragment.FragmentType.All)
            }

            1 -> { //history
                MapsListFragment.create(MapsListFragment.FragmentType.History)
            }

            2 -> { //nature
                MapsListFragment.create(MapsListFragment.FragmentType.Nature)
            }
            else -> {
                MapsListFragment.create(MapsListFragment.FragmentType.All)
            }
        }

    }
}