package com.ceaver.bao.backup.import

import android.Manifest
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProviders
import com.ceaver.bao.R
import com.ceaver.bao.backup.BackupFragment
import kotlinx.android.synthetic.main.fragment_import.*

class ImportFragment : BackupFragment() {

    override fun getViewResource(): Int = R.layout.fragment_import
    override fun lookupViewModel(): ImportViewModel = ViewModelProviders.of(this).get(ImportViewModel::class.java)
    override fun getBackupButton(): Button = importFragmentImportButton
    override fun getSnackbarView(): View = importFragmentConstraintLayout
    override fun getPermission(): String = Manifest.permission.READ_EXTERNAL_STORAGE
}