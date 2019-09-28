package com.ceaver.bao.backup.export

import android.Manifest
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProviders
import com.ceaver.bao.R
import com.ceaver.bao.backup.BackupFragment
import kotlinx.android.synthetic.main.fragment_export.*

class ExportFragment : BackupFragment() {

    override fun getViewResource(): Int = R.layout.fragment_export
    override fun lookupViewModel(): ExportViewModel = ViewModelProviders.of(this).get(ExportViewModel::class.java)
    override fun getSnackbarView(): View = exportFragmentConstraintLayout
    override fun getPermission(): String = Manifest.permission.WRITE_EXTERNAL_STORAGE
    override fun getBackupButton(): Button = exportFragmentExportButton
}
