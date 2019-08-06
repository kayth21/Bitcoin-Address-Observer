package com.ceaver.bao.addresses.list

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.bao.R
import com.ceaver.bao.addresses.Address
import com.ceaver.bao.extensions.asFormattedNumber
import com.ceaver.bao.extensions.setLocked
import com.ceaver.bao.network.SyncStatus
import kotlin.random.Random

internal class AddressListAdapter(private val onClickListener: AddressListFragment.OnItemClickListener) : RecyclerView.Adapter<AddressListAdapter.AddressViewHolder>() {

    companion object {
        val CONTEXT_MENU_GROUP_ID = Random.nextInt()
        val CONTEXT_MENU_EDIT_ITEM_ID = Random.nextInt()
        val CONTEXT_MENU_DELETE_ITEM_ID = Random.nextInt()
        val CONTEXT_MENU_RESET_ITEM_ID = Random.nextInt()
        val CONTEXT_MENU_SHOW_ITEM_ID = Random.nextInt()
    }

    var addressList: List<Address> = ArrayList()
    var currentLongClickAddress: Address? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.address_list_row, parent, false))
    }

    override fun getItemCount(): Int = addressList.size

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bindItem(addressList[position], onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickAddress = addressList[position]; false }
    }

    inner class AddressViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_EDIT_ITEM_ID, 0, "Edit")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_DELETE_ITEM_ID, 1, "Delete")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_RESET_ITEM_ID, 2, "Reset")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_SHOW_ITEM_ID, 3, "Show")
        }

        fun bindItem(address: Address, onClickListener: AddressListFragment.OnItemClickListener) {
            (view.findViewById(R.id.addressListRowStatusImage) as ImageView).setImageResource(address.addressStatus().image)
            (view.findViewById(R.id.addressListRowStatusImage) as ImageView).setLocked(address.lastSyncStatus == SyncStatus.LOADING || address.lastSyncStatus == SyncStatus.ERROR)
            (view.findViewById(R.id.addressListRowMappingField) as TextView).text = address.mapping
            (view.findViewById(R.id.addressListRowAddressField) as TextView).text = shortenAddress(address.value)
            (view.findViewById(R.id.addressListRowTxoCountField) as TextView).text = address.currentTransactionCount?.asFormattedNumber()?.plus(" tx") ?: ""
            (view.findViewById(R.id.addressListRowBalanceField) as TextView).text = address.balance?.asFormattedNumber()?.plus(" sats") ?: ""


            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(address) }
        }

        private fun shortenAddress(address: String): String {
            return if (address.length > 25) address.take(25) + "..." else address
        }
    }
}