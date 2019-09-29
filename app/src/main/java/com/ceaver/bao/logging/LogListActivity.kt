package com.ceaver.bao.logging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.bao.R
import kotlinx.android.synthetic.main.log_list_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LogListActivity : AppCompatActivity() {

    private val logListAdapter = LogListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_list_activity)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        logListActivityList.adapter = logListAdapter
        logListActivityList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        loadAllLogs()
        logListActivitySwipeRefreshLayout.setOnRefreshListener { loadAllLogs() }
    }

    private fun loadAllLogs() {
        LogRepository.loadAllLogsAsync(true) { onAllLogsLoaded(it.sortedByDescending { log -> log.id }) }
    }

    private fun onAllLogsLoaded(logs: List<Log>) {
        logListAdapter.logList = logs; logListAdapter.notifyDataSetChanged(); logListActivitySwipeRefreshLayout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        logListActivityList.adapter = null
        logListActivitySwipeRefreshLayout.setOnRefreshListener(null)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogEvents.Insert) {
        loadAllLogs()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: LogEvents.Update) {
        loadAllLogs()
    }
}