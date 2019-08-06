package com.ceaver.bao.addresses

import android.os.Handler
import android.os.Looper
import com.ceaver.bao.database.Database
import com.ceaver.bao.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus

object AddressRepository {

    // LOAD ADDRESS BY ID //

    fun loadAddress(id: Long): Address {
        return getAddressDao().loadAddress(id)
    }

    fun loadAddressAsync(id: Long, callbackInMainThread: Boolean = false, callback: (Address) -> Unit) {
        BackgroundThreadExecutor.execute { handleCallback(callbackInMainThread, callback, loadAddress(id)) }
    }

    // LOAD ALL ADDRESSES //

    fun loadAddresses(): List<Address> {
        return getAddressDao().loadAddresses()
    }

    fun loadAddressesAsync(callbackInMainThread: Boolean = false, callback: (List<Address>) -> Unit) {
        BackgroundThreadExecutor.execute { handleCallback(callbackInMainThread, callback, loadAddresses()) }
    }

    // SAVE ADDRESS //

    fun saveAddress(address: Address): Long {
        return if (address.id > 0)
            updateAddress(address)
        else
            insertAddress(address)
    }

    fun saveAddressAsync(address: Address, callbackInMainThread: Boolean = false, callback: ((Long) -> Unit)? = null) {
        if (address.id > 0)
            updateAddressAsync(address, callbackInMainThread, callback)
        else
            insertAddressAsync(address, callbackInMainThread, callback)
    }

    // INSERT ADDRESS //

    fun insertAddress(address: Address): Long {
        val addressId = getAddressDao().insertAddress(address)
        getEventbus().post(AddressEvents.Insert(addressId))
        return addressId
    }

    fun insertAddressAsync(address: Address, callbackInMainThread: Boolean = false, callback: ((Long) -> Unit)? = null) {
        BackgroundThreadExecutor.execute {
            val addressId = insertAddress(address)
            if (callback != null)
                handleCallback(callbackInMainThread, callback, addressId)
        }
    }
    // UPDATE ADDRESS //

    fun updateAddress(address: Address, suppressReload: Boolean = false): Long {
        getAddressDao().updateAddress(address)
        getEventbus().post(AddressEvents.Update(address.id, suppressReload))
        return address.id
    }

    fun updateAddressAsync(address: Address, callbackInMainThread: Boolean = false, callback: ((Long) -> Unit)? = null) {
        BackgroundThreadExecutor.execute {
            val addressId = updateAddress(address)
            if (callback != null)
                handleCallback(callbackInMainThread, callback, addressId)
        }
    }

    // UPDATE ADDRESSES //

    fun updateAddresses(addresses: List<Address>, suppressReload: Boolean = false): List<Long> {
        getAddressDao().updateAddresses(addresses)
        val ids = addresses.map { it.id }
        getEventbus().post(AddressEvents.Update(suppressReload = suppressReload))
        return ids
    }


    fun updateAddressesAsync(addresses: List<Address>, callbackInMainThread: Boolean = false, callback: ((List<Long>) -> Unit)? = null) {
        BackgroundThreadExecutor.execute {
            val addressIds = updateAddresses(addresses)
            if (callback != null)
                handleCallback(callbackInMainThread, callback, addressIds)
        }
    }

    // DELETE ADDRESS //

    fun deleteAddress(address: Address): Long {
        getAddressDao().deleteAddress(address)
        getEventbus().post(AddressEvents.Delete(address.id))
        return address.id
    }

    fun deleteAddressAsync(address: Address, callbackInMainThread: Boolean = false, callback: ((Long) -> Unit)? = null) {
        BackgroundThreadExecutor.execute {
            val addressId = deleteAddress(address)
            if (callback != null)
                handleCallback(callbackInMainThread, callback, addressId)
        }
    }

    // HELPER //

    private fun <T> handleCallback(callbackInMainThread: Boolean = false, callback: (T) -> Unit, addressId: T) {
        if (callbackInMainThread)
            Handler(Looper.getMainLooper()).post { callback.invoke(addressId) }
        else
            callback.invoke(addressId)
    }

    private fun getAddressDao(): AddressDao = getDatabase().addressDao()

    private fun getDatabase(): Database = Database.getInstance()

    private fun getEventbus() = EventBus.getDefault()

}