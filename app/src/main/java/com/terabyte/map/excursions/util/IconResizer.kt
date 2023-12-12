package com.terabyte.map.excursions.util

import android.util.DisplayMetrics
import android.view.WindowManager
import com.terabyte.map.excursions.LOCATION_MARKER_ICON_IN_DISPLAY_HEIGHT
import com.terabyte.map.excursions.SIGHT_MARKER_ICON_IN_DISPLAY_HEIGHT
import kotlin.reflect.KProperty

class IconResizer(windowManager: WindowManager) {
    private val displayHeight: Int

    init {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayHeight = displayMetrics.heightPixels
    }

    fun getLocationMarkerHeight(): Int {
        return displayHeight/LOCATION_MARKER_ICON_IN_DISPLAY_HEIGHT
    }

    fun getSightMarkerHeight(): Int {
        return displayHeight/SIGHT_MARKER_ICON_IN_DISPLAY_HEIGHT
    }

    companion object {
        private lateinit var instance: IconResizer

        fun getInstance(windowManager: WindowManager): IconResizer {
            return if(!::instance.isInitialized) {
                IconResizer(windowManager)
            }
            else instance
        }
    }
}