package com.terabyte.map.excursions.ui.map

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.scaleMatrix
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.SIGHT_MARKER_TYPES
import com.terabyte.map.excursions.activity.MapActivity
import com.terabyte.map.excursions.json.SightJson
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LocationMarker(private val activity: MapActivity, mapView: MapView, private val iconHeight: Int): Marker(mapView) {
    init {
        val iconBitmap = BitmapFactory.decodeResource(mResources, R.drawable.location_marker)
        icon = resizeIcon(iconBitmap)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        isDraggable = false
        infoWindow = null
    }

    private fun resizeIcon(sourceIcon: Bitmap): BitmapDrawable {
        val aspectRatio = sourceIcon.width.toDouble() / sourceIcon.height.toDouble()
        val iconWidth = (iconHeight*aspectRatio).toInt()
        return BitmapDrawable(activity.resources, Bitmap.createScaledBitmap(sourceIcon, iconWidth, iconHeight, false))
    }
}