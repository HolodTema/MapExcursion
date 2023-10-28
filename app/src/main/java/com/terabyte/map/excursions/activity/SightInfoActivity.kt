package com.terabyte.map.excursions.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.terabyte.map.excursions.INTENT_KEY_MAP_JSON
import com.terabyte.map.excursions.INTENT_KEY_MAP_START_LAT
import com.terabyte.map.excursions.INTENT_KEY_MAP_START_LON
import com.terabyte.map.excursions.INTENT_KEY_MAP_START_ZOOM
import com.terabyte.map.excursions.INTENT_KEY_SIGHT_JSON
import com.terabyte.map.excursions.databinding.ActivitySightInfoBinding
import com.terabyte.map.excursions.json.MapJson
import com.terabyte.map.excursions.json.SightJson
import com.terabyte.map.excursions.viewmodel.SightInfoViewModel

class SightInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySightInfoBinding
    private lateinit var viewModel: SightInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySightInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[SightInfoViewModel::class.java]

        if(savedInstanceState==null) {
            viewModel.map = intent.extras!!.getSerializable(INTENT_KEY_MAP_JSON) as MapJson
            viewModel.sight = intent.extras!!.getSerializable(INTENT_KEY_SIGHT_JSON) as SightJson
            viewModel.startZoom = intent.extras!!.getDouble(INTENT_KEY_MAP_START_ZOOM)
            viewModel.startLat = intent.extras!!.getDouble(INTENT_KEY_MAP_START_LAT)
            viewModel.startLon = intent.extras!!.getDouble(INTENT_KEY_MAP_START_LON)
        }

        configureOnBackPressed()
        binding.buttonBack.setOnClickListener {
            startMapActivity()
        }

        binding.tabSightImages.setupWithViewPager(binding.viewPagerSightImages, true)
    }

    private fun configureOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startMapActivity()
            }
        })
    }

    private fun startMapActivity() {
        val intent = Intent(this, MapActivity::class.java)
        intent.putExtra(INTENT_KEY_MAP_JSON, viewModel.map)
        intent.putExtra(INTENT_KEY_MAP_START_ZOOM, viewModel.startZoom)
        intent.putExtra(INTENT_KEY_MAP_START_LAT, viewModel.startLat)
        intent.putExtra(INTENT_KEY_MAP_START_LON, viewModel.startLon)
        startActivity(intent)
    }
}