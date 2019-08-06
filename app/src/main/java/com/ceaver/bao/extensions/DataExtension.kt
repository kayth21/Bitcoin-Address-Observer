package com.ceaver.bao.extensions

import androidx.work.Data

fun Data.getLong(key: String): Long? {
    val value = getLong(key, Long.MIN_VALUE)
    return if (value == Long.MIN_VALUE) null else value
}