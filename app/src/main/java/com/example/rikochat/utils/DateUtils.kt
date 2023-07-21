package com.example.rikochat.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun Long.toStringDate(): String{
    val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return simpleDateFormat.format(this)
}

fun Long.toHoursMinutes(): String{
    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    return simpleDateFormat.format(this)
}