package com.terabyte.map.excursions.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.terabyte.map.excursions.INTENT_KEY_FRAGMENT_INTRO_ICON_NUMBER
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.databinding.FragmentIntroIconBinding


class IntroIconFragment() : Fragment() {
    private var iconNumber: Int? = null

    private lateinit var binding: FragmentIntroIconBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(savedInstanceState!=null) {
            iconNumber = savedInstanceState.getInt(INTENT_KEY_FRAGMENT_INTRO_ICON_NUMBER)
        }
        binding = FragmentIntroIconBinding.inflate(inflater, container, false)
        binding.imageIntroIcon.setImageResource(icons[iconNumber!!])
        val captions = resources.getStringArray(R.array.marker_captions)
        binding.textLabelIntroIcon.text = captions[iconNumber!!]
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(INTENT_KEY_FRAGMENT_INTRO_ICON_NUMBER, iconNumber!!)
    }

    companion object {
        private val icons = arrayOf(R.drawable.location_marker,
            R.drawable.sight_marker_landscape,
            R.drawable.sight_marker_museum,
            R.drawable.sight_marker_nature,
            R.drawable.sight_marker_from_history,
            R.drawable.sight_marker_building,
            R.drawable.sight_marker_parks,
            R.drawable.sight_marker_other)

        fun create(iconNumber: Int): IntroIconFragment {
            val fragment = IntroIconFragment()
            fragment.iconNumber = iconNumber
            return fragment
        }
    }
}