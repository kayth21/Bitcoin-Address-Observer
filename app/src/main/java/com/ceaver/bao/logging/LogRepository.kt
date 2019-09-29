package com.ceaver.bao.logging

import android.os.Handler
import android.os.Looper
import com.ceaver.bao.database.Database
import com.ceaver.bao.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus
import java.util.*

object LogRepository {

    // LOAD LOG //

    fun loadLog(identifier: UUID): Log {
        return getLogDao().loadLog(identifier)
    }

    fun loadAllLogs(): List<Log> {
        return getLogDao().loadAllLogs()
    }

    fun loadAllLogsAsync(callbackInMainThread: Boolean, callback: (List<Log>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val logs = loadAllLogs()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(logs) }
            else
                callback.invoke(logs)
        }
    }

    // INSERT LOG //

    fun insertLog(message: String, category: LogCategory, identifier: UUID = UUID.randomUUID()) {
        getLogDao().insertLog(Log(message = message, category = category, identifier = identifier))
        EventBus.getDefault().post(LogEvents.Insert())
    }

    fun insertLogAsync(message: String, category: LogCategory) {
        BackgroundThreadExecutor.execute { insertLog(message, category) }
    }

    // UPDATE LOG //

    fun updateLog(log: Log) {
        getLogDao().updateLog(log)
        EventBus.getDefault().post(LogEvents.Update())
    }

    // DELETE ALL LOGS //

    fun deleteAllLogs() {
        getLogDao().deleteAllLogs()
        EventBus.getDefault().post(LogEvents.Insert())
    }

    fun deleteAllLogsAsync() {
        BackgroundThreadExecutor.execute { deleteAllLogs() }
    }

    // HELPER //

    private fun getLogDao(): LogDao {
        return getDatabase().logDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }


}