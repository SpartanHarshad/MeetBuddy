package com.harshad.meetbuddy.utility

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Locale

object Util {

    fun getDurationForMilliseconds(milliseconds: Long): String {
        val hours = (milliseconds / (1000 * 60 * 60)) % 24
        val minutes = (milliseconds / (1000 * 60)) % 60
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    fun getTimeStamp(milliseconds: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss a", Locale.getDefault())
        return simpleDateFormat.format(milliseconds)
    }
}