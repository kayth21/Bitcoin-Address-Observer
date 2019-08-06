package com.ceaver.bao.database

import androidx.room.TypeConverter
import com.ceaver.bao.network.SyncStatus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class Converters {

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): Long? = localDateTime?.let { ChronoUnit.SECONDS.between(LocalDateTime.MIN, localDateTime) }

    @TypeConverter
    fun toLocalDateTime(long: Long?): LocalDateTime? = long?.let { LocalDateTime.MIN.plusSeconds(it) }

    @TypeConverter
    fun fromSyncStatus(syncStatus: SyncStatus?): String? = syncStatus?.name

    @TypeConverter
    fun toSyncStatus(string: String?): SyncStatus? = string?.let { SyncStatus.valueOf(it) }

}