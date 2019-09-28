package com.ceaver.bao.backup

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

abstract class BackupFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getViewResource(), container, false)
    }

    override fun onStart() {
        super.onStart()
        bindActions()
        observeStatus()
    }

    override fun onStop() {
        super.onStop()
        unbindActions()
    }

    private fun bindActions() = getBackupButton().setOnClickListener { onStartBackupOperationClick() }
    private fun unbindActions() = getBackupButton().setOnClickListener(null)

    private fun observeStatus() {
        lookupViewModel().status.observe(this, Observer {
            when (it) {
                BackupViewModel.BackupStatus.CHECK_PERMISSIONS -> checkPermissions()
                BackupViewModel.BackupStatus.REQUEST_PERMISSION -> requestPermissions()
                BackupViewModel.BackupStatus.BACKUP_OPERATION_SUCCESSFUL -> onSuccessfulBackupOperation()
                BackupViewModel.BackupStatus.BACKUP_OPERATION_NO_DATA_FOUND -> onNoBackupDataFound()
                BackupViewModel.BackupStatus.BACKUP_OPERATION_EXCEPTION -> onBackupOperationException()
                else -> throw IllegalStateException()
            }
        })
    }

    private fun onStartBackupOperationClick() {
        disableButtons()
        lookupViewModel().onStartBackupOperationClick()
    }

    private fun onSuccessfulBackupOperation() {
        Snackbar.make(getSnackbarView(), "Backup operation successful", Snackbar.LENGTH_LONG).show()
    }

    private fun onNoBackupDataFound() {
        Snackbar.make(getSnackbarView(), "No Backup data found", Snackbar.LENGTH_LONG).show()
        enableButtons()
    }

    private fun onBackupOperationException() {
        Snackbar.make(getSnackbarView(), "Backup operation failed", Snackbar.LENGTH_LONG).show()
        enableButtons()
    }

    private fun checkPermissions() {
        if (hasPermission(getPermission())) {
            lookupViewModel().onPermissionAvailable()
        } else
            lookupViewModel().onPermissionMissing()
    }

    private fun requestPermissions() = requestPermissions(arrayOf(getPermission()), 0)

    private fun hasPermission(permission: String) = ContextCompat.checkSelfPermission(this.requireContext(), permission) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionGranted = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (permissionGranted) lookupViewModel().onPermissionGranted() else enableButtons()
    }

    private fun disableButtons() = enableButtons(false)
    private fun enableButtons() = enableButtons(true)
    private fun enableButtons(enable: Boolean) { getBackupButton().isEnabled = enable}
    abstract fun getViewResource() : Int
    abstract fun getBackupButton(): Button
    abstract fun lookupViewModel(): BackupViewModel
    abstract fun getPermission(): String
    abstract fun getSnackbarView(): View
}
