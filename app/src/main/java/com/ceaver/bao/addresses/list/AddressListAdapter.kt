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
import kotlin.random.Random

internal class AddressListAdapter(private val onClickListener: AddressListFragment.OnItemClickListener) : RecyclerView.Adapter<AddressListAdapter.AddressViewHolder>() {

    companion object {
        val CONTEXT_MENU_GROUP_ID = Random.nextInt()
        val CONTEXT_MENU_EDIT_ITEM_ID = Random.nextInt()
        val CONTEXT_MENU_DELETE_ITEM_ID = Random.nextInt()
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

    inner class AddressViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_EDIT_ITEM_ID, 0, "Edit")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_DELETE_ITEM_ID, 1, "Delete")
        }

        fun bindItem(address: Address, onClickListener: AddressListFragment.OnItemClickListener) {
            (view.findViewById(R.id.addressListRowStatusImage) as ImageView).setImageResource(R.drawable.address_status_unknown)
            (view.findViewById(R.id.addressListRowMappingField) as TextView).text = address.mapping
            (view.findViewById(R.id.addressListRowAddressField) as TextView).text = address.value

            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(address) }
        }
    }
}