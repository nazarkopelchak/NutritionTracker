package com.nazarkopelchak.nutritiontracker.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Double.toOneDecimal(): Double {
    return BigDecimal(this).setScale(1, RoundingMode.HALF_EVEN).toDouble()
}

fun String.capitalized(): String {
    return this.lowercase()
        .replaceFirstChar { it.uppercase() }
}

fun timeDifference(time: String): LocalTime {
    val localTime = LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")))
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val t = SimpleDateFormat("HH:mm", Locale.getDefault()).format(timeFormat.parse(time) ?: timeFormat.parse("0:0"))
    val formattedTime = LocalTime.parse(t)

    var hours = 0
    var minutes = 0
    var localHours = localTime.hour
    var localMinutes = localTime.minute

    while (true) {
        if (localHours == 24) { localHours = 0 }
        if (localHours == formattedTime.hour) { break }
        localHours++
        hours++
    }
    while (true) {
        if (localMinutes == formattedTime.minute) { break }
        localMinutes++
        minutes++
        if (localMinutes == 60) {
            localMinutes = 0
            hours--
        }
    }

    return if (hours == -1) {
        LocalTime.parse("23:%02d".format(minutes))
    } else {
        LocalTime.parse("%02d:%02d".format(hours, minutes))
    }
}