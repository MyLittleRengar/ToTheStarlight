package com.project.tothestarlight.utility

import com.github.usingsky.calendar.KoreanLunarCalendar

fun convertSolarToLunar(year: Int, month: Int, day: Int): String {
    val calendar = KoreanLunarCalendar.getInstance()
    calendar.setSolarDate(year,month,day)
    return calendar.lunarIsoFormat
}