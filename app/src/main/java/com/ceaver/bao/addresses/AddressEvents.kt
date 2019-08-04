package com.ceaver.bao.addresses

class AddressEvents {
    data class Update(val ids: List<Long>)
    data class Insert(val ids: List<Long>)
    data class Delete(val ids: List<Long>)
}