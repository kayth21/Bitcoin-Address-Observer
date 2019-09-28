package com.ceaver.bao.backup

import androidx.lifecycle.ViewModel
import com.ceaver.bao.common.SingleLiveEvent
import com.ceaver.bao.threading.BackgroundThreadExecutor

abstract class BackupViewModel : ViewModel() {

    val status = SingleLiveEvent<BackupStatus>()

    fun onStartBackupOperationClick() {
        status.postValue(BackupStatus.CHECK_PERMISSIONS)
    }

    fun onPermissionAvailable() {
        doBackupOperation()
    }

    fun onPermissionGranted() {
        doBackupOperation()
    }

    fun onPermissionMissing() {
        status.postValue(BackupStatus.REQUEST_PERMISSION)
    }

    private fun doBackupOperation() {
        BackgroundThreadExecutor.execute {
            when (startBackupOperation()) {
                BackupManager.Result.SUCCESS -> status.postValue(BackupStatus.BACKUP_OPERATION_SUCCESSFUL)
                BackupManager.Result.NO_DATA_FOUND -> status.postValue(BackupStatus.BACKUP_OPERATION_NO_DATA_FOUND)
                BackupManager.Result.EXCEPTION -> status.postValue(BackupStatus.BACKUP_OPERATION_EXCEPTION)
            }
        }
    }

    abstract fun startBackupOperation(): BackupManager.Result

    enum class BackupStatus {
        CHECK_PERMISSIONS,
        REQUEST_PERMISSION,
        BACKUP_OPERATION_SUCCESSFUL,
        BACKUP_OPERATION_NO_DATA_FOUND,
        BACKUP_OPERATION_EXCEPTION
    }
}