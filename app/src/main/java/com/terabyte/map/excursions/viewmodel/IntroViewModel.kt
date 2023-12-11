package com.terabyte.map.excursions.viewmodel

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.json.MoshiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class IntroViewModel(application: Application) : AndroidViewModel(application) {
    private val application = getApplication<Application>()
    val liveDataHaveIntroImages = MutableLiveData<Boolean>()
    var fragmentNumber = 0

    private lateinit var introImagesPairs: List<Pair<String, Drawable>>

    init {
        CoroutineScope(Dispatchers.Main).launch {
            val deferred = async(Dispatchers.IO) {
                return@async chooseIntroImages()
            }
            introImagesPairs = deferred.await()
            liveDataHaveIntroImages.value = true
        }
    }

    fun getCurrentIntroImage(): Pair<String, Drawable> {
        return introImagesPairs[fragmentNumber]
    }

    private fun chooseIntroImages(): List<Pair<String, Drawable>> {
        val introImagesFromServer = loadIntroImages()
        return introImagesFromServer
            ?: return listOf(
                "Залив Провал, Байкал" to application.getDrawable(R.drawable.intro_image_default1)!!,
                "Пик Любви, Аршан" to application.getDrawable(R.drawable.intro_image_default2)!!,
                "Тажеранские степи, Байкал" to application.getDrawable(R.drawable.intro_image_default3)!!
            )
    }


    private fun loadIntroImages(): List<Pair<String, Drawable>>? {
        fun analyseIntroImageFilenames(): Map<Int, String> {
            val result = mutableMapOf<Int, String>()
            val introImageFilenames = application.assets.list("intro_images/")
            for(introImageFileName in introImageFilenames!!.iterator()) {
                val nameWithoutFormat = introImageFileName.split(".")[0]
                var id = ""
                for (i in nameWithoutFormat.length-1 downTo 0) {
                    if (nameWithoutFormat[i] in "0123456789") {
                        id+=nameWithoutFormat[i]
                    }
                    else {
                        break
                    }
                }
                if(id.isNotEmpty()) result[id.toInt()] = introImageFileName
            }
            return result
        }

        fun getThreeDifferentRandomValues(intRange: IntRange): List<Int> {
            val result = arrayListOf<Int>()
            while(result.size<3) {
                val newValue = intRange.random()
                if (newValue !in result) result.add(newValue)
            }
            return result
        }

        try {
            // TODO: in the future there will be GET request of random intro images from server
            var jsonText = ""
            val inputStream = application.assets.open("intro_images/intro_images.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
            var line = bufferedReader.readLine()
            while (line!=null) {
                jsonText += line
                line = bufferedReader.readLine()
            }

            val introImages = MoshiHelper.getIntroImagesAdapter().fromJson(jsonText)

            inputStream.close()
            bufferedReader.close()

            if (introImages.isNullOrEmpty()) return null
            else {
                val analysedFilenamesMap = analyseIntroImageFilenames()
                val result = arrayListOf<Pair<String, Drawable>>()
                var inputStreamImageFiles: InputStream?
                var drawable: Drawable?
                val randomImageIndexes = getThreeDifferentRandomValues(introImages.indices)
                for(randomImageIndex in randomImageIndexes) {
                    inputStreamImageFiles = application.assets.open("intro_images/${analysedFilenamesMap[introImages[randomImageIndex].id]}")
                    drawable = Drawable.createFromStream(inputStreamImageFiles, null)
                    if(drawable==null) return null
                    result.add(introImages[randomImageIndex].caption to drawable)
                }
                return result
            }
        }
        catch (e: java.lang.Exception) {
            return null
        }
    }


    class Factory(private val application: Application): ViewModelProvider.AndroidViewModelFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return IntroViewModel(application) as T
        }
    }

    companion object {
    }
}