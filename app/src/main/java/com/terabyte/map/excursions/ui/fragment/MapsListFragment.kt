package com.terabyte.map.excursions.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.databinding.FragmentMapsListBinding
import com.terabyte.map.excursions.json.MapJson
import com.terabyte.map.excursions.ui.adapter.MapsAdapter

class MapsListFragment : Fragment() {
    private var maps: List<MapJson>? = null

    private lateinit var binding: FragmentMapsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if(maps!=null) {
            binding = FragmentMapsListBinding.inflate(inflater, container, false)

            binding.recyclerMaps.adapter = MapsAdapter(requireContext(), inflater, maps!!)

            return binding.root
        }
        else {
            inflater.inflate(R.layout.fragment_no_maps, container, false)
        }
    }

    companion object {
        fun create(maps: List<MapJson>?): MapsListFragment {
            val fragment = MapsListFragment()
            fragment.maps = maps
            return fragment
        }
    }
}