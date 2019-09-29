package com.ceaver.bao.extensions

fun Boolean.getEnabledOrDisabledText(): String {
    return if (this) "enabled" else "disabled"
}