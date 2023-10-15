package com.terabyte.map.excursions.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.terabyte.map.excursions.databinding.BottomSheetMapPermissionsInDialogBinding
import com.terabyte.map.excursions.databinding.BottomSheetMapPermissionsInSettingsBinding

class MapPermissionsBottomSheetDialog(
    private val mode: Mode,
    private val onGotItListener: () -> Unit
):
BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return when(mode) {
            Mode.InDialog -> {
                val binding = BottomSheetMapPermissionsInDialogBinding.inflate(inflater, container, false)
                binding.buttonGotIt.setOnClickListener {
                    dismiss()
                    onGotItListener()
                }
                return binding.root
            }
            Mode.InSettings -> {
                val binding = BottomSheetMapPermissionsInSettingsBinding.inflate(inflater, container, false)
                binding.buttonGotIt.setOnClickListener {
                    dismiss()
                    onGotItListener()
                }
                return binding.root
            }
        }

    }

    enum class Mode {
        InDialog, InSettings
    }

    companion object {
        const val FRAGMENT_TAG = "MapPermissionsBottomSheetDialog"

        fun newInstance(mode: Mode, onGotItListener: () -> Unit): MapPermissionsBottomSheetDialog {
            val dialog = MapPermissionsBottomSheetDialog(mode, onGotItListener)
            dialog.isCancelable = false
            return dialog
        }
    }
}