package com.ceaver.bao.addresses.list

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.bao.R
import com.ceaver.bao.addresses.Address
import com.ceaver.bao.addresses.AddressEvents
import com.ceaver.bao.addresses.AddressRepository
import com.ceaver.bao.addresses.input.AddressInputFragment
import kotlinx.android.synthetic.main.address_list_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AddressListFragment : Fragment() {

    private val addressListAdapter = AddressListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.address_list_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        addressListFragmentAddressList.adapter = addressListAdapter
        addressListFragmentAddressList.addItemDecoration(DividerItemDecoration(activity!!.application, LinearLayoutManager.VERTICAL)) // TODO seriously?
        addressListFragmentCreateAddressButton.setOnClickListener { AddressInputFragment().show(fragmentManager!!, AddressInputFragment.FRAGMENT_TAG) }
        loadAllAddresses()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        addressListFragmentAddressList.adapter = null
        addressListFragmentAddressList.removeItemDecorationAt(0) // TODO seriously?
        addressListFragmentCreateAddressButton.setOnClickListener(null)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AddressEvents.Delete) {
        loadAllAddresses()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AddressEvents.Insert) {
        loadAllAddresses()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AddressEvents.Update) {
        loadAllAddresses()
    }

    private fun loadAllAddresses() {
        AddressRepository.loadAllAddressesAsync(true) { onAllAddressesLoaded(it) }
    }

    private fun onAllAddressesLoaded(addresss: List<Address>) {
        addressListAdapter.addressList = addresss
        addressListAdapter.notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: Address)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Address) {
            showDialogFragment(AddressInputFragment(), AddressInputFragment.FRAGMENT_TAG, AddressInputFragment.ADDRESS_ID, item.id)
        }
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.groupId == AddressListAdapter.CONTEXT_MENU_GROUP_ID) {
            val selectedAddress = addressListAdapter.currentLongClickAddress!!
            when (menuItem.itemId) {
                AddressListAdapter.CONTEXT_MENU_EDIT_ITEM_ID -> showDialogFragment(AddressInputFragment(), AddressInputFragment.FRAGMENT_TAG, AddressInputFragment.ADDRESS_ID, selectedAddress.id)
                AddressListAdapter.CONTEXT_MENU_DELETE_ITEM_ID -> AddressRepository.deleteAddressAsync(selectedAddress)
                AddressListAdapter.CONTEXT_MENU_RESET_ITEM_ID -> AddressRepository.updateAddressAsync(selectedAddress.copyForReset())
                AddressListAdapter.CONTEXT_MENU_SHOW_ITEM_ID -> startActivity( Intent(Intent.ACTION_VIEW, Uri.parse("https://oxt.me/address/${selectedAddress.value}")));
                else -> throw IllegalStateException()
            }
        }
        return super.onContextItemSelected(menuItem)
    }

    private fun showDialogFragment(dialogFragment: DialogFragment, fragmentTag: String, addressIdKey: String, addressIdValue: Long) {
        val arguments = Bundle()
        arguments.putLong(addressIdKey, addressIdValue)
        dialogFragment.arguments = arguments
        dialogFragment.show(fragmentManager!!, fragmentTag)
    }
}
