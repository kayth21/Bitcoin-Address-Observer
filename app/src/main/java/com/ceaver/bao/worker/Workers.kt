package com.ceaver.bao.worker

import android.content.Context
import androidx.work.*
import com.ceaver.bao.R
import com.ceaver.bao.addresses.AddressRepository
import com.ceaver.bao.blockstream.BlockstreamRepository
import com.ceaver.bao.extensions.getLong
import com.ceaver.bao.logging.LogCategory
import com.ceaver.bao.logging.LogRepository
import com.ceaver.bao.notification.Notification
import com.ceaver.bao.preferences.Preferences
import com.ceaver.bao.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

private const val ADDRESS_ID = "com.ceaver.bao.worker.Workers.addressId"
private const val UNIQUE_WORK_ID = "com.ceaver.bao.worker.Workers.uniqueWorkId"

object Workers {

    fun run() {
        run(null)
    }

    fun run(addressId: Long?) {
        BackgroundThreadExecutor.execute {
            WorkManager.getInstance()
                .beginUniqueWork(UNIQUE_WORK_ID, ExistingWorkPolicy.REPLACE, notifyStart())
                .then(prepareAddresses(addressId))
                .then(updateAddresses(addressId))
                .then(notifyEnd())
                .enqueue()
        }
    }

    private fun notifyStart(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<StartWorker>().build()
    }

    class StartWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            EventBus.getDefault().postSticky(WorkerEvents.Start())
            return Result.success()
        }
    }

    private fun prepareAddresses(addressId: Long?): OneTimeWorkRequest {
        return if (addressId == null) {
            OneTimeWorkRequestBuilder<PrepareAddressesWorker>().build()
        } else {
            val data = Data.Builder().putLong(ADDRESS_ID, addressId).build()
            OneTimeWorkRequestBuilder<PrepareAddressesWorker>().setInputData(data).build()
        }
    }

    class PrepareAddressesWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val addressId = inputData.getLong(ADDRESS_ID)
            if (addressId == null) {
                AddressRepository.updateAddresses(AddressRepository.loadAllAddresses().map { it.copyForReload() }, true)
            } else {
                AddressRepository.updateAddress(AddressRepository.loadAddress(addressId).copyForReload(), true)
            }
            return Result.success()
        }
    }

    private fun updateAddresses(addressId: Long?): List<OneTimeWorkRequest> {
        val addresses = if (addressId == null) AddressRepository.loadAllAddresses() else listOf(AddressRepository.loadAddress(addressId))
        return addresses.map {
            val data = Data.Builder().putLong(ADDRESS_ID, it.id).build()
            OneTimeWorkRequestBuilder<UpdateAddressWorker>().setInputData(data).build()
        }
    }

    class UpdateAddressWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            val address = AddressRepository.loadAddress(inputData.getLong(ADDRESS_ID)!!)

            val logIdentifier = if (Preferences.isLoggingEnabled()) UUID.randomUUID() else null
            if (logIdentifier != null) {
                LogRepository.insertLog("Check ${address.value.take(20)}...", LogCategory.CHECK, logIdentifier)
            }

            val addressResponse = BlockstreamRepository.lookupAddress(address.value)
            val updatedAddress = address.copyFromBlockstreamResponse(addressResponse)

            if (Preferences.isNotifyOnChange() && address.isUnchanged() && updatedAddress.isChanged()) {
                BackgroundThreadExecutor.execute {
                    val title = "Tx count on address ${address.mapping} changed"
                    val text = address.value
                    val image = R.drawable.bitcoin_logo
                    Notification.notifyStatusChange(title, text, image)
                    if(Preferences.isLoggingEnabled()) {
                        LogRepository.insertLogAsync("Notified address change ${address.value.take(20)}...", LogCategory.NOTIFY)
                    }
                }
            }

            AddressRepository.updateAddress(updatedAddress, true)

            if (logIdentifier != null) {
                val log = LogRepository.loadLog(logIdentifier)
                val duration = log.timestamp.until(LocalDateTime.now(), ChronoUnit.MILLIS)
                val logResult = if (addressResponse.isSuccessful()) " success ($duration ms)" else " failed ($duration ms)\n${addressResponse.failureText()}"
                LogRepository.updateLog(log.copy(message = log.message + logResult))
            }

            return Result.success()
        }
    }

    private fun notifyEnd(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<EndWorker>().build()
    }

    class EndWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
        override fun doWork(): Result {
            EventBus.getDefault().removeStickyEvent(WorkerEvents.Start::class.java)
            EventBus.getDefault().postSticky(WorkerEvents.End())
            return Result.success()
        }
    }
}