package com.terabyte.map.excursions.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiHelper {
    private lateinit var moshi: Moshi
    private lateinit var mapAdapter: JsonAdapter<MapJson>
    private lateinit var introImagesAdapter: JsonAdapter<List<IntroImageJson>>

    fun getMoshi(): Moshi {
        if(!::moshi.isInitialized) {
            moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }
        return moshi
    }

    fun getMapAdapter(): JsonAdapter<MapJson> {
        if(!::mapAdapter.isInitialized) {
            mapAdapter = getMoshi().adapter(MapJson::class.java)
        }
        return mapAdapter
    }

    fun getIntroImagesAdapter(): JsonAdapter<List<IntroImageJson>> {
        if(!::introImagesAdapter.isInitialized) {
            val type = Types.newParameterizedType(List::class.java, IntroImageJson::class.java)
            introImagesAdapter = getMoshi().adapter(type)
        }
        return introImagesAdapter
    }
}