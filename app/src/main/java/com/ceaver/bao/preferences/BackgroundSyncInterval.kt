package com.ceaver.bao.preferences

import java.util.*
import java.util.concurrent.TimeUnit

enum class BackgroundSyncInterval(val repeatInterval: Long?, val repeatIntervalTimeUnit: TimeUnit?, val flexTimeInterval: Long?, val flexTimeIntervalUnit: TimeUnit?) {
    NONE(null, null, null, null),
    M15(15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES),
    M30(30, TimeUnit.MINUTES, 10, TimeUnit.MINUTES),
    H1(1, TimeUnit.HOURS, 15, TimeUnit.MINUTES),
    H2(2, TimeUnit.HOURS, 20, TimeUnit.MINUTES),
    H4(4, TimeUnit.HOURS, 30, TimeUnit.MINUTES),
    D1(1, TimeUnit.DAYS, 1, TimeUnit.HOURS),
    D2(2, TimeUnit.DAYS, 2, TimeUnit.HOURS),
    W1(7, TimeUnit.DAYS, 6, TimeUnit.HOURS);

    fun toHumanReadableString() : String {
        return when (this) {
            NONE -> "disabled"
            else -> "every $repeatInterval ${repeatIntervalTimeUnit!!.name.toLowerCase(Locale.ROOT)}"
        }
    }
}