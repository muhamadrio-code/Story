package com.riopermana.story.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToast(@StringRes resId:Int, duration: Int = Toast.LENGTH_LONG) {
    val string = resources.getString(resId)
    Toast.makeText(this,string,duration).show()
}