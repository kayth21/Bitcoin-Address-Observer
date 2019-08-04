package com.ceaver.bao.addresses.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ceaver.bao.R
import com.ceaver.bao.extensions.afterTextChanged
import com.ceaver.bao.extensions.registerInputValidator
import kotlinx.android.synthetic.main.address_input_fragment.*

class AddressInputFragment : DialogFragment() {

    companion object {
        const val FRAGMENT_TAG = "com.ceaver.bao.addresses.input.AddressInputFragment.FragmentTag"
        const val ADDRESS_ID = "com.ceaver.bao.addresses.input.AddressInputFragment.addressId"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.address_input_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        val addressId = lookupAddressId()
        val viewModel = lookupViewModel().apply { init(addressId) }

        bindActions(viewModel)
        observeStatus(viewModel)
        observeDataReady(viewModel)
    }

    private fun lookupAddressId(): Long? = arguments?.getLong(ADDRESS_ID).takeUnless { it == 0L }
    private fun lookupViewModel(): AddressInputViewModel = ViewModelProviders.of(this).get(AddressInputViewModel::class.java)
    private fun bindActions(viewModel: AddressInputViewModel) = addressInputFragmentSaveButton.setOnClickListener { onSaveClick(viewModel) }

    private fun onSaveClick(viewModel: AddressInputViewModel) {
        val address = addressInputFragmentAddressField.text.toString()
        val mapping = addressInputFragmentAddressMappingValue.text.toString()
        viewModel.onSaveClick(address, mapping)
    }

    private fun observeStatus(viewModel: AddressInputViewModel) {
        viewModel.status.observe(this, Observer {
            when (it) {
                AddressInputViewModel.AddressInputStatus.START_SAVE -> onStartSave()
                AddressInputViewModel.AddressInputStatus.END_SAVE -> onEndSave()
                null -> throw IllegalStateException()
            }
        })
    }

    private fun observeDataReady(viewModel: AddressInputViewModel) {
        viewModel.address.observe(this, Observer {
            addressInputFragmentAddressField.setText(it!!.value)
            addressInputFragmentAddressMappingValue.setText(it.mapping)

            registerInputValidation()
            enableInput(true)
            viewModel.address.removeObservers(this)
        })
    }


    private fun onStartSave() {
        enableInput(false)
    }

    private fun onEndSave() {
        dismiss()
    }

    private fun registerInputValidation() {
        addressInputFragmentAddressField.registerInputValidator({ "^(bc1|[13])[a-zA-HJ-NP-Z0-9]{25,39}\$".toRegex().matches(it) }, getString(R.string.invalid_bitcoin_address)) // TODO better input validation incl. checksum check
        addressInputFragmentAddressField.afterTextChanged { addressInputFragmentSaveButton.isEnabled = checkSaveButton() }
        addressInputFragmentAddressMappingValue.registerInputValidator({ it.length < 100 }, getString(R.string.invalid_bitcoin_address_mapping)) // TODO improve validation
        addressInputFragmentAddressMappingValue.afterTextChanged { addressInputFragmentSaveButton.isEnabled = checkSaveButton() }
    }

    private fun enableInput(enable: Boolean) {
        addressInputFragmentSaveButton.isEnabled = enable && checkSaveButton()
        addressInputFragmentAddressField.isEnabled = enable
        addressInputFragmentAddressMappingValue.isEnabled = enable
    }

    private fun checkSaveButton(): Boolean {
        return addressInputFragmentAddressField.error == null && addressInputFragmentAddressMappingValue.error == null
    }
}
