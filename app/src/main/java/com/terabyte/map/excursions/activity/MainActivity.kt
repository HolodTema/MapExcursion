package com.terabyte.map.excursions.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.terabyte.map.excursions.INTENT_KEY_MAP_JSON
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.databinding.ActivityMainBinding
import com.terabyte.map.excursions.json.MapJson
import com.terabyte.map.excursions.ui.adapter.ViewPagerMapsAdapter
import com.terabyte.map.excursions.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.liveDataMapsAll.observe(this) {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            configureTabLayoutViewPager(it, viewModel.liveDataMapsHistory.value, viewModel.liveDataMapsNature.value)
        }
    }

    private fun configureTabLayoutViewPager(mapsAll: List<MapJson>, mapsHistory: List<MapJson>?, mapsNature: List<MapJson>?) {
        val adapter = ViewPagerMapsAdapter(this, mapsAll, mapsHistory, mapsNature)
        binding.viewPagerMaps.adapter = adapter
        val tabNames = resources.getStringArray(R.array.strings_tabs_maps_names)
        TabLayoutMediator(binding.tabMaps, binding.viewPagerMaps) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }
}