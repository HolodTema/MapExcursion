package com.terabyte.map.excursions.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class MapJson(
    @Json(name = "name") val name: String,
    @Json(name = "caption") val caption: String,
    @Json(name = "image_id") val imageId: Int,
    @Json(name = "sights") val sights: List<SightJson>
    ): Serializable
