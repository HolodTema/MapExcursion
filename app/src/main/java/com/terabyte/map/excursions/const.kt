package com.terabyte.map.excursions

const val MAP_DEFAULT_ZOOM = 13.0
const val LOG_TAG_DEBUG = "myDebug"

const val MAP_TYPES = 3

const val INTENT_KEY_MAP_JSON = "mapJson"
const val INTENT_KEY_FRAGMENT_MAPS_LIST_TYPE = "mapsListType"
const val INTENT_KEY_MAP_START_ZOOM = "mapStartZoom"
const val INTENT_KEY_MAP_START_LAT = "mapStartLat"
const val INTENT_KEY_MAP_START_LON = "mapStartLon"
const val INTENT_KEY_SIGHT_JSON = "sightJson"
const val INTENT_KEY_FRAGMENT_INTRO_ICON_NUMBER = "introIconNumber"

const val FILE_NAME_BEGINNING_SIGHT_IMAGE = "sight_image"

val SIGHT_MARKER_TYPES = mapOf(
    0 to R.drawable.sight_marker_other,
    1 to R.drawable.sight_marker_landscape,
    2 to R.drawable.sight_marker_museum,
    3 to R.drawable.sight_marker_from_history,
    4 to R.drawable.sight_marker_parks,
    5 to R.drawable.sight_marker_building,
    6 to R.drawable.sight_marker_nature
)

const val SIGHT_MARKER_ICON_IN_DISPLAY_HEIGHT = 16
const val LOCATION_MARKER_ICON_IN_DISPLAY_HEIGHT = 18