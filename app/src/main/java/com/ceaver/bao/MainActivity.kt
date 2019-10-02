package com.ceaver.bao

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.ceaver.bao.addresses.AddressEvents
import com.ceaver.bao.backup.export.ExportFragment
import com.ceaver.bao.backup.import.ImportFragment
import com.ceaver.bao.contribute.ContributeFragment
import com.ceaver.bao.credits.CreditsFragment
import com.ceaver.bao.feedback.FeedbackFragment
import com.ceaver.bao.logging.LogListActivity
import com.ceaver.bao.manual.ManualFragment
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
            R.id.mainMenuSyncAction -> {
                Workers.run()
                true
            }
            R.id.mainMenuPreferencesAction -> {
                startActivity(Intent(this, PreferencesActivity::class.java))
                true
            }
            R.id.mainMenuImportAction -> {
                showFragment(ImportFragment())
                true
            }
            R.id.mainMenuExportAction -> {
                showFragment(ExportFragment())
                true
            }
            R.id.mainmenuFeedbackAction -> {
                showFragment(FeedbackFragment())
                true
            }
            R.id.mainmenuContributeAction -> {
                showFragment(ContributeFragment())
                true
            }
            R.id.mainmenuManualAction -> {
                showFragment(ManualFragment())
                true
            }
            R.id.mainmenuCreditsAction -> {
                showFragment(CreditsFragment())
                true
            }
            R.id.mainMenuLoggingAction -> {
                startActivity(Intent(this, LogListActivity::class.java))
            true
        }
            else -> super.onOptionsItemSelected(item)
        }

    private fun showFragment(dialogFragment: DialogFragment) {
        dialogFragment.show(supportFragmentManager, dialogFragment.javaClass.canonicalName)
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

    private fun enableSyncAction(enable: Boolean) {
        val syncAction = mainActivityToolbar.menu.findItem(R.id.mainMenuSyncAction)
        syncAction.icon.mutate().alpha = if (enable) 255 else 130
        syncAction.isEnabled = enable
    }
}
