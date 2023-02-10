package com.riopermana.story.ui.dialogs

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import com.riopermana.story.databinding.DialogLoadingBinding

class LoadingDialog(context: Context) : AlertDialog(context) {
    init {
        window?.setBackgroundDrawable(ColorDrawable(0))
        val binding = DialogLoadingBinding.inflate(layoutInflater)
        setView(binding.root)
        setCancelable(false)
        create()
    }
}