package com.ceaver.bao.addresses

class AddressEvents {
    data class Update(val addressId: Long? = null, val suppressReload: Boolean = false)
    data class Insert(val addressId: Long? = null, val suppressReload: Boolean = false)
    data class Delete(val addressId: Long? = null, val suppressReload: Boolean = false)
}