package com.ceaver.bao.extensions

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun LocalDateTime.asFormattedDateTime(): String {
    return format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault()))
}