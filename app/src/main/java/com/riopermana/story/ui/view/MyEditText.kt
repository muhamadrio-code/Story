package com.riopermana.story.ui.view

import android.content.Context
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import com.riopermana.story.R

class MyEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var textInputLayout: TextInputLayout? = null

    init {
        setSingleLine()
        if (isInputTypeTextPassword()) {
            transformationMethod = PasswordTransformationMethod()
        }
        val layoutChangeListener = object : OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                if (parent != null) {
                    removeOnLayoutChangeListener(this)
                    textInputLayout = parent.parent as? TextInputLayout
                }
            }
        }
        addOnLayoutChangeListener(layoutChangeListener)
    }

    private fun isInputTypeTextPassword() : Boolean = inputType == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD or EditorInfo.TYPE_CLASS_TEXT
    private fun isInputTypeTextEmail() : Boolean = inputType == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or EditorInfo.TYPE_CLASS_TEXT

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        text ?: return

        if (isInputTypeTextPassword()) {
            validateTextPassword(text)
        }

        if (isInputTypeTextEmail()) {
            validateEmailAddress(text)
        }

    }

    private fun validateEmailAddress(text: CharSequence) {
        textInputLayout?.let {
            if (isValidEmail(text)) {
                it.isErrorEnabled = false
            } else {
                it.error = resources.getString(R.string.invalid_email_message)
            }
        }
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    private fun validateTextPassword(text: CharSequence) {
        textInputLayout?.let {
            if (text.length < 8) {
                it.error = resources.getString(R.string.invalid_password)
            } else {
                it.isErrorEnabled = false
            }
        }
    }


}