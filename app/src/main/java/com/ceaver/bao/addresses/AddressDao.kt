package com.ceaver.bao.addresses

import androidx.room.*

@Dao
interface AddressDao {
    @Query("select * from address")
    fun loadAllAddresses(): List<Address>

    @Query("select * from address where id = :id")
    fun loadAddress(id: Long): Address

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAddress(address: Address) : Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAddresses(addresses: List<Address>): List<Long>

    @Update
    fun updateAddress(address: Address)

    @Update
    fun updateAddresses(addresses: List<Address>)

    @Delete
    fun deleteAddress(address: Address)

    @Query("delete from address")
    fun deleteAllAddresses()
}