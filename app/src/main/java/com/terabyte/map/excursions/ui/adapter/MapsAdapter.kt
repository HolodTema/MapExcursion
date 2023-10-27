package com.terabyte.map.excursions.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

//            imageMap.setImageURI()

            root.setOnClickListener {
                val intent = Intent(context, MapActivity::class.java)
                intent.putExtra(INTENT_KEY_MAP_JSON, map)
                context.startActivity(intent)
            }
        }
    }

    class Holder(internal val binding: ItemMapBinding): RecyclerView.ViewHolder(binding.root)
}