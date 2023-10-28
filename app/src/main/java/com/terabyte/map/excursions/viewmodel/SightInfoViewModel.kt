package com.terabyte.map.excursions.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.terabyte.map.excursions.json.MapJson
import com.terabyte.map.excursions.json.MoshiHelper
import com.terabyte.map.excursions.json.SightJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.stream.Stream
import kotlin.streams.toList

class SightInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val application = getApplication<Application>()

    lateinit var map: MapJson
    lateinit var sight: SightJson
    var startZoom: Double? = null
    var startLat: Double? = null
    var startLon: Double? = null

    class Factory(private val application: Application): ViewModelProvider.AndroidViewModelFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SightInfoViewModel(application) as T
        }
    }
}