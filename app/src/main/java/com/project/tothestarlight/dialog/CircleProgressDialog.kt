package com.project.tothestarlight.dialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.project.tothestarlight.R


class CircleProgressDialog(context: Context): Dialog(context) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setContentView(R.layout.fragment_circle_progress)
    }
}
