package com.ceaver.bao

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.ceaver.bao.addresses.AddressEvents
import com.ceaver.bao.preferences.PreferencesActivity
import com.ceaver.bao.worker.WorkerEvents
import com.ceaver.bao.worker.Workers
import kotlinx.android.synthetic.main.main_activity.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(findViewById(R.id.mainActivityToolbar))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.mainmenuSyncAction -> {
                Workers.run()
                true
            }
            R.id.mainmenuPreferencesAction -> {
                startActivity(Intent(this, PreferencesActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: WorkerEvents.Start) {
        enableSyncAction(false)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: WorkerEvents.End) {
        enableSyncAction(true)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AddressEvents.Insert) {
        if (!event.suppressReload)
            Workers.run(event.addressId)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AddressEvents.Update) {
        if (!event.suppressReload)
            Workers.run(event.addressId)
    }

    private fun enableSyncAction(enable: Boolean) {
        val syncAction = mainActivityToolbar.menu.findItem(R.id.mainmenuSyncAction)
        syncAction.icon.mutate().alpha = if (enable) 255 else 130
        syncAction.isEnabled = enable
    }
}
