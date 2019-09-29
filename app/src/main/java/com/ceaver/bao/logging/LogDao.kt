package com.ceaver.bao.logging

import androidx.room.*
import java.util.*

@Dao
interface LogDao {
    @Query("select * from log")
    fun loadAllLogs(): List<Log>

    @Query("select * from log where id = :id")
    fun loadLog(id: Long): Log

    @Query("select * from log where identifier = :identifier")
    fun loadLog(identifier: UUID): Log

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertLog(log: Log)

    @Update
    fun updateLog(log: Log)

    @Delete
    fun deleteLog(log: Log)

    @Query("delete from log")
    fun deleteAllLogs()

}