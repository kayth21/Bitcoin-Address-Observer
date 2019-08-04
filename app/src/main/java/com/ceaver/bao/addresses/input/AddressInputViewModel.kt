package com.ceaver.bao.addresses.input

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ceaver.bao.addresses.Address
import com.ceaver.bao.addresses.AddressRepository
import com.ceaver.bao.common.SingleLiveEvent
import com.ceaver.bao.threading.BackgroundThreadExecutor

class AddressInputViewModel : ViewModel() {

    val address = MutableLiveData<Address>()
    val status = SingleLiveEvent<AddressInputStatus>()

    fun init(addressId: Long?): AddressInputViewModel {
        if (addressId == null)
            BackgroundThreadExecutor.execute { address.postValue(Address(0, "")) }
        else
            AddressRepository.loadAddressAsync(addressId) { address.postValue(it) }
        return this
    }

    fun onSaveClick(value: String, mapping: String?) {
        status.postValue(AddressInputStatus.START_SAVE)
        AddressRepository.saveAddressAsync(address.value!!.copy(value = value, mapping = mapping)) {
            status.postValue(AddressInputStatus.END_SAVE)
        }
    }

    enum class AddressInputStatus {
        START_SAVE,
        END_SAVE
    }
}