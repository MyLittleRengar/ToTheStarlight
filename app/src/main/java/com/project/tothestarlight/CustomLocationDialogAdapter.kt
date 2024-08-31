package com.project.tothestarlight

import android.app.Dialog
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.tothestarlight.databinding.CustomLocationBinding

class CustomLocationDialogAdapter(private val context: AppCompatActivity) {

    private lateinit var listener: CustomDialogAcceptButtonClick
    private lateinit var  binding: CustomLocationBinding
    private val dlg = Dialog(context)

    fun show() {
        binding = CustomLocationBinding.inflate(context.layoutInflater)

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dlg.setContentView(binding.root)
        dlg.setCancelable(false)

        binding.locationeAcceptBTN.setOnClickListener {
            val location = binding.locationSn.selectedItem.toString()
            if(location.isNotBlank()) {
                listener.onAcceptClick(location)
            }
            else {
                Toast.makeText(context, "지역을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
            dlg.dismiss()
        }
        binding.locationCancelBTN.setOnClickListener {
            dlg.dismiss()
        }
        dlg.show()
    }

    fun setOnAcceptClickedListener(listener: (String) -> Unit) {
        this.listener = object: CustomDialogAcceptButtonClick {
            override fun onAcceptClick(location: String) {
                listener(location)
            }

        }
    }

    interface CustomDialogAcceptButtonClick {
        fun onAcceptClick(location: String)
    }
}