package com.terabyte.map.excursions.ui.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.terabyte.map.excursions.activity.MainActivity
import com.terabyte.map.excursions.databinding.FragmentIntro0Binding
import com.terabyte.map.excursions.databinding.FragmentIntro1Binding
import com.terabyte.map.excursions.databinding.FragmentIntro2Binding
import com.terabyte.map.excursions.ui.adapter.ViewPagerIntroIconsAdapter
import com.terabyte.map.excursions.viewmodel.IntroViewModel

class IntroFragment : Fragment() {
    private val viewModel by activityViewModels<IntroViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return when(viewModel.fragmentNumber) {
            0 -> {
                val binding = FragmentIntro0Binding.inflate(layoutInflater, container, false)

                return binding.root
            }
            1 -> {
                val binding = FragmentIntro1Binding.inflate(layoutInflater, container, false)
                val adapter = ViewPagerIntroIconsAdapter(requireActivity())
                binding.viewPagerIntroIcons.adapter = adapter
                TabLayoutMediator(binding.tabIntroIcons, binding.viewPagerIntroIcons) { tab, position ->

                }.attach()
                return binding.root
            }
            2 -> {
                val binding = FragmentIntro2Binding.inflate(layoutInflater, container, false)
                binding.buttonLetsGo.setOnClickListener {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
                return binding.root
            }
            else -> {
                val binding = FragmentIntro0Binding.inflate(layoutInflater, container, false)
                return binding.root
            }
        }
    }

}