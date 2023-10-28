package com.terabyte.map.excursions.ui.map

import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.scaleMatrix
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.activity.MapActivity
import com.terabyte.map.excursions.json.SightJson
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class SightMarker(activity: MapActivity, mapView: MapView, val sight: SightJson): Marker(mapView) {
    init {
        icon = AppCompatResources.getDrawable(activity, R.drawable.sight_marker1)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        isDraggable = false
        infoWindow = null
    }
}