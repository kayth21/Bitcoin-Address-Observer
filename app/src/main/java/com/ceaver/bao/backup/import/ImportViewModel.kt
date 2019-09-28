package com.ceaver.bao.backup.import

import com.ceaver.bao.backup.BackupManager
import com.ceaver.bao.backup.BackupViewModel

class ImportViewModel : BackupViewModel() {

    override fun startBackupOperation() : BackupManager.Result {
        return BackupManager.import()
    }
}