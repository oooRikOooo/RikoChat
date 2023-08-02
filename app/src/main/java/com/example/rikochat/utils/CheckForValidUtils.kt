package com.example.rikochat.utils

import android.text.TextUtils
import android.util.Patterns


fun isValidEmail(text: CharSequence): Boolean {
    return !TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches()
}