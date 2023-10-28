package com.terabyte.map.excursions.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.terabyte.map.excursions.R


class SightImageFragment() : Fragment() {
    private var imageId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sight_image, container, false)
    }

    companion object {
        fun create(imageId: Int): SightImageFragment {
            val fragment = SightImageFragment()
            fragment.imageId = imageId
            return fragment
        }
    }
}