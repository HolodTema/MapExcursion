package com.terabyte.map.excursions.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Rect
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.scaleMatrix
import androidx.lifecycle.MutableLiveData
import com.terabyte.map.excursions.INTENT_KEY_MAP_JSON
import com.terabyte.map.excursions.INTENT_KEY_MAP_START_LAT
import com.terabyte.map.excursions.INTENT_KEY_MAP_START_LON
import com.terabyte.map.excursions.INTENT_KEY_MAP_START_LON
import com.terabyte.map.excursions.INTENT_KEY_MAP_START_ZOOM
import com.terabyte.map.excursions.INTENT_KEY_SIGHT_JSON
import com.terabyte.map.excursions.LOG_TAG_DEBUG
import com.terabyte.map.excursions.MAP_DEFAULT_ZOOM
import com.terabyte.map.excursions.databinding.ActivityMapBinding
import com.terabyte.map.excursions.json.MapJson
import com.terabyte.map.excursions.ui.dialog.MapPermissionsBottomSheetDialog
import com.terabyte.map.excursions.ui.map.LocationMarker
import com.terabyte.map.excursions.ui.map.SightMarker
import com.terabyte.map.excursions.util.IconResizer
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider


class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var locationManager: LocationManager

    private val liveDataLocationByGps = MutableLiveData<Location>()
    private val liveDataLocationByNetwork = MutableLiveData<Location>()

    private var locationMarker: LocationMarker? = null

    private lateinit var map: MapJson

    private var startZoom: Double? = null
    private var startLat: Double? = null
    private var startLon: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureOnBackPressed()

        if(savedInstanceState==null) {
            map = intent.extras!!.getSerializable(INTENT_KEY_MAP_JSON) as MapJson
            if(intent.extras!!.containsKey(INTENT_KEY_MAP_START_ZOOM)) startZoom = intent.extras!!.getDouble(INTENT_KEY_MAP_START_ZOOM)
            if(intent.extras!!.containsKey(INTENT_KEY_MAP_START_LAT)) startLat = intent.extras!!.getDouble(INTENT_KEY_MAP_START_LAT)
            if(intent.extras!!.containsKey(INTENT_KEY_MAP_START_LON)) startLon = intent.extras!!.getDouble(INTENT_KEY_MAP_START_LON)
        }
        else {
            map = savedInstanceState.getSerializable(INTENT_KEY_MAP_JSON) as MapJson
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_OSM_PERMISSIONS && grantResults.isNotEmpty()) {
            var permissionsGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false
                    break
                }
            }
            if (permissionsGranted) {
                loadMap()
            } else {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS_TO_CHECK[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSIONS_TO_CHECK[1])) {
                    showBottomSheetMapPermissions(MapPermissionsBottomSheetDialog.Mode.InDialog)
                }
                else {
                    showBottomSheetMapPermissions(MapPermissionsBottomSheetDialog.Mode.InSettings)
                }

            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putSerializable(INTENT_KEY_MAP_JSON, map)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun configureOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@MapActivity, MainActivity::class.java))
            }
        })
    }

    private fun checkPermissions() {
        var allPermissionsGranted = true
        for (permission in PERMISSIONS_TO_CHECK) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_TO_CHECK,
                    REQUEST_CODE_OSM_PERMISSIONS
                )
                allPermissionsGranted = false
                break
            }
        }
        if (allPermissionsGranted) {
            loadMap()
        }
    }

    private fun loadMap() {
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        with(binding.map) {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(startZoom ?: MAP_DEFAULT_ZOOM)
        }

        val startPoint = GeoPoint(startLat ?: map.latStart, startLon ?: map.lonStart)
        binding.map.controller.setCenter(startPoint)

        configureLocation()
        configureCompass()
        configureSights()
    }

    private fun configureSights() {
        for(sight in map.sights) {
            val geoPoint = GeoPoint(sight.lat, sight.lon)
            val marker = SightMarker(this, binding.map, sight, IconResizer.getInstance(windowManager).getSightMarkerHeight()).apply {
                position = geoPoint
                setOnMarkerClickListener { _, _ ->
                    startSightInfoActivity(this)
                    true
                }
            }
            binding.map.overlays.add(marker)
        }
        binding.map.invalidate()
    }

    private fun startSightInfoActivity(sightMarker: SightMarker) {
        val intent = Intent(this, SightInfoActivity::class.java)
        intent.putExtra(INTENT_KEY_MAP_JSON, map)
        intent.putExtra(INTENT_KEY_SIGHT_JSON, sightMarker.sight)
        intent.putExtra(INTENT_KEY_MAP_START_ZOOM, binding.map.zoomLevelDouble)
        intent.putExtra(INTENT_KEY_MAP_START_LAT, binding.map.mapCenter.latitude)
        intent.putExtra(INTENT_KEY_MAP_START_LON, binding.map.mapCenter.longitude)
        startActivity(intent)
    }

    private fun configureCompass() {
        val compassOverlay = object: CompassOverlay(this, InternalCompassOrientationProvider(this), binding.map) {
            override fun drawCompass(canvas: Canvas?, bearing: Float, screenRect: Rect?) {
                screenRect?.intersects(binding.map.right-308, screenRect.top, binding.map.right-8, screenRect.bottom)
                super.drawCompass(canvas, bearing, screenRect)
            }
        }
        compassOverlay.enableCompass()
        binding.map.overlays.add(compassOverlay)
    }

    @SuppressLint("MissingPermission")
    private fun configureLocation() {
        fun showLocationMarker(locationByGps: Location, locationByNetwork: Location) {
            val location = if (locationByGps.accuracy > locationByNetwork.accuracy) locationByGps
            else locationByNetwork

            if(locationMarker!=null) {
                binding.map.overlays.remove(locationMarker)
            }
            val pointMarker = GeoPoint(location)
            locationMarker = LocationMarker(this, binding.map, IconResizer.getInstance(windowManager).getLocationMarkerHeight()).apply {
                position = pointMarker

            }
            binding.map.overlays.add(locationMarker)
            binding.map.invalidate()
        }

        fun showLocationMarker(location: Location) {
            if(locationMarker!=null) {
                binding.map.overlays.remove(locationMarker)
            }
            val pointMarker = GeoPoint(location)
            locationMarker = LocationMarker(this, binding.map, IconResizer.getInstance(windowManager).getLocationMarkerHeight()).apply {
                position = pointMarker

            }
            binding.map.overlays.add(locationMarker)
            binding.map.invalidate()
        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(hasGps) {
            val gpsLocationListener =
                LocationListener { location -> liveDataLocationByGps.value = location }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0f,
                gpsLocationListener
            )
        }
        if(hasNetwork) {
            val networkLocationListener =
                LocationListener { location -> liveDataLocationByNetwork.value = location}
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0f,
                networkLocationListener
            )
        }

        liveDataLocationByGps.observe(this) {
            Log.d(LOG_TAG_DEBUG, "Gps location: ${it.latitude.toString()} and ${it.longitude.toString()}")
            if(liveDataLocationByNetwork.value!=null) {
                showLocationMarker(it, liveDataLocationByNetwork.value!!)
            }
            else {
                showLocationMarker(it)
            }
        }
        liveDataLocationByNetwork.observe(this) {
            Log.d(LOG_TAG_DEBUG, "Network location: ${it.latitude.toString()} and ${it.longitude.toString()}")
            if(liveDataLocationByGps.value!=null) {
                showLocationMarker(liveDataLocationByGps.value!!, it)
            }
            else {
                showLocationMarker(it)
            }
        }
    }

    private fun showBottomSheetMapPermissions(mode: MapPermissionsBottomSheetDialog.Mode) {
        val dialog = if(mode==MapPermissionsBottomSheetDialog.Mode.InDialog) {
            MapPermissionsBottomSheetDialog.newInstance(mode) {
                checkPermissions()
            }
        }
        else {
            MapPermissionsBottomSheetDialog.newInstance(mode) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
        dialog.show(supportFragmentManager, MapPermissionsBottomSheetDialog.FRAGMENT_TAG)
    }


    companion object {
        val PERMISSIONS_TO_CHECK = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        const val REQUEST_CODE_OSM_PERMISSIONS = 0
    }
}