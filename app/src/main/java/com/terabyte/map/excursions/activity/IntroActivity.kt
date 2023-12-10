package com.terabyte.map.excursions.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.terabyte.map.excursions.R
import com.terabyte.map.excursions.databinding.ActivityIntroBinding
import com.terabyte.map.excursions.ui.fragment.IntroFragment
import com.terabyte.map.excursions.viewmodel.IntroViewModel

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding
    private lateinit var viewModel: IntroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        viewModel = ViewModelProvider(this)[IntroViewModel::class.java]

        viewModel.liveDataHaveIntroImages.observe(this) {
           onIntroImagesGot()
        }
    }

    private fun onIntroImagesGot() {
        fun setCurrentBackground() {
            val currentIntroImagePair = viewModel.getCurrentIntroImage()
            binding.textIntroImageCaption.text = currentIntroImagePair.first
            binding.root.background = currentIntroImagePair.second
        }

        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCurrentBackground()

        val fragment = IntroFragment()
        supportFragmentManager.beginTransaction().add(R.id.frame_fragment_intro, fragment, "").commit()

        binding.buttonContinue.setOnClickListener {
            viewModel.fragmentNumber++
            setCurrentBackground()
            if(viewModel.fragmentNumber==2) {
                binding.buttonContinue.isVisible = false
                binding.buttonSkip.isVisible = false
            }
            else {
                binding.buttonContinue.isVisible = true
                binding.buttonSkip.isVisible = true
            }
            val fragmentToReplace = IntroFragment()
            supportFragmentManager.beginTransaction().replace(R.id.frame_fragment_intro, fragmentToReplace).commit()
        }

        binding.buttonSkip.setOnClickListener {
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
        }
    }
}