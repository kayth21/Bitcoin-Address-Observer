package com.ceaver.bao.addresses

import com.ceaver.bao.R


enum class AddressStatus(val image: Int) {
    UNCHANGED(R.drawable.address_status_unchanged),
    CHANGED(R.drawable.address_status_changed),
    EXCEPTION(R.drawable.address_status_exception),
    UNKNOWN(R.drawable.address_status_unknown)
}