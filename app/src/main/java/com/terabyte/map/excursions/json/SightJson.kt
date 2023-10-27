package com.terabyte.map.excursions.json

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class SightJson(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "caption") val caption: String,
    @Json(name = "image_ids") val imageIds: List<Int>
): Serializable
