package com.terabyte.map.excursions.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.terabyte.map.excursions.FILE_NAME_BEGINNING_SIGHT_IMAGE
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.databinding.FragmentSightImageBinding


class SightImageFragment() : Fragment() {
    private var imageId: Int? = null

    private lateinit var binding: FragmentSightImageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSightImageBinding.inflate(inflater, container, false)
        val sightImages = requireContext().assets.list("sight_images")
        var sightImageWeNeed: String? = null
        for(sightImage in sightImages!!.iterator()) {
            if(sightImage.substringBefore(".")=="${FILE_NAME_BEGINNING_SIGHT_IMAGE}${imageId}") {
                sightImageWeNeed = sightImage
                break
            }
        }
        val inputStream = requireContext().assets.open("sight_images/${sightImageWeNeed!!}")
        binding.imageSight.setImageDrawable(Drawable.createFromStream(inputStream, null))
        inputStream.close()
        return binding.root
    }

    companion object {
        fun create(imageId: Int): SightImageFragment {
            val fragment = SightImageFragment()
            fragment.imageId = imageId
            return fragment
        }
    }
}