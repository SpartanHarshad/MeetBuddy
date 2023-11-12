package com.harshad.meetbuddy.utility

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Locale

object Util {

    fun getDurationForMilliseconds(milliseconds: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        val amPm = if (calendar.get(HOUR_OF_DAY) < 12) "AM" else "PM"
        val hours = calendar.get(Calendar.HOUR) % 12
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)
        return String.format("%02d:%02d:%02d %s", hours, minutes, seconds, amPm)
    }


    fun getTimeStamp(milliseconds: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return simpleDateFormat.format(milliseconds)
    }
}