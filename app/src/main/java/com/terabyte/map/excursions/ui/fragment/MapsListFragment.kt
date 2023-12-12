package com.terabyte.map.excursions.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.terabyte.map.excursions.INTENT_KEY_FRAGMENT_MAPS_LIST_TYPE
import com.terabyte.map.excursions.INTENT_KEY_MAP_JSON
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.databinding.FragmentMapsListBinding
import com.terabyte.map.excursions.json.MapJson
import com.terabyte.map.excursions.ui.adapter.MapsAdapter
import com.terabyte.map.excursions.viewmodel.MainViewModel

class MapsListFragment : Fragment() {
    private lateinit var type: FragmentType

    private lateinit var binding: FragmentMapsListBinding

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        type = if(savedInstanceState==null) {
            requireArguments().getSerializable(INTENT_KEY_FRAGMENT_MAPS_LIST_TYPE) as FragmentType
        }
        else {
            savedInstanceState.getSerializable(INTENT_KEY_FRAGMENT_MAPS_LIST_TYPE) as FragmentType
        }

        binding = FragmentMapsListBinding.inflate(inflater, container, false)
        binding.recyclerMaps.isVisible = false
        binding.textLabelNoMaps.isVisible = true
        when(type) {
            FragmentType.All -> {
                viewModel.liveDataMapsAll.observe(requireActivity()) {
                    binding.recyclerMaps.adapter = MapsAdapter(requireContext(), inflater, it)
                    binding.recyclerMaps.isVisible = true
                    binding.textLabelNoMaps.isVisible = false
                }
            }
            FragmentType.History -> {
                viewModel.liveDataMapsHistory.observe(requireActivity()) {
                    if(it!=null) {
                        binding.recyclerMaps.adapter = MapsAdapter(requireContext(), inflater, it)
                        binding.recyclerMaps.isVisible = true
                        binding.textLabelNoMaps.isVisible = false
                    }
                    else {
                        binding.recyclerMaps.isVisible = false
                        binding.textLabelNoMaps.isVisible = true
                    }
                }
            }
            FragmentType.Nature -> {
                viewModel.liveDataMapsNature.observe(requireActivity()) {
                    if(it!=null) {
                        binding.recyclerMaps.adapter = MapsAdapter(requireContext(), inflater, it)
                        binding.recyclerMaps.isVisible = true
                        binding.textLabelNoMaps.isVisible = false
                    }
                    else {
                        binding.recyclerMaps.isVisible = false
                        binding.textLabelNoMaps.isVisible = true
                    }
                }
            }
        }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(INTENT_KEY_FRAGMENT_MAPS_LIST_TYPE, type)
    }

    enum class FragmentType {
        All, History, Nature
    }

    companion object {
        fun create(type: FragmentType): MapsListFragment {
            val bundle = Bundle()
            bundle.putSerializable(INTENT_KEY_FRAGMENT_MAPS_LIST_TYPE, type)

            val fragment = MapsListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}