package com.ceaver.bao.addresses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ceaver.bao.blockstream.BlockstreamAddress
import com.ceaver.bao.network.Response
import com.ceaver.bao.network.SyncStatus
import java.time.LocalDateTime

@Entity(tableName = "address")
data class Address(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "value") val value: String,
    @ColumnInfo(name = "mapping") val mapping: String? = null,
    //
    @ColumnInfo(name = "last_sync_status") val lastSyncStatus: SyncStatus? = null,
    @ColumnInfo(name = "last_sync_date") val lastSyncDate: LocalDateTime? = null,
    @ColumnInfo(name = "failure_message") val failureMessage: String? = null,
    //
    @ColumnInfo(name = "initial_transaction_count") val initialTransactionCount: Int? = null,
    @ColumnInfo(name = "current_transaction_count") val currentTransactionCount: Int? = null,
    @ColumnInfo(name = "balance") val balance: Long? = null
) {

    fun addressStatus(): AddressStatus {
        return when {
            failureMessage != null && lastSyncStatus == SyncStatus.NORMAL -> AddressStatus.EXCEPTION
            initialTransactionCount == null -> AddressStatus.UNKNOWN
            initialTransactionCount != currentTransactionCount -> AddressStatus.CHANGED
            else -> AddressStatus.UNCHANGED
        }
    }

    fun copyForReload(): Address {
        return copy(
            lastSyncStatus = SyncStatus.LOADING,
            lastSyncDate = LocalDateTime.now()
        )
    }

    fun copyFromBlockstreamResponse(addressResponse: Response<BlockstreamAddress>): Address {
        return if (addressResponse.isSuccessful()) {
            val result = addressResponse.result!!
            copy(
                lastSyncStatus = SyncStatus.NORMAL,
                lastSyncDate = LocalDateTime.now(),
                failureMessage = null,
                //
                initialTransactionCount = initialTransactionCount ?: result.chainStats.txCount,
                currentTransactionCount = result.chainStats.txCount,
                balance = result.chainStats.fundedTxoSum - result.chainStats.spentTxoSum
            )
        } else {
            copy(
                lastSyncStatus = if (addressResponse.isError()) SyncStatus.ERROR else SyncStatus.NORMAL,
                lastSyncDate = LocalDateTime.now(),
                failureMessage = addressResponse.failureText()
            )
        }
    }

    fun copyForReset(): Address {
        return copy(initialTransactionCount = currentTransactionCount)
    }

    fun isUnchanged(): Boolean = addressStatus() == AddressStatus.UNCHANGED
    fun isChanged(): Boolean = addressStatus() == AddressStatus.CHANGED
}