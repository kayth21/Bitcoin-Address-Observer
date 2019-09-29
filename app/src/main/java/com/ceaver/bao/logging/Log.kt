package com.ceaver.bao.logging

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "log")
data class Log(//
        @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
        @ColumnInfo(name = "identifier") val identifier: UUID = UUID.randomUUID(),
        @ColumnInfo(name = "timestamp") val timestamp: LocalDateTime = LocalDateTime.now(),
        @ColumnInfo(name = "message") val message: String,
        @ColumnInfo(name = "category") val category: LogCategory
)