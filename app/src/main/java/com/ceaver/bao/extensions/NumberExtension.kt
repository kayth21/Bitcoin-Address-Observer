package com.ceaver.bao.extensions

import java.text.DecimalFormat

fun Number.asFormattedNumber(): String {
    val formatter = DecimalFormat("#,###,###")
    return formatter.format(this)
}