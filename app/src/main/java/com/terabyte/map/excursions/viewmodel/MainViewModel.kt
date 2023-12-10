package com.terabyte.map.excursions.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.terabyte.map.excursions.json.MapJson
import com.terabyte.map.excursions.json.MoshiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Stream
import kotlin.streams.toList

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val application = getApplication<Application>()

    val liveDataMapsAll = MutableLiveData<List<MapJson>>()
    val liveDataMapsHistory = MutableLiveData<List<MapJson>?>()
    val liveDataMapsNature = MutableLiveData<List<MapJson>?>()

    init {
        CoroutineScope(Dispatchers.Main).launch {
            val deferred = async<List<List<MapJson>?>>(Dispatchers.IO) {
                val result = arrayListOf<List<MapJson>?>()
                val mapsHistory = loadHistoryMaps()
                val mapsNature = loadNatureMaps()
                val mapsAll = if(mapsHistory!=null && mapsNature!=null) {
                    Stream.concat(mapsHistory.stream(), mapsNature.stream()).toList()
                }
                else if(mapsHistory==null && mapsNature!=null){
                    mapsNature
                }
                else if(mapsNature==null && mapsHistory!=null) {
                    mapsHistory
                }
                else {
                    throw AtLeastOneMapJsonExistsException()
                }

                result.add(mapsHistory)
                result.add(mapsNature)
                result.add(mapsAll)
                return@async result
            }
            val maps = deferred.await()
            liveDataMapsHistory.value = maps[0]
            liveDataMapsNature.value = maps[1]
            liveDataMapsAll.value = maps[2]
        }
    }

    private fun loadHistoryMaps(): List<MapJson>? {
        val result = arrayListOf<MapJson>()
        var jsonText: String?
        var inputStream: InputStream?
        var bufferedReader: BufferedReader?
        try {
            for (fileName in application.assets.list("maps/history")!!.iterator()) {
                jsonText = ""
                inputStream = application.assets.open("maps/history/$fileName")
                bufferedReader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                var line = bufferedReader.readLine()
                while (line!=null) {
                    jsonText += line
                    line = bufferedReader.readLine()
                }

                val mapJson = MoshiHelper.getMapAdapter().fromJson(jsonText)
                if (mapJson != null) result.add(mapJson)
                inputStream.close()
                bufferedReader.close()
            }
            return if(result.isEmpty()) null
            else result
        }
        catch (e: java.lang.Exception) {
            return null
        }
    }

    private fun loadNatureMaps(): List<MapJson>? {
        val result = arrayListOf<MapJson>()
        var jsonText: String?
        var inputStream: InputStream?
        var bufferedReader: BufferedReader?
        try {
            for (fileName in application.assets.list("maps/nature")!!.iterator()) {
                jsonText = ""
                inputStream = application.assets.open("maps/nature/$fileName")

                bufferedReader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
                var line = bufferedReader.readLine()
                while (line!=null) {
                    jsonText += line
                    line = bufferedReader.readLine()
                }

                val mapJson = MoshiHelper.getMapAdapter().fromJson(jsonText)
                if (mapJson != null) result.add(mapJson)
                inputStream.close()
            }
            return if(result.isEmpty()) null
            else result
        }
        catch (e: java.lang.Exception) {
            return null
        }
    }

    class Factory(private val application: Application): ViewModelProvider.AndroidViewModelFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(application) as T
        }
    }

    class AtLeastOneMapJsonExistsException: Exception()
}