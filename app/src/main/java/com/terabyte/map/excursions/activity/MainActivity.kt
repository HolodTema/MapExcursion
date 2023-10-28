package com.terabyte.map.excursions.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.databinding.ActivityMainBinding
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

            configureTabLayoutViewPager()
        }
    }

    private fun configureTabLayoutViewPager() {
        val adapter = ViewPagerMapsAdapter(this)
        binding.viewPagerMaps.adapter = adapter
        val tabNames = resources.getStringArray(R.array.strings_tabs_maps_names)
        TabLayoutMediator(binding.tabMaps, binding.viewPagerMaps) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }
}