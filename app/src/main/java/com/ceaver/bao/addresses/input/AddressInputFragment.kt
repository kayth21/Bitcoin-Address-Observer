package com.ceaver.bao.addresses.input

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ceaver.bao.extensions.afterTextChanged
import com.ceaver.bao.extensions.registerInputValidator
import com.ceaver.bao.logging.LogCategory
import com.ceaver.bao.logging.LogRepository
import com.ceaver.bao.preferences.Preferences
import com.ceaver.bao.threading.BackgroundThreadExecutor
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.address_input_fragment.*


class AddressInputFragment : DialogFragment() {

    companion object {
        const val FRAGMENT_TAG = "com.ceaver.bao.addresses.input.AddressInputFragment.FragmentTag"
        const val ADDRESS_ID = "com.ceaver.bao.addresses.input.AddressInputFragment.addressId"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.bao.R.layout.address_input_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

        val addressId = lookupAddressId()
        val viewModel = lookupViewModel().apply { init(addressId) }

        modifyView(addressId)
        bindActions(viewModel)
        observeStatus(viewModel)
        observeDataReady(viewModel)
    }

    private fun modifyView(addressId: Long?) {
        if (addressId != null) {
            addressInputFragmentAddressField.setInputType(InputType.TYPE_NULL);
            addressInputFragmentAddressField.setTextIsSelectable(true);
            addressInputFragmentAddressField.setKeyListener(null);
        }
    }

    private fun lookupAddressId(): Long? = arguments?.getLong(ADDRESS_ID).takeUnless { it == 0L }
    private fun lookupViewModel(): AddressInputViewModel = ViewModelProviders.of(this).get(AddressInputViewModel::class.java)
    private fun bindActions(viewModel: AddressInputViewModel) {
        addressInputFragmentSaveButton.setOnClickListener { onSaveClick(viewModel) }
        addressInputFragmentQrButton.setOnClickListener { onQrClick() }
    }

    private fun onQrClick() {
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    private fun onSaveClick(viewModel: AddressInputViewModel) {
        val address = addressInputFragmentAddressField.text.toString()
        val mapping = addressInputFragmentAddressMappingValue.text.toString()
        viewModel.onSaveClick(address, mapping)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data) ?: return

        if (result.contents == null) {
            Toast.makeText(this.context, "Scanning cancelled", Toast.LENGTH_LONG).show()
            return
        }

        val btcAddress = result.contents.substringAfter(":").substringBefore("?")
        BackgroundThreadExecutor.execute { Handler(Looper.getMainLooper()).post { addressInputFragmentAddressField.setText(btcAddress) } }
        Toast.makeText(this.context, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
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
        if (Preferences.isLoggingEnabled()) {
            val address = addressInputFragmentAddressField.text.toString()
            if (lookupAddressId() == null) {
                LogRepository.insertLogAsync("Added address ${address.take(20)}...", LogCategory.INSERT)
            } else {
                LogRepository.insertLogAsync("Modified address ${address.take(20)}...", LogCategory.MODIFY)
            }
        }
        dismiss()
    }

    private fun registerInputValidation() {
        addressInputFragmentAddressField.registerInputValidator({ "^(bc1|[13])[a-zA-HJ-NP-Z0-9]{25,39}\$".toRegex().matches(it) },getString(com.ceaver.bao.R.string.invalid_bitcoin_address)) // TODO better input validation incl. checksum check
        addressInputFragmentAddressField.afterTextChanged { addressInputFragmentSaveButton.isEnabled = checkSaveButton() }
        addressInputFragmentAddressMappingValue.registerInputValidator({ it.length < 100 }, getString(com.ceaver.bao.R.string.invalid_bitcoin_address_mapping)) // TODO improve validation
        addressInputFragmentAddressMappingValue.afterTextChanged { addressInputFragmentSaveButton.isEnabled = checkSaveButton() }
    }

    private fun enableInput(enable: Boolean) {
        addressInputFragmentSaveButton.isEnabled = enable && checkSaveButton()
        addressInputFragmentAddressField.isEnabled = enable
        addressInputFragmentAddressMappingValue.isEnabled = enable
        addressInputFragmentQrButton.isEnabled = enable
    }

    private fun checkSaveButton(): Boolean {
        return addressInputFragmentAddressField.error == null && addressInputFragmentAddressMappingValue.error == null &&
                (addressInputFragmentAddressField.text.toString() != lookupViewModel().address.value!!.value || addressInputFragmentAddressMappingValue.text.toString() != lookupViewModel().address.value!!.mapping)
    }
}
