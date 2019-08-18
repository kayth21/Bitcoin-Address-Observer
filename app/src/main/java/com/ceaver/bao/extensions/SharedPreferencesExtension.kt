package com.ceaver.bao.extensions

import android.content.SharedPreferences

fun SharedPreferences.Editor.putInt(key: String, value: Int?): SharedPreferences.Editor {
    return putInt(key, value ?: Int.MIN_VALUE)
}

fun SharedPreferences.Editor.putLong(key: String, value: Long?): SharedPreferences.Editor {
    return putLong(key, value ?: Long.MIN_VALUE)
}

fun SharedPreferences.getInt(key: String): Int? {
    if (!contains(key))
        return null

    val value = getInt(key, Int.MIN_VALUE)
    return if (value == Int.MIN_VALUE) null else value
}

fun SharedPreferences.getLong(key: String): Long? {
    if (!contains(key))
        return null

    val value = getLong(key, Long.MIN_VALUE)
    return if (value == Long.MIN_VALUE) null else value
}

fun SharedPreferences.getString(key: String): String? {
    return if (contains(key)) getString(key, "") else null
}

fun SharedPreferences.getBoolean(key: String): Boolean? {
    return if (contains(key)) getBoolean(key, false) else null
}
