package com.terabyte.map.excursions.ui.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.terabyte.map.excursions.FILE_NAME_BEGINNING_SIGHT_IMAGE
import com.terabyte.map.excursions.INTENT_KEY_MAP_JSON
import com.terabyte.map.excursions.activity.MapActivity
import com.terabyte.map.excursions.databinding.ItemMapBinding
import com.terabyte.map.excursions.json.MapJson

class MapsAdapter(private val context: Context, private val inflater: LayoutInflater, private val maps: List<MapJson>): RecyclerView.Adapter<MapsAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemMapBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return maps.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val map = maps[position]
        with(holder.binding) {
            textMapName.text = map.name
            textMapCaption.text = map.caption

            root.setOnClickListener {
                val intent = Intent(context, MapActivity::class.java)
                intent.putExtra(INTENT_KEY_MAP_JSON, map)
                context.startActivity(intent)
            }

            val sightImages = context.assets.list("sight_images")
            var sightImageWeNeed: String? = null
            for(sightImage in sightImages!!.iterator()) {
                if(sightImage.substringBefore(".")=="$FILE_NAME_BEGINNING_SIGHT_IMAGE${map.imageId}") {
                    sightImageWeNeed = sightImage
                    break
                }
            }
            val inputStream = context.assets.open("sight_images/${sightImageWeNeed!!}")
            imageMap.setImageDrawable(Drawable.createFromStream(inputStream, null))
            inputStream.close()
        }
    }

    class Holder(internal val binding: ItemMapBinding): RecyclerView.ViewHolder(binding.root)
}