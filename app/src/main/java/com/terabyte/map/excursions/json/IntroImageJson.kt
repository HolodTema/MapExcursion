package com.terabyte.map.excursions.json

import com.squareup.moshi.Json

data class IntroImageJson(
    @Json(name = "id") val id: Int,
    @Json(name = "caption") val caption: String
)
