package com.ceaver.bao.logging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.bao.R
import com.ceaver.bao.extensions.asFormattedDateTime

class LogListAdapter : RecyclerView.Adapter<LogListAdapter.ViewHolder>() {
    var logList: List<Log> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(logList[position])
    }

    override fun getItemCount() = logList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bindItem(log: Log) {
            (view.findViewById(R.id.logListRowTimestampValue) as TextView).text = log.timestamp.asFormattedDateTime()
            (view.findViewById(R.id.logListRowIdValue) as TextView).text = "#${log.id}"
            (view.findViewById(R.id.logListRowMessageValue) as TextView).text = log.message
        }
    }
}