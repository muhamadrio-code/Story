package com.riopermana.story.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.recyclerview.widget.ListUpdateCallback

fun Context.showToast(@StringRes resId:Int, duration: Int = Toast.LENGTH_LONG) {
    val string = resources.getString(resId)
    Toast.makeText(this,string,duration).show()
}

fun Context.showToast(text:String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this,text,duration).show()
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}