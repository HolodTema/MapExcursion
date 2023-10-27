package com.terabyte.map.excursions.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.terabyte.map.excursions.LOG_TAG_DEBUG
import com.terabyte.map.excursions.MAP_DEFAULT_ZOOM
import com.terabyte.map.excursions.MAP_START_POINT_LAT
import com.terabyte.map.excursions.MAP_START_POINT_LON
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.databinding.ActivityMapBinding
import com.terabyte.map.excursions.ui.dialog.MapPermissionsBottomSheetDialog
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding
    private lateinit var locationManager: LocationManager

    private val liveDataLocationByGps = MutableLiveData<Location>()
    private val liveDataLocationByNetwork = MutableLiveData<Location>()

    private var locationMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            controller.setZoom(MAP_DEFAULT_ZOOM)
        }

        val startPoint = GeoPoint(MAP_START_POINT_LAT, MAP_START_POINT_LON)
        binding.map.controller.setCenter(startPoint)

        configureLocation()
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
            locationMarker = Marker(binding.map).apply {
                icon = AppCompatResources.getDrawable(this@MapActivity, R.drawable.location_marker)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                position = pointMarker
                isDraggable = false
                infoWindow = null

            }
            binding.map.overlays.add(locationMarker)
            binding.map.invalidate()
        }

        fun showLocationMarker(location: Location) {
            if(locationMarker!=null) {
                binding.map.overlays.remove(locationMarker)
            }
            val pointMarker = GeoPoint(location)
            locationMarker = Marker(binding.map).apply {
                icon = AppCompatResources.getDrawable(this@MapActivity, R.drawable.location_marker)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                position = pointMarker
                isDraggable = false
                infoWindow = null

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