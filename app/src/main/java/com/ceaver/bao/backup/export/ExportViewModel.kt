package com.ceaver.bao.backup.export

import com.ceaver.bao.backup.BackupManager
import com.ceaver.bao.backup.BackupViewModel

class ExportViewModel : BackupViewModel() {

    override fun startBackupOperation() : BackupManager.Result {
        return BackupManager.export()
    }
}