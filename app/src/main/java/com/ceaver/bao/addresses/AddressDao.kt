package com.ceaver.bao.addresses

import androidx.room.*

@Dao
interface AddressDao {
    @Query("select * from address")
    fun loadAddresses(): List<Address>

    @Query("select * from address where id = :id")
    fun loadAddress(id: Long): Address

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAddress(address: Address) : Long

    @Update
    fun updateAddress(address: Address)

    @Update
    fun updateAddresses(addresses: List<Address>)

    @Delete
    fun deleteAddress(address: Address)
}